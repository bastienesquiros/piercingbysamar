package com.besquiros.piercingbysamar.dto.request;

public record UpdateProductRequest(
        String name,
        String description,
        String material,
        Boolean nickelFree,
        Long categoryId,
        String metaTitle,
        String metaDescription,
        Boolean active
) {}
