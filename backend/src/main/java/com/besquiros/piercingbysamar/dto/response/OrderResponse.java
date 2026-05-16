package com.besquiros.piercingbysamar.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String reference,
        String orderType,
        String status,
        String customerEmail,
        String customerName,
        String customerPhone,
        String shippingAddress,
        String shippingCity,
        String shippingPostalCode,
        String shippingCountry,
        Integer subtotalCents,
        Integer shippingCostCents,
        Integer totalCents,
        String currency,
        String notes,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
