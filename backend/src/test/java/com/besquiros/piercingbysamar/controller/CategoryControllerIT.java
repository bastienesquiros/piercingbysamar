package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.response.CategoryResponse;
import com.besquiros.piercingbysamar.exception.GlobalExceptionHandler;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerIT {

    @Mock CategoryService categoryService;
    @InjectMocks CategoryController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_shouldReturn200WithCategoryList() throws Exception {
        CategoryResponse cat = new CategoryResponse(1L, null, "Oreille", "oreille", null, null, List.of());
        when(categoryService.getRootCategoriesWithChildren()).thenReturn(List.of(cat));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Oreille"))
                .andExpect(jsonPath("$[0].slug").value("oreille"));
    }

    @Test
    void getBySlug_whenFound_shouldReturn200() throws Exception {
        CategoryResponse cat = new CategoryResponse(7L, 1L, "Hélix", "helix", null, null, List.of());
        when(categoryService.getBySlug("helix")).thenReturn(cat);

        mockMvc.perform(get("/api/categories/helix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hélix"));
    }

    @Test
    void getBySlug_whenNotFound_shouldReturn404() throws Exception {
        when(categoryService.getBySlug("unknown")).thenThrow(new NotFoundException("Catégorie introuvable : unknown"));

        mockMvc.perform(get("/api/categories/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Catégorie introuvable : unknown"));
    }
}
