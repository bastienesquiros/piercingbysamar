package com.besquiros.piercingbysamar.dto.response;

public record OrderItemResponse(
        Long id,
        Long productVariantId,
        String snapshotProductName,
        String snapshotVariantLabel,
        Integer unitPriceCents,
        Integer quantity,
        Integer totalCents
) {}
