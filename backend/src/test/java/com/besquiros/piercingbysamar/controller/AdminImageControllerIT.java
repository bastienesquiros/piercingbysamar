package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.response.ProductImageResponse;
import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.ProductImage;
import com.besquiros.piercingbysamar.exception.GlobalExceptionHandler;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.repository.ProductImageRepository;
import com.besquiros.piercingbysamar.repository.ProductRepository;
import com.besquiros.piercingbysamar.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminImageControllerIT {

    @Mock ProductRepository productRepository;
    @Mock ProductImageRepository imageRepository;
    @Mock StorageService storageService;
    @InjectMocks AdminImageController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void upload_withValidFile_shouldReturn200WithImageResponse() throws Exception {
        Product product = Product.builder().id(1L).name("Anneau").build();
        ProductImage savedImage = ProductImage.builder()
                .id(10L)
                .product(product)
                .r2Url("https://eu2.contabostorage.com/piercingbysamar/products/1/uuid.jpg")
                .altText("Anneau titanium")
                .position(0)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(storageService.upload(any(), anyString()))
                .thenReturn("https://eu2.contabostorage.com/piercingbysamar/products/1/uuid.jpg");
        when(imageRepository.save(any())).thenReturn(savedImage);

        MockMultipartFile file = new MockMultipartFile(
                "file", "anneau.jpg", "image/jpeg", new byte[1024]);

        mockMvc.perform(multipart("/api/admin/products/1/images")
                        .file(file)
                        .param("altText", "Anneau titanium")
                        .param("position", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.r2Url").value("https://eu2.contabostorage.com/piercingbysamar/products/1/uuid.jpg"));
    }

    @Test
    void upload_withUnknownProduct_shouldReturn404() throws Exception {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "file", "anneau.jpg", "image/jpeg", new byte[1024]);

        mockMvc.perform(multipart("/api/admin/products/99/images")
                        .file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_withValidImage_shouldReturn204() throws Exception {
        ProductImage image = ProductImage.builder()
                .id(10L)
                .r2Url("https://eu2.contabostorage.com/piercingbysamar/products/1/uuid.jpg")
                .build();

        when(imageRepository.findByIdAndProductId(10L, 1L)).thenReturn(Optional.of(image));
        doNothing().when(storageService).delete(anyString());
        doNothing().when(imageRepository).delete(image);

        mockMvc.perform(delete("/api/admin/products/1/images/10"))
                .andExpect(status().isNoContent());

        verify(storageService).delete("https://eu2.contabostorage.com/piercingbysamar/products/1/uuid.jpg");
        verify(imageRepository).delete(image);
    }

    @Test
    void delete_withUnknownImage_shouldReturn404() throws Exception {
        when(imageRepository.findByIdAndProductId(99L, 1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/products/1/images/99"))
                .andExpect(status().isNotFound());
    }
}
