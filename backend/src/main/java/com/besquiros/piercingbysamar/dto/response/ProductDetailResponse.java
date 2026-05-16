package com.besquiros.piercingbysamar.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/** Réponse complète pour la page détail produit */
public record ProductDetailResponse(
        Long id,
        String name,
        String slug,
        String description,
        String material,
        boolean nickelFree,
        Long categoryId,
        String categoryName,
        String metaTitle,
        String metaDescription,
        boolean active,
        List<ProductVariantResponse> variants,
        List<ProductImageResponse> images,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
