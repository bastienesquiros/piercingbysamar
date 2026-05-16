package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.response.StripeCheckoutResponse;
import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.OrderItem;
import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.entity.enums.OrderType;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.repository.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock MailService mailService;
    @InjectMocks StripeService stripeService;

    private Order shippingOrder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripeService, "webhookSecret", "whsec_test");
        ReflectionTestUtils.setField(stripeService, "successUrl", "http://localhost:3000/commande/succes");
        ReflectionTestUtils.setField(stripeService, "cancelUrl", "http://localhost:3000/panier");

        OrderItem item = OrderItem.builder()
                .snapshotProductName("Anneau Titanium")
                .snapshotVariantLabel("8mm - Silver")
                .unitPriceCents(2490)
                .quantity(1)
                .totalCents(2490)
                .build();

        shippingOrder = Order.builder()
                .id(1L)
                .reference("PBS-2026-0001")
                .orderType(OrderType.SHIPPING)
                .status(OrderStatus.PENDING)
                .customerEmail("client@test.com")
                .customerName("Client Test")
                .currency("EUR")
                .subtotalCents(2490)
                .totalCents(2490)
                .items(new ArrayList<>(List.of(item)))
                .build();
    }

    // ── createCheckoutSession ──────────────────────────────────

    @Test
    void createCheckoutSession_withValidOrder_shouldReturnCheckoutUrl() throws Exception {
        when(orderRepository.findByReference("PBS-2026-0001")).thenReturn(Optional.of(shippingOrder));

        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("cs_test_123");
        when(mockSession.getUrl()).thenReturn("https://checkout.stripe.com/pay/cs_test_123");

        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            sessionStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            StripeCheckoutResponse response = stripeService.createCheckoutSession("PBS-2026-0001", "EUR");

            assertThat(response.checkoutUrl()).isEqualTo("https://checkout.stripe.com/pay/cs_test_123");
            assertThat(shippingOrder.getStripeSessionId()).isEqualTo("cs_test_123");
            verify(orderRepository).save(shippingOrder);
        }
    }

    @Test
    void createCheckoutSession_withClickCollect_shouldThrowBadRequest() {
        Order cnc = Order.builder()
                .reference("PBS-2026-0002")
                .orderType(OrderType.CLICK_COLLECT)
                .status(OrderStatus.CLICK_COLLECT_PENDING)
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findByReference("PBS-2026-0002")).thenReturn(Optional.of(cnc));

        assertThatThrownBy(() -> stripeService.createCheckoutSession("PBS-2026-0002", "EUR"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Click & Collect");
    }

    @Test
    void createCheckoutSession_withAlreadyPaidOrder_shouldThrowBadRequest() {
        shippingOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findByReference("PBS-2026-0001")).thenReturn(Optional.of(shippingOrder));

        assertThatThrownBy(() -> stripeService.createCheckoutSession("PBS-2026-0001", "EUR"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("déjà payée");
    }

    @Test
    void createCheckoutSession_withUnknownReference_shouldThrowNotFound() {
        when(orderRepository.findByReference("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stripeService.createCheckoutSession("UNKNOWN", "EUR"))
                .isInstanceOf(NotFoundException.class);
    }

    // ── handleWebhook ──────────────────────────────────────────

    @Test
    void handleWebhook_withCheckoutCompleted_shouldMarkOrderAsPaid() throws Exception {
        when(orderRepository.findByReference("PBS-2026-0001")).thenReturn(Optional.of(shippingOrder));

        Event mockEvent = mock(Event.class);
        EventDataObjectDeserializer mockDeserializer = mock(EventDataObjectDeserializer.class);
        Session mockSession = mock(Session.class);

        when(mockEvent.getType()).thenReturn("checkout.session.completed");
        when(mockEvent.getDataObjectDeserializer()).thenReturn(mockDeserializer);
        when(mockDeserializer.getObject()).thenReturn(Optional.of(mockSession));
        when(mockSession.getMetadata()).thenReturn(Map.of("order_reference", "PBS-2026-0001"));
        when(mockSession.getPaymentIntent()).thenReturn("pi_test_123");

        try (MockedStatic<Webhook> webhookStatic = mockStatic(Webhook.class)) {
            webhookStatic.when(() -> Webhook.constructEvent("payload", "sig", "whsec_test"))
                    .thenReturn(mockEvent);

            stripeService.handleWebhook("payload", "sig");

            assertThat(shippingOrder.getStatus()).isEqualTo(OrderStatus.PAID);
            assertThat(shippingOrder.getStripePaymentIntentId()).isEqualTo("pi_test_123");
            verify(orderRepository).save(shippingOrder);
        }
    }

    @Test
    void handleWebhook_withOtherEventType_shouldDoNothing() throws Exception {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getType()).thenReturn("payment_intent.created");

        try (MockedStatic<Webhook> webhookStatic = mockStatic(Webhook.class)) {
            webhookStatic.when(() -> Webhook.constructEvent(any(), any(), any()))
                    .thenReturn(mockEvent);

            stripeService.handleWebhook("payload", "sig");

            verify(orderRepository, never()).save(any());
        }
    }

    @Test
    void handleWebhook_withInvalidSignature_shouldThrowBadRequest() {
        try (MockedStatic<Webhook> webhookStatic = mockStatic(Webhook.class)) {
            webhookStatic.when(() -> Webhook.constructEvent(any(), any(), any()))
                    .thenThrow(new SignatureVerificationException("Invalid signature", "sig"));

            assertThatThrownBy(() -> stripeService.handleWebhook("bad_payload", "bad_sig"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Signature webhook invalide");
        }
    }
}
