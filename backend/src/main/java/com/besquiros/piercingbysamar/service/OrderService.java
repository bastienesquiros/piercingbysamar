package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.request.CreateOrderRequest;
import com.besquiros.piercingbysamar.dto.response.OrderResponse;
import com.besquiros.piercingbysamar.dto.response.PageResponse;
import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.OrderItem;
import com.besquiros.piercingbysamar.entity.ProductVariant;
import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.entity.enums.OrderType;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.OrderMapper;
import com.besquiros.piercingbysamar.repository.OrderRepository;
import com.besquiros.piercingbysamar.repository.ProductVariantRepository;
import com.besquiros.piercingbysamar.util.OrderReferenceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final OrderMapper orderMapper;
    private final MailService mailService;

    public OrderResponse getByReference(String reference) {
        return orderRepository.findByReference(reference)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Commande introuvable : " + reference));
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        OrderType orderType = OrderType.valueOf(request.orderType().toUpperCase());

        if (orderType == OrderType.SHIPPING) {
            if (request.shippingAddress() == null || request.shippingCity() == null
                    || request.shippingPostalCode() == null || request.shippingCountry() == null) {
                throw new BadRequestException("L'adresse de livraison est obligatoire pour une commande en livraison");
            }
        }

        List<OrderItem> items = new ArrayList<>();
        int subtotal = 0;

        for (var itemReq : request.items()) {
            ProductVariant variant = variantRepository.findById(itemReq.variantId())
                    .orElseThrow(() -> new NotFoundException("Variante introuvable : " + itemReq.variantId()));

            if (!variant.isActive()) throw new BadRequestException("Variante inactive : " + variant.getSku());
            if (!variant.isInStock()) throw new BadRequestException("Variante en rupture de stock : " + variant.getSku());
            if (variant.getStock() < itemReq.quantity()) {
                throw new BadRequestException("Stock insuffisant pour : " + variant.getSku());
            }

            int lineTotal = variant.getPriceCents() * itemReq.quantity();
            subtotal += lineTotal;

            items.add(OrderItem.builder()
                    .productVariant(variant)
                    .snapshotProductName(variant.getProduct().getName())
                    .snapshotVariantLabel(buildVariantLabel(variant))
                    .unitPriceCents(variant.getPriceCents())
                    .quantity(itemReq.quantity())
                    .totalCents(lineTotal)
                    .build());
        }

        String reference = OrderReferenceUtils.generate(orderRepository.count());

        Order order = Order.builder()
                .reference(reference)
                .orderType(orderType)
                .status(orderType == OrderType.CLICK_COLLECT ? OrderStatus.CLICK_COLLECT_PENDING : OrderStatus.PENDING)
                .customerEmail(request.customerEmail())
                .customerName(request.customerName())
                .customerPhone(request.customerPhone())
                .shippingAddress(request.shippingAddress())
                .shippingCity(request.shippingCity())
                .shippingPostalCode(request.shippingPostalCode())
                .shippingCountry(request.shippingCountry())
                .subtotalCents(subtotal)
                .shippingCostCents(0)
                .totalCents(subtotal)
                .currency(request.currency() != null ? request.currency() : "EUR")
                .notes(request.notes())
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));
        Order saved = orderRepository.save(order);
        mailService.sendOrderConfirmation(saved);
        mailService.sendAdminNewOrder(saved);
        return orderMapper.toResponse(saved);
    }

    // ── Admin ─────────────────────────────────────────────────

    public PageResponse<OrderResponse> getAll(int page, int size, OrderStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> result = status != null
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);
        return toPageResponse(result);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande introuvable : " + id));
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        if (newStatus == OrderStatus.PAID) {
            decrementStock(saved);
        } else if (newStatus == OrderStatus.READY) {
            decrementStock(saved);
            mailService.sendClickCollectReady(saved);
        } else if (newStatus == OrderStatus.CANCELLED) {
            reinstateStock(saved);
            mailService.sendCancellation(saved);
        } else if (newStatus == OrderStatus.SHIPPED) {
            mailService.sendShippingNotification(saved);
        } else if (newStatus == OrderStatus.DELIVERED) {
            mailService.sendReviewRequest(saved);
        } else if (newStatus == OrderStatus.COLLECTED) {
            mailService.sendCollected(saved);
            mailService.sendReviewRequest(saved);
        }

        return orderMapper.toResponse(saved);
    }

    private void decrementStock(Order order) {
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStock(Math.max(0, variant.getStock() - item.getQuantity()));
            variantRepository.save(variant);
        }
    }

    private void reinstateStock(Order order) {
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStock(variant.getStock() + item.getQuantity());
            variantRepository.save(variant);
        }
    }

    private String buildVariantLabel(ProductVariant variant) {
        StringBuilder label = new StringBuilder();
        if (variant.getSize() != null) label.append(variant.getSize());
        if (variant.getColor() != null) {
            if (!label.isEmpty()) label.append(" - ");
            label.append(variant.getColor());
        }
        return label.toString();
    }

    private PageResponse<OrderResponse> toPageResponse(Page<Order> page) {
        return new PageResponse<>(
                page.getContent().stream().map(orderMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
