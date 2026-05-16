package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.CreateOrderRequest;
import com.besquiros.piercingbysamar.dto.request.OrderItemRequest;
import com.besquiros.piercingbysamar.dto.response.OrderResponse;
import com.besquiros.piercingbysamar.exception.GlobalExceptionHandler;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerIT {

    @Mock OrderService orderService;
    @InjectMocks OrderController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private OrderResponse orderResponse(String ref) {
        return new OrderResponse(1L, ref, "SHIPPING", "PENDING",
                "client@test.com", "Client Test", null,
                "12 rue Test", "Paris", "75001", "FR",
                2490, 490, 2980, "EUR", null, List.of(), null, null);
    }

    @Test
    void create_withValidShippingOrder_shouldReturn201() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "SHIPPING", "client@test.com", "Client Test", null,
                "12 rue Test", "Paris", "75001", "FR",
                List.of(new OrderItemRequest(1L, 1)),
                "EUR", null);

        when(orderService.create(any())).thenReturn(orderResponse("PBS-2026-0007"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("PBS-2026-0007"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void create_withEmptyItems_shouldReturn400() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "SHIPPING", "client@test.com", "Client Test", null,
                "12 rue Test", "Paris", "75001", "FR",
                List.of(), // items vides
                "EUR", null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withMissingEmail_shouldReturn400() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "SHIPPING", null, "Client Test", null,
                "12 rue Test", "Paris", "75001", "FR",
                List.of(new OrderItemRequest(1L, 1)),
                "EUR", null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByReference_whenFound_shouldReturn200() throws Exception {
        when(orderService.getByReference("PBS-2026-0001")).thenReturn(orderResponse("PBS-2026-0001"));

        mockMvc.perform(get("/api/orders/PBS-2026-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("PBS-2026-0001"));
    }

    @Test
    void getByReference_whenNotFound_shouldReturn404() throws Exception {
        when(orderService.getByReference("UNKNOWN")).thenThrow(new NotFoundException("Commande introuvable : UNKNOWN"));

        mockMvc.perform(get("/api/orders/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}
