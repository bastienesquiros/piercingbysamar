package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.response.ProductImageResponse;
import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.ProductImage;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.repository.ProductImageRepository;
import com.besquiros.piercingbysamar.repository.ProductRepository;
import com.besquiros.piercingbysamar.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products/{productId}/images")
@RequiredArgsConstructor
public class AdminImageController {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final StorageService storageService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductImageResponse> upload(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + productId));

        String url = storageService.upload(file, "products/" + productId);

        // Auto-assign position = current max + 1 so images don't all start at 0
        int nextPosition = imageRepository.findMaxPositionByProductId(productId)
                .map(max -> max + 1)
                .orElse(0);

        ProductImage image = ProductImage.builder()
                .product(product)
                .r2Url(url)
                .altText(altText)
                .position(nextPosition)
                .build();

        ProductImage saved = imageRepository.save(image);

        return ResponseEntity.ok(new ProductImageResponse(
                saved.getId(), saved.getR2Url(), saved.getPosition(), saved.getAltText()));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        ProductImage image = imageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new NotFoundException("Image introuvable : " + imageId));

        storageService.delete(image.getR2Url());
        imageRepository.delete(image);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> update(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestBody UpdateImageRequest body) {

        ProductImage image = imageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new NotFoundException("Image introuvable : " + imageId));

        if (body.altText() != null) image.setAltText(body.altText());
        if (body.position() != null) image.setPosition(body.position());
        ProductImage saved = imageRepository.save(image);

        return ResponseEntity.ok(new ProductImageResponse(
                saved.getId(), saved.getR2Url(), saved.getPosition(), saved.getAltText()));
    }

    public record UpdateImageRequest(String altText, Integer position) {}
}
