package com.besquiros.piercingbysamar.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull String orderType,           // SHIPPING | CLICK_COLLECT
        @NotBlank @Email String customerEmail,
        @NotBlank String customerName,
        String customerPhone,
        // Obligatoire si SHIPPING
        String shippingAddress,
        String shippingCity,
        String shippingPostalCode,
        String shippingCountry,
        @NotEmpty @Valid List<OrderItemRequest> items,
        String currency,
        String notes
) {}
