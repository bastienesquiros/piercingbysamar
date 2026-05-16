package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.CreateProductRequest;
import com.besquiros.piercingbysamar.dto.response.PageResponse;
import com.besquiros.piercingbysamar.dto.response.ProductDetailResponse;
import com.besquiros.piercingbysamar.dto.response.ProductSummaryResponse;
import com.besquiros.piercingbysamar.exception.GlobalExceptionHandler;
import com.besquiros.piercingbysamar.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminProductControllerIT {

    @Mock ProductService productService;
    @InjectMocks AdminProductController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ProductSummaryResponse summary() {
        return new ProductSummaryResponse(1L, "Anneau Titanium", "anneau-titanium",
                "TITANIUM", true, 7L, "Hélix", 2490, 2890, true, null, List.of());
    }

    private ProductDetailResponse detail() {
        return new ProductDetailResponse(1L, "Anneau Titanium", "anneau-titanium",
                "Desc", "TITANIUM", true, 7L, "Hélix",
                null, null, true, List.of(), List.of(), List.of(), null, null);
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        PageResponse<ProductSummaryResponse> page = new PageResponse<>(List.of(summary()), 0, 20, 1, 1, true);
        when(productService.getAllAdmin(0, 20)).thenReturn(page);

        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("anneau-titanium"));
    }

    @Test
    void create_withValidRequest_shouldReturn201() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Anneau Titanium", null, "Desc", "TITANIUM", true, 7L, null, null);

        when(productService.create(any())).thenReturn(detail());

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Anneau Titanium"));
    }

    @Test
    void create_withBlankName_shouldReturn400() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "", null, null, null, false, null, null, null);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deactivate_shouldReturn204() throws Exception {
        doNothing().when(productService).deactivate(1L);

        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isNoContent());
    }
}
