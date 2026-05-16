package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.entity.Tag;
import com.besquiros.piercingbysamar.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /** Liste tous les tags — public (utilisé par les filtres client). */
    @GetMapping("/api/tags")
    public ResponseEntity<List<Tag>> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    /** Crée un nouveau tag — admin. */
    @PostMapping("/api/admin/tags")
    public ResponseEntity<Tag> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(tagService.create(body.get("name")));
    }

    /** Renomme un tag — admin. */
    @PatchMapping("/api/admin/tags/{id}")
    public ResponseEntity<Tag> rename(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(tagService.rename(id, body.get("name")));
    }

    /** Supprime un tag (retire aussi des produits via cascade) — admin. */
    @DeleteMapping("/api/admin/tags/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Associe un tag à un produit — admin. */
    @PostMapping("/api/admin/products/{productId}/tags/{tagId}")
    public ResponseEntity<List<String>> addTag(@PathVariable Long productId, @PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.addTagToProduct(productId, tagId));
    }

    /** Retire un tag d'un produit — admin. */
    @DeleteMapping("/api/admin/products/{productId}/tags/{tagId}")
    public ResponseEntity<List<String>> removeTag(@PathVariable Long productId, @PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.removeTagFromProduct(productId, tagId));
    }
}
