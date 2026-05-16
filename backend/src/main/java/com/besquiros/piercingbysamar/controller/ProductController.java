package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.response.PageResponse;
import com.besquiros.piercingbysamar.dto.response.ProductDetailResponse;
import com.besquiros.piercingbysamar.dto.response.ProductSummaryResponse;
import com.besquiros.piercingbysamar.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) Boolean nickelFree,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        if (categoryId != null || material != null || nickelFree != null) {
            return ResponseEntity.ok(productService.getWithFilters(categoryId, material, nickelFree, page, size, sort));
        }
        return ResponseEntity.ok(productService.getAll(page, size, sort));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(productService.search(q, page, size));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }
}
