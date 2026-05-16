package com.besquiros.piercingbysamar.dto.response;

import java.util.List;

/** Réponse allégée pour les listes (catalogue, recherche) */
public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        String material,
        boolean nickelFree,
        Long categoryId,
        String categoryName,
        Integer minPriceCents,
        Integer maxPriceCents,
        boolean inStock,
        String coverImageUrl,
        List<String> tags
) {}
