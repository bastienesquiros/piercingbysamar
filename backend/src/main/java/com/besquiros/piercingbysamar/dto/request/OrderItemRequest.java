package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull Long variantId,
        @NotNull @Min(1) Integer quantity
) {}
