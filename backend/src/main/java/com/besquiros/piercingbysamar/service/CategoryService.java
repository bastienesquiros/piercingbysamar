package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.response.CategoryResponse;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.CategoryMapper;
import com.besquiros.piercingbysamar.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getRootCategoriesWithChildren() {
        return categoryMapper.toResponseList(categoryRepository.findRootCategoriesWithChildren());
    }

    public CategoryResponse getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + slug));
    }
}
