package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.request.CreateProductRequest;
import com.besquiros.piercingbysamar.dto.request.CreateVariantRequest;
import com.besquiros.piercingbysamar.dto.request.UpdateProductRequest;
import com.besquiros.piercingbysamar.dto.response.ProductDetailResponse;
import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.ProductVariant;
import com.besquiros.piercingbysamar.entity.enums.Material;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.ProductMapper;
import com.besquiros.piercingbysamar.repository.CategoryRepository;
import com.besquiros.piercingbysamar.repository.ProductRepository;
import com.besquiros.piercingbysamar.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock ProductMapper productMapper;
    @InjectMocks ProductService productService;

    private Product product;
    private ProductDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Anneau Titanium")
                .slug("anneau-titanium")
                .material(Material.TITANIUM)
                .nickelFree(true)
                .active(true)
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();

        detailResponse = new ProductDetailResponse(
                1L, "Anneau Titanium", "anneau-titanium", null,
                "TITANIUM", true, null, null, null, null,
                true, List.of(), List.of(), List.of(), null, null);
    }

    @Test
    void getBySlug_whenFound_shouldReturnDetail() {
        when(productRepository.findBySlugAndActiveTrue("anneau-titanium")).thenReturn(Optional.of(product));
        when(productMapper.toDetail(product)).thenReturn(detailResponse);

        ProductDetailResponse result = productService.getBySlug("anneau-titanium");

        assertThat(result.slug()).isEqualTo("anneau-titanium");
        assertThat(result.material()).isEqualTo("TITANIUM");
    }

    @Test
    void getBySlug_whenNotFound_shouldThrowNotFoundException() {
        when(productRepository.findBySlugAndActiveTrue("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getBySlug("unknown"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_withoutSlug_shouldAutoGenerateSlug() {
        CreateProductRequest request = new CreateProductRequest(
                "Anneau Segment Titanium", null, null, "TITANIUM", true, null, null, null);

        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDetail(any())).thenReturn(detailResponse);

        productService.create(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getSlug()).isEqualTo("anneau-segment-titanium");
    }

    @Test
    void create_withExplicitSlug_shouldUseProvidedSlug() {
        CreateProductRequest request = new CreateProductRequest(
                "Anneau Segment", "mon-slug-custom", null, "TITANIUM", false, null, null, null);

        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDetail(any())).thenReturn(detailResponse);

        productService.create(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getSlug()).isEqualTo("mon-slug-custom");
    }

    @Test
    void update_shouldPatchOnlyProvidedFields() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Nouveau Nom", null, null, null, null, null, null, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDetail(any())).thenReturn(detailResponse);

        productService.update(1L, request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Nouveau Nom");
        assertThat(captor.getValue().getMaterial()).isEqualTo(Material.TITANIUM); // inchangé
    }

    @Test
    void update_whenProductNotFound_shouldThrowNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, new UpdateProductRequest(null, null, null, null, null, null, null, null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        productService.deactivate(1L);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    void addVariant_whenSkuAlreadyExists_shouldThrowBadRequestException() {
        CreateVariantRequest request = new CreateVariantRequest("EXISTING-SKU", "6mm", "Gold", 2490, 10);
        when(variantRepository.existsBySku("EXISTING-SKU")).thenReturn(true);

        assertThatThrownBy(() -> productService.addVariant(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("EXISTING-SKU");
    }

    @Test
    void addVariant_whenSkuIsNew_shouldAddAndReturnDetail() {
        CreateVariantRequest request = new CreateVariantRequest("NEW-SKU", "6mm", "Gold", 2490, 10);

        when(variantRepository.existsBySku("NEW-SKU")).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(variantRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productMapper.toDetail(product)).thenReturn(detailResponse);

        productService.addVariant(1L, request);

        verify(variantRepository).save(any(ProductVariant.class));
    }

    @Test
    void deleteVariant_whenVariantNotBelongsToProduct_shouldThrowBadRequest() {
        ProductVariant variant = ProductVariant.builder()
                .id(10L)
                .product(Product.builder().id(99L).build()) // produit différent
                .build();

        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> productService.deleteVariant(1L, 10L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteVariant_whenVariantBelongsToProduct_shouldDelete() {
        ProductVariant variant = ProductVariant.builder()
                .id(10L)
                .product(product)
                .build();

        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        productService.deleteVariant(1L, 10L);

        verify(variantRepository).delete(variant);
    }
}
