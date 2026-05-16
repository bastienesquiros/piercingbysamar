package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.Tag;
import com.besquiros.piercingbysamar.exception.BadRequestException;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.repository.ProductRepository;
import com.besquiros.piercingbysamar.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Transactional
    public Tag create(String name) {
        String trimmed = name.trim();
        if (tagRepository.existsByName(trimmed)) {
            throw new BadRequestException("Un tag avec ce nom existe déjà : " + trimmed);
        }
        String slug = toSlug(trimmed);
        if (tagRepository.existsBySlug(slug)) {
            throw new BadRequestException("Un tag avec ce slug existe déjà : " + slug);
        }
        return tagRepository.save(Tag.builder().name(trimmed).slug(slug).build());
    }

    @Transactional
    public Tag rename(Long id, String name) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag introuvable : " + id));
        String trimmed = name.trim();
        if (!tag.getName().equals(trimmed) && tagRepository.existsByName(trimmed)) {
            throw new BadRequestException("Un tag avec ce nom existe déjà : " + trimmed);
        }
        tag.setName(trimmed);
        tag.setSlug(toSlug(trimmed));
        return tagRepository.save(tag);
    }

    @Transactional
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new NotFoundException("Tag introuvable : " + id);
        }
        tagRepository.deleteById(id);
    }

    @Transactional
    public List<String> addTagToProduct(Long productId, Long tagId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + productId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag introuvable : " + tagId));
        if (!product.getTags().contains(tag)) {
            product.getTags().add(tag);
            productRepository.save(product);
        }
        return product.getTags().stream().map(Tag::getName).toList();
    }

    @Transactional
    public List<String> removeTagFromProduct(Long productId, Long tagId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + productId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag introuvable : " + tagId));
        product.getTags().remove(tag);
        productRepository.save(product);
        return product.getTags().stream().map(Tag::getName).toList();
    }

    private String toSlug(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
