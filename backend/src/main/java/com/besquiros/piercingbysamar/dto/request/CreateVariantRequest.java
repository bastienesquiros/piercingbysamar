package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateVariantRequest(
        @NotBlank String sku,
        @NotBlank String size,
        @NotBlank String color,
        @NotNull @Positive Integer priceCents,
        @NotNull Integer stock
) {}
