package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.response.PageResponse;
import com.besquiros.piercingbysamar.dto.response.ProductDetailResponse;
import com.besquiros.piercingbysamar.dto.response.ProductSummaryResponse;
import com.besquiros.piercingbysamar.exception.GlobalExceptionHandler;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerIT {

    @Mock ProductService productService;
    @InjectMocks ProductController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ProductSummaryResponse summaryResponse() {
        return new ProductSummaryResponse(1L, "Anneau Titanium", "anneau-titanium",
                "TITANIUM", true, 7L, "Hélix", 2490, 2890, true, null, List.of("Titane"));
    }

    private ProductDetailResponse detailResponse() {
        return new ProductDetailResponse(1L, "Anneau Titanium", "anneau-titanium",
                "Description", "TITANIUM", true, 7L, "Hélix",
                "Meta title", "Meta desc", true, List.of(), List.of(), List.of(), null, null);
    }

    @Test
    void getAll_withoutFilters_shouldReturn200WithPage() throws Exception {
        PageResponse<ProductSummaryResponse> page = new PageResponse<>(List.of(summaryResponse()), 0, 12, 1, 1, true);
        when(productService.getAll(0, 12)).thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("anneau-titanium"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAll_withFilters_shouldCallFilteredMethod() throws Exception {
        PageResponse<ProductSummaryResponse> page = new PageResponse<>(List.of(summaryResponse()), 0, 12, 1, 1, true);
        when(productService.getWithFilters(eq(7L), eq("TITANIUM"), isNull(), eq(0), eq(12))).thenReturn(page);

        mockMvc.perform(get("/api/products").param("categoryId", "7").param("material", "TITANIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].material").value("TITANIUM"));

        verify(productService).getWithFilters(7L, "TITANIUM", null, 0, 12);
    }

    @Test
    void search_shouldReturn200WithResults() throws Exception {
        PageResponse<ProductSummaryResponse> page = new PageResponse<>(List.of(summaryResponse()), 0, 12, 1, 1, true);
        when(productService.search("titanium", 0, 12)).thenReturn(page);

        mockMvc.perform(get("/api/products/search").param("q", "titanium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Anneau Titanium"));
    }

    @Test
    void getBySlug_whenFound_shouldReturn200WithDetail() throws Exception {
        when(productService.getBySlug("anneau-titanium")).thenReturn(detailResponse());

        mockMvc.perform(get("/api/products/anneau-titanium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anneau Titanium"))
                .andExpect(jsonPath("$.nickelFree").value(true));
    }

    @Test
    void getBySlug_whenNotFound_shouldReturn404() throws Exception {
        when(productService.getBySlug("unknown")).thenThrow(new NotFoundException("Produit introuvable : unknown"));

        mockMvc.perform(get("/api/products/unknown"))
                .andExpect(status().isNotFound());
    }
}
