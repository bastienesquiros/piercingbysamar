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
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "position", defaultValue = "0") Integer position) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + productId));

        String url = storageService.upload(file, "products/" + productId);

        ProductImage image = ProductImage.builder()
                .product(product)
                .r2Url(url)
                .altText(altText)
                .position(position)
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
}
