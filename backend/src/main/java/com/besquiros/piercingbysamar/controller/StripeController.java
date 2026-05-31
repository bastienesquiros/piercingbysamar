package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.StripeCheckoutRequest;
import com.besquiros.piercingbysamar.dto.response.StripeCheckoutResponse;
import com.besquiros.piercingbysamar.service.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeController {

    @Value("${app.stripe.enabled:true}")
    private boolean stripeEnabled;

    private final StripeService stripeService;

    /**
     * Crée une Stripe Checkout Session pour une commande PENDING.
     * Retourne l'URL de redirection vers Stripe.
     */
    @PostMapping("/checkout")
    public ResponseEntity<StripeCheckoutResponse> createCheckout(
            @Valid @RequestBody StripeCheckoutRequest request) {
        if (!stripeEnabled) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok(stripeService.createCheckoutSession(
                request.orderReference(),
                request.displayCurrency()));
    }

    /**
     * Webhook Stripe — appelé par Stripe après paiement.
     * Le body doit être lu brut (non parsé) pour que la vérification de signature fonctionne.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        if (!stripeEnabled) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        stripeService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
