package com.besquiros.piercingbysamar.mapper;

import com.besquiros.piercingbysamar.dto.response.OrderItemResponse;
import com.besquiros.piercingbysamar.dto.response.OrderResponse;
import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductVariant() != null ? item.getProductVariant().getId() : null,
                item.getSnapshotProductName(),
                item.getSnapshotVariantLabel(),
                item.getUnitPriceCents(),
                item.getQuantity(),
                item.getTotalCents()
        );
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getReference(),
                order.getOrderType().name(),
                order.getStatus().name(),
                order.getCustomerEmail(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                order.getSubtotalCents(),
                order.getShippingCostCents(),
                order.getTotalCents(),
                order.getCurrency(),
                order.getNotes(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
