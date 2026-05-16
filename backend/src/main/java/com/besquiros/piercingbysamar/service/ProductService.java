package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.request.CreateProductRequest;
import com.besquiros.piercingbysamar.dto.request.CreateVariantRequest;
import com.besquiros.piercingbysamar.dto.request.UpdateProductRequest;
import com.besquiros.piercingbysamar.dto.response.PageResponse;
import com.besquiros.piercingbysamar.dto.response.ProductDetailResponse;
import com.besquiros.piercingbysamar.dto.response.ProductSummaryResponse;
import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.ProductVariant;
import com.besquiros.piercingbysamar.entity.enums.Material;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.ProductMapper;
import com.besquiros.piercingbysamar.repository.CategoryRepository;
import com.besquiros.piercingbysamar.repository.ProductRepository;
import com.besquiros.piercingbysamar.repository.ProductVariantRepository;
import com.besquiros.piercingbysamar.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductSummaryResponse> getAll(int page, int size, String sort) {
        Page<Product> result = productRepository.findByActiveTrue(PageRequest.of(page, size, resolveSort(sort)));
        return toPageResponse(result);
    }

    public PageResponse<ProductSummaryResponse> getWithFilters(Long categoryId, String material, Boolean nickelFree, int page, int size, String sort) {
        Material mat = material != null ? Material.valueOf(material.toUpperCase()) : null;
        Page<Product> result = productRepository.findWithFilters(categoryId, mat, nickelFree, PageRequest.of(page, size, resolveSort(sort)));
        return toPageResponse(result);
    }

    public PageResponse<ProductSummaryResponse> search(String query, int page, int size) {
        Page<Product> result = productRepository.searchByName(query, PageRequest.of(page, size));
        return toPageResponse(result);
    }

    public ProductDetailResponse getBySlug(String slug) {
        Product product = productRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + slug));
        return productMapper.toDetail(product);
    }

    public ProductDetailResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + id));
        return productMapper.toDetail(product);
    }

    // ── Admin ─────────────────────────────────────────────────

    @Transactional
    public ProductDetailResponse create(CreateProductRequest request) {
        String slug = request.slug() != null ? request.slug() : SlugUtils.toSlug(request.name());

        Product product = Product.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .material(request.material() != null ? Material.valueOf(request.material().toUpperCase()) : null)
                .nickelFree(request.nickelFree())
                .metaTitle(request.metaTitle())
                .metaDescription(request.metaDescription())
                .category(request.categoryId() != null
                        ? categoryRepository.findById(request.categoryId()).orElseThrow(() -> new NotFoundException("Catégorie introuvable"))
                        : null)
                .build();

        return productMapper.toDetail(productRepository.save(product));
    }

    @Transactional
    public ProductDetailResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + id));

        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.material() != null) product.setMaterial(Material.valueOf(request.material().toUpperCase()));
        if (request.nickelFree() != null) product.setNickelFree(request.nickelFree());
        if (request.metaTitle() != null) product.setMetaTitle(request.metaTitle());
        if (request.metaDescription() != null) product.setMetaDescription(request.metaDescription());
        if (request.active() != null) product.setActive(request.active());
        if (request.categoryId() != null) {
            product.setCategory(categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("Catégorie introuvable")));
        }

        return productMapper.toDetail(productRepository.save(product));
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public ProductDetailResponse addVariant(Long productId, CreateVariantRequest request) {
        if (variantRepository.existsBySku(request.sku())) {
            throw new BadRequestException("SKU déjà utilisé : " + request.sku());
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + productId));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.sku())
                .size(request.size())
                .color(request.color())
                .priceCents(request.priceCents())
                .stock(request.stock())
                .build();

        product.getVariants().add(variantRepository.save(variant));
        return productMapper.toDetail(product);
    }

    @Transactional
    public void deleteVariant(Long productId, Long variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variante introuvable : " + variantId));
        if (!variant.getProduct().getId().equals(productId)) {
            throw new BadRequestException("Cette variante n'appartient pas au produit " + productId);
        }
        variantRepository.delete(variant);
    }

    // ── Admin — liste complète (actifs + inactifs) ────────────

    public PageResponse<ProductSummaryResponse> getAllAdmin(int page, int size) {
        Page<Product> result = productRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPageResponse(result);
    }

    private Sort resolveSort(String sort) {
        return switch (sort == null ? "newest" : sort) {
            case "price_asc"  -> Sort.by("minPriceCents").ascending();
            case "price_desc" -> Sort.by("minPriceCents").descending();
            default           -> Sort.by("createdAt").descending(); // newest
        };
    }

    private PageResponse<ProductSummaryResponse> toPageResponse(Page<Product> page) {
        return new PageResponse<>(
                page.getContent().stream().map(productMapper::toSummary).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
