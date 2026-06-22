package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.dto.request.CategoryRequest;
import com.besquiros.piercingbysamar.dto.response.CategoryResponse;
import com.besquiros.piercingbysamar.entity.Category;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.mapper.CategoryMapper;
import com.besquiros.piercingbysamar.repository.CategoryRepository;
import com.besquiros.piercingbysamar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;
    private final StorageService storageService;

    public List<CategoryResponse> getRootCategoriesWithChildren() {
        return categoryMapper.toResponseList(categoryRepository.findRootCategoriesWithChildren());
    }

    public List<CategoryResponse> getAllFlat() {
        return categoryMapper.toResponseList(
            categoryRepository.findAll().stream()
                .filter(Category::isActive)
                .toList()
        );
    }

    public CategoryResponse getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + slug));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.findBySlug(request.slug()).isPresent()) {
            throw new BadRequestException("Slug déjà utilisé : " + request.slug());
        }
        Category parent = resolveParent(request.parentId());
        Category category = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .parent(parent)
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + id));
        categoryRepository.findBySlug(request.slug())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new BadRequestException("Slug déjà utilisé : " + request.slug()); });
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        category.setParent(resolveParent(request.parentId()));
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + id));
        if (productRepository.existsByCategoryIdAndActiveTrue(id)) {
            throw new BadRequestException("Impossible de supprimer une catégorie contenant des produits actifs.");
        }
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category resolveParent(Long parentId) {
        if (parentId == null) return null;
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Catégorie parente introuvable : " + parentId));
    }

    @Transactional
    public CategoryResponse uploadImage(Long id, MultipartFile file) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + id));
        if (category.getImageUrl() != null) {
            storageService.delete(category.getImageUrl());
        }
        String url = storageService.upload(file, "categories");
        category.setImageUrl(url);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteImage(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable : " + id));
        if (category.getImageUrl() != null) {
            storageService.delete(category.getImageUrl());
            category.setImageUrl(null);
            categoryRepository.save(category);
        }
    }
}
