package com.besquiros.piercingbysamar.dto.response;

import java.util.List;

public record CategoryResponse(
        Long id,
        Long parentId,
        String name,
        String slug,
        String description,
        String imageUrl,
        List<CategoryResponse> children
) {}
