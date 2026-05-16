package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank String name,
        String slug,                // auto-généré si absent
        String description,
        String material,            // TITANIUM | STEEL | BIOFLEX | GOLD
        boolean nickelFree,
        Long categoryId,
        String metaTitle,
        String metaDescription
) {}
