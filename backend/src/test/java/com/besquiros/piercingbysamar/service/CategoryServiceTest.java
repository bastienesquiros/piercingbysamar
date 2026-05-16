package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.response.CategoryResponse;
import com.besquiros.piercingbysamar.entity.Category;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.CategoryMapper;
import com.besquiros.piercingbysamar.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock CategoryRepository categoryRepository;
    @Mock CategoryMapper categoryMapper;
    @InjectMocks CategoryService categoryService;

    @Test
    void getRootCategoriesWithChildren_shouldReturnMappedList() {
        Category cat = Category.builder().id(1L).name("Oreille").slug("oreille").build();
        CategoryResponse response = new CategoryResponse(1L, null, "Oreille", "oreille", null, List.of());

        when(categoryRepository.findRootCategoriesWithChildren()).thenReturn(List.of(cat));
        when(categoryMapper.toResponseList(List.of(cat))).thenReturn(List.of(response));

        List<CategoryResponse> result = categoryService.getRootCategoriesWithChildren();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Oreille");
        verify(categoryRepository).findRootCategoriesWithChildren();
    }

    @Test
    void getBySlug_whenFound_shouldReturnResponse() {
        Category cat = Category.builder().id(1L).name("Hélix").slug("helix").build();
        CategoryResponse response = new CategoryResponse(1L, null, "Hélix", "helix", null, List.of());

        when(categoryRepository.findBySlug("helix")).thenReturn(Optional.of(cat));
        when(categoryMapper.toResponse(cat)).thenReturn(response);

        CategoryResponse result = categoryService.getBySlug("helix");

        assertThat(result.slug()).isEqualTo("helix");
    }

    @Test
    void getBySlug_whenNotFound_shouldThrowNotFoundException() {
        when(categoryRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getBySlug("unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("unknown");
    }
}
