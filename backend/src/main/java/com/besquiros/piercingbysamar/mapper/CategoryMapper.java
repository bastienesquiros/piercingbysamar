package com.besquiros.piercingbysamar.mapper;

import com.besquiros.piercingbysamar.dto.response.CategoryResponse;
import com.besquiros.piercingbysamar.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        List<CategoryResponse> children = category.getChildren().stream()
                .filter(Category::isActive)
                .map(this::toResponse)
                .toList();

        return new CategoryResponse(
                category.getId(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                children
        );
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream().map(this::toResponse).toList();
    }
}
