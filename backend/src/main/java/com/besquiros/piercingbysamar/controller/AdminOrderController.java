package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.UpdateOrderStatusRequest;
import com.besquiros.piercingbysamar.dto.response.OrderResponse;
import com.besquiros.piercingbysamar.dto.response.PageResponse;
import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.service.GdprAnonymizationService;
import com.besquiros.piercingbysamar.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final GdprAnonymizationService gdprService;

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status
    ) {
        OrderStatus orderStatus = null;
        if (status != null && !status.isBlank()) {
            try { orderStatus = OrderStatus.valueOf(status.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }
        return ResponseEntity.ok(orderService.getAll(page, size, orderStatus));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByReference(String.valueOf(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.status()));
    }

    /** Anonymise les données personnelles d'une commande (RGPD — droit à l'effacement). */
    @PostMapping("/{id}/anonymize")
    public ResponseEntity<Void> anonymize(@PathVariable Long id) {
        gdprService.anonymizeById(id);
        return ResponseEntity.noContent().build();
    }
}
