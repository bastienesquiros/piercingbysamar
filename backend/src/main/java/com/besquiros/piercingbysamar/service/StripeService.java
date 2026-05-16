package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.response.StripeCheckoutResponse;
import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.entity.enums.OrderType;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.repository.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    // Taux de secours si le service de change est indisponible
    private static final double FALLBACK_MAD_TO_EUR = 0.0926;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    private final OrderRepository orderRepository;
    private final MailService mailService;
    private final ExchangeRateService exchangeRateService;

    // Devises supportées par Stripe pour le paiement direct
    private static final java.util.Set<String> STRIPE_SUPPORTED = java.util.Set.of("EUR", "USD", "GBP", "CAD", "CHF");

    @Transactional
    public StripeCheckoutResponse createCheckoutSession(String orderReference, String displayCurrency) {
        Order order = orderRepository.findByReference(orderReference)
                .orElseThrow(() -> new NotFoundException("Commande introuvable : " + orderReference));

        if (order.getOrderType() == OrderType.CLICK_COLLECT) {
            throw new BadRequestException("Le paiement Stripe n'est pas disponible pour le Click & Collect");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BadRequestException("Cette commande est déjà payée");
        }

        // Si la devise affichée est supportée par Stripe, on charge directement dedans.
        // MAD n'est pas supporté par Stripe → fallback EUR.
        final String stripeCurrency;
        final boolean directCharge;
        if (displayCurrency != null && STRIPE_SUPPORTED.contains(displayCurrency.toUpperCase())) {
            stripeCurrency = displayCurrency.toLowerCase();
            directCharge = true;
        } else {
            stripeCurrency = "eur";
            directCharge = false;
        }

        List<SessionCreateParams.LineItem> lineItems = order.getItems().stream()
                .map(item -> SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getQuantity())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(stripeCurrency)
                                .setUnitAmount(directCharge
                                        ? madCentimesToCurrencyCents(item.getUnitPriceCents(), stripeCurrency)
                                        : madCentimesToEurCents(item.getUnitPriceCents()))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getSnapshotProductName())
                                        .setDescription(item.getSnapshotVariantLabel())
                                        .build())
                                .build())
                        .build())
                .toList();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?ref=" + orderReference)
                    .setCancelUrl(cancelUrl)
                    .addAllLineItem(lineItems)
                    .setCustomerEmail(order.getCustomerEmail())
                    .putMetadata("order_reference", orderReference)
                    .build();

            Session session = Session.create(params);

            order.setStripeSessionId(session.getId());
            orderRepository.save(order);

            log.info("Stripe Checkout Session créée : {} pour commande {}", session.getId(), orderReference);
            return new StripeCheckoutResponse(session.getUrl());

        } catch (StripeException e) {
            log.error("Erreur Stripe lors de la création de la session pour {}", orderReference, e);
            throw new RuntimeException("Erreur Stripe : " + e.getMessage(), e);
        }
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Signature webhook Stripe invalide");
            throw new BadRequestException("Signature webhook invalide");
        }

        log.info("Webhook Stripe reçu : {}", event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new RuntimeException("Impossible de désérialiser l'événement Stripe"));

            String orderReference = session.getMetadata().get("order_reference");
            if (orderReference == null) {
                log.warn("Webhook reçu sans order_reference dans les métadonnées");
                return;
            }

            orderRepository.findByReference(orderReference).ifPresentOrElse(order -> {
                order.setStatus(OrderStatus.PAID);
                order.setStripePaymentIntentId(session.getPaymentIntent());
                Order saved = orderRepository.save(order);
                mailService.sendPaymentConfirmation(saved);
                log.info("Commande {} marquée comme PAID via Stripe", orderReference);
            }, () -> log.warn("Commande introuvable pour le webhook Stripe : {}", orderReference));
        }
    }

    /** Convertit des centimes MAD dans la devise cible (EUR ou USD). */
    private long madCentimesToCurrencyCents(int madCentimes, String targetCurrency) {
        double rate = switch (targetCurrency.toLowerCase()) {
            case "usd" -> exchangeRateService != null ? exchangeRateService.getMadToUsd() : 0.099;
            default    -> exchangeRateService != null ? exchangeRateService.getMadToEur() : FALLBACK_MAD_TO_EUR;
        };
        return Math.max(1L, Math.round(madCentimes * rate));
    }

    /** Convertit des centimes MAD en centimes EUR pour Stripe (MAD non supporté). */
    private long madCentimesToEurCents(int madCentimes) {
        double rate = exchangeRateService != null
                ? exchangeRateService.getMadToEur()
                : FALLBACK_MAD_TO_EUR;
        return Math.max(1L, Math.round(madCentimes * rate));
    }
}
