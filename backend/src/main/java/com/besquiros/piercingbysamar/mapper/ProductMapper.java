package com.besquiros.piercingbysamar.mapper;

import com.besquiros.piercingbysamar.dto.response.*;
import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.ProductVariant;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductMapper {

    public ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getSku(),
                variant.getSize(),
                variant.getColor(),
                variant.getPriceCents(),
                variant.getStock(),
                variant.getReservedStock(),
                variant.getAvailableStock(),
                variant.isInStock(),
                variant.isActive()
        );
    }

    public ProductImageResponse toImageResponse(com.besquiros.piercingbysamar.entity.ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getR2Url(),
                image.getPosition(),
                image.getAltText(),
                image.getVariant() != null ? image.getVariant().getId() : null
        );
    }

    public ProductSummaryResponse toSummary(Product product) {
        List<ProductVariant> activeVariants = product.getVariants().stream()
                .filter(ProductVariant::isActive)
                .toList();

        Integer minPrice = activeVariants.stream()
                .map(ProductVariant::getPriceCents)
                .min(Comparator.naturalOrder())
                .orElse(null);

        Integer maxPrice = activeVariants.stream()
                .map(ProductVariant::getPriceCents)
                .max(Comparator.naturalOrder())
                .orElse(null);

        boolean inStock = activeVariants.stream().anyMatch(ProductVariant::isInStock);

        String coverUrl = product.getImages().isEmpty() ? null : product.getImages().get(0).getR2Url();

        List<String> tagNames = product.getTags().stream()
                .map(t -> t.getName())
                .toList();

        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getMaterial() != null ? product.getMaterial().name() : null,
                product.isNickelFree(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                minPrice,
                maxPrice,
                inStock,
                product.isActive(),
                coverUrl,
                tagNames
        );
    }

    public ProductDetailResponse toDetail(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getMaterial() != null ? product.getMaterial().name() : null,
                product.isNickelFree(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getMetaTitle(),
                product.getMetaDescription(),
                product.isActive(),
                product.getVariants().stream().filter(ProductVariant::isActive).map(this::toVariantResponse).toList(),
                product.getImages().stream().map(this::toImageResponse).toList(),
                product.getTags().stream().map(t -> t.getName()).toList(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    /** Admin view: includes inactive variants */
    public ProductDetailResponse toDetailAdmin(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getMaterial() != null ? product.getMaterial().name() : null,
                product.isNickelFree(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getMetaTitle(),
                product.getMetaDescription(),
                product.isActive(),
                product.getVariants().stream().map(this::toVariantResponse).toList(),
                product.getImages().stream().map(this::toImageResponse).toList(),
                product.getTags().stream().map(t -> t.getName()).toList(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
