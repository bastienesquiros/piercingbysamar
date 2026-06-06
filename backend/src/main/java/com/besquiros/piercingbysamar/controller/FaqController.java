package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.response.FaqItemResponse;
import com.besquiros.piercingbysamar.entity.FaqItem;
import com.besquiros.piercingbysamar.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FaqController {

    private final FaqRepository faqRepository;

    // ── Public ─────────────────────────────────────────────────────
    @GetMapping("/api/faq")
    public ResponseEntity<List<FaqItemResponse>> getPublic() {
        return ResponseEntity.ok(
                faqRepository.findByActiveTrueOrderByPositionAsc()
                        .stream().map(this::toResponse).toList()
        );
    }

    // ── Admin ──────────────────────────────────────────────────────
    @GetMapping("/api/admin/faq")
    public ResponseEntity<List<FaqItemResponse>> getAll() {
        return ResponseEntity.ok(
                faqRepository.findAllByOrderByPositionAsc()
                        .stream().map(this::toResponse).toList()
        );
    }

    @PostMapping("/api/admin/faq")
    public ResponseEntity<FaqItemResponse> create(@RequestBody FaqRequest body) {
        long maxPos = faqRepository.findAllByOrderByPositionAsc().stream()
                .mapToLong(f -> f.getPosition() == null ? 0 : f.getPosition()).max().orElse(-1);
        FaqItem item = FaqItem.builder()
                .question(body.question())
                .answer(body.answer())
                .position((int) maxPos + 1)
                .active(body.active() != null ? body.active() : true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(faqRepository.save(item)));
    }

    @PutMapping("/api/admin/faq/{id}")
    public ResponseEntity<FaqItemResponse> update(@PathVariable Long id, @RequestBody FaqRequest body) {
        FaqItem item = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ item not found: " + id));
        if (body.question() != null) item.setQuestion(body.question());
        if (body.answer() != null) item.setAnswer(body.answer());
        if (body.active() != null) item.setActive(body.active());
        if (body.position() != null) item.setPosition(body.position());
        return ResponseEntity.ok(toResponse(faqRepository.save(item)));
    }

    @DeleteMapping("/api/admin/faq/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faqRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private FaqItemResponse toResponse(FaqItem f) {
        return new FaqItemResponse(f.getId(), f.getQuestion(), f.getAnswer(), f.getPosition(), f.getActive());
    }

    public record FaqRequest(String question, String answer, Boolean active, Integer position) {}
}
