package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * displayCurrency : devise affichée côté client (MAD, EUR, USD).
 * Stripe ne supporte pas MAD → on convertit toujours MAD→EUR dans ce cas.
 * Si le client voit EUR ou USD, on charge directement dans sa devise.
 */
public record StripeCheckoutRequest(
        @NotBlank String orderReference,
        String displayCurrency
) {}
