package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StripeCheckoutRequest(
        @NotBlank String orderReference
) {}
