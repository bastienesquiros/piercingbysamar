package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.request.CreateOrderRequest;
import com.besquiros.piercingbysamar.dto.request.OrderItemRequest;
import com.besquiros.piercingbysamar.dto.response.OrderResponse;
import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.ProductVariant;
import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.entity.enums.OrderType;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.OrderMapper;
import com.besquiros.piercingbysamar.repository.OrderRepository;
import com.besquiros.piercingbysamar.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock OrderMapper orderMapper;
    @Mock MailService mailService;
    @InjectMocks OrderService orderService;

    private ProductVariant activeVariant;

    @BeforeEach
    void setUp() {
        activeVariant = ProductVariant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .priceCents(2490)
                .stock(10)
                .active(true)
                .product(Product.builder().id(1L).name("Anneau Titanium").build())
                .build();
    }

    @Test
    void getByReference_whenFound_shouldReturnResponse() {
        Order order = Order.builder().id(1L).reference("PBS-2026-0001").build();
        OrderResponse response = buildOrderResponse("PBS-2026-0001", "PENDING");

        when(orderRepository.findByReference("PBS-2026-0001")).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.getByReference("PBS-2026-0001");

        assertThat(result.reference()).isEqualTo("PBS-2026-0001");
    }

    @Test
    void getByReference_whenNotFound_shouldThrowNotFoundException() {
        when(orderRepository.findByReference("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getByReference("UNKNOWN"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shipping_withMissingAddress_shouldThrowBadRequest() {
        CreateOrderRequest request = new CreateOrderRequest(
                "SHIPPING", "test@test.com", "Test User", null,
                null, null, null, null, // adresses manquantes
                List.of(new OrderItemRequest(1L, 1)),
                "EUR", null);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("livraison");
    }

    @Test
    void create_shipping_withValidData_shouldCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                "SHIPPING", "client@test.com", "Client Test", "+33612345678",
                "12 rue Test", "Paris", "75001", "FR",
                List.of(new OrderItemRequest(1L, 2)),
                "EUR", null);

        Order saved = Order.builder().id(1L).reference("PBS-2026-0007").status(OrderStatus.PENDING).build();
        OrderResponse response = buildOrderResponse("PBS-2026-0007", "PENDING");

        when(variantRepository.findById(1L)).thenReturn(Optional.of(activeVariant));
        when(orderRepository.count()).thenReturn(6L);
        when(orderRepository.save(any())).thenReturn(saved);
        when(orderMapper.toResponse(saved)).thenReturn(response);

        OrderResponse result = orderService.create(request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order captured = captor.getValue();

        assertThat(captured.getOrderType()).isEqualTo(OrderType.SHIPPING);
        assertThat(captured.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(captured.getSubtotalCents()).isEqualTo(2490 * 2);
        assertThat(captured.getCustomerEmail()).isEqualTo("client@test.com");
        assertThat(result.reference()).isEqualTo("PBS-2026-0007");
    }

    @Test
    void create_clickCollect_shouldSetCorrectStatus() {
        CreateOrderRequest request = new CreateOrderRequest(
                "CLICK_COLLECT", "client@test.com", "Client Test", null,
                null, null, null, null,
                List.of(new OrderItemRequest(1L, 1)),
                "EUR", null);

        Order saved = Order.builder().id(1L).reference("PBS-2026-0008").status(OrderStatus.CLICK_COLLECT_PENDING).build();
        when(variantRepository.findById(1L)).thenReturn(Optional.of(activeVariant));
        when(orderRepository.count()).thenReturn(7L);
        when(orderRepository.save(any())).thenReturn(saved);
        when(orderMapper.toResponse(saved)).thenReturn(buildOrderResponse("PBS-2026-0008", "CLICK_COLLECT_PENDING"));

        orderService.create(request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.CLICK_COLLECT_PENDING);
    }

    @Test
    void create_withInactiveVariant_shouldThrowBadRequest() {
        activeVariant.setActive(false);
        CreateOrderRequest request = simpleShippingRequest();
        when(variantRepository.findById(1L)).thenReturn(Optional.of(activeVariant));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    void create_withOutOfStockVariant_shouldThrowBadRequest() {
        activeVariant.setStock(0);
        CreateOrderRequest request = simpleShippingRequest();
        when(variantRepository.findById(1L)).thenReturn(Optional.of(activeVariant));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("rupture");
    }

    @Test
    void create_withInsufficientStock_shouldThrowBadRequest() {
        activeVariant.setStock(1);
        CreateOrderRequest request = new CreateOrderRequest(
                "SHIPPING", "client@test.com", "Test", null,
                "Rue Test", "Paris", "75001", "FR",
                List.of(new OrderItemRequest(1L, 5)), // veut 5, stock = 1
                "EUR", null);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(activeVariant));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("insuffisant");
    }

    @Test
    void updateStatus_shouldChangeOrderStatus() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).items(List.of()).build();
        Order saved = Order.builder().id(1L).status(OrderStatus.PAID).items(List.of()).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(saved);
        when(orderMapper.toResponse(saved)).thenReturn(buildOrderResponse("PBS-2026-0001", "PAID"));

        OrderResponse result = orderService.updateStatus(1L, "PAID");

        assertThat(result.status()).isEqualTo("PAID");
    }

    @Test
    void updateStatus_whenOrderNotFound_shouldThrowNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(99L, "PAID"))
                .isInstanceOf(NotFoundException.class);
    }

    // ── Helpers ───────────────────────────────────────────────

    private CreateOrderRequest simpleShippingRequest() {
        return new CreateOrderRequest(
                "SHIPPING", "client@test.com", "Test", null,
                "Rue Test", "Paris", "75001", "FR",
                List.of(new OrderItemRequest(1L, 1)),
                "EUR", null);
    }

    private OrderResponse buildOrderResponse(String ref, String status) {
        return new OrderResponse(1L, ref, "SHIPPING", status, "client@test.com",
                "Test", null, null, null, null, null,
                2490, 0, 2490, "EUR", null, List.of(), null, null, null);
    }
}
