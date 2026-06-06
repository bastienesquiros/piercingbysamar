package com.besquiros.piercingbysamar.dto.response;

public record ProductVariantResponse(
        Long id,
        String sku,
        String size,
        String color,
        Integer priceCents,
        Integer stock,
        Integer reservedStock,
        Integer availableStock,
        boolean inStock,
        boolean active
) {}
