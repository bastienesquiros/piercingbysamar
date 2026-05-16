package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.StripeCheckoutRequest;
import com.besquiros.piercingbysamar.dto.response.StripeCheckoutResponse;
import com.besquiros.piercingbysamar.exception.GlobalExceptionHandler;
import com.besquiros.piercingbysamar.service.StripeService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StripeControllerIT {

    @Mock StripeService stripeService;
    @InjectMocks StripeController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCheckout_withValidReference_shouldReturn200WithUrl() throws Exception {
        StripeCheckoutRequest request = new StripeCheckoutRequest("PBS-2026-0001", "EUR");
        StripeCheckoutResponse response = new StripeCheckoutResponse("https://checkout.stripe.com/pay/cs_test_123");

        when(stripeService.createCheckoutSession("PBS-2026-0001", any())).thenReturn(response);

        mockMvc.perform(post("/api/stripe/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.stripe.com/pay/cs_test_123"));
    }

    @Test
    void createCheckout_withBlankReference_shouldReturn400() throws Exception {
        StripeCheckoutRequest request = new StripeCheckoutRequest("", null);

        mockMvc.perform(post("/api/stripe/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void webhook_withValidPayload_shouldReturn200() throws Exception {
        doNothing().when(stripeService).handleWebhook(any(), any());

        mockMvc.perform(post("/api/stripe/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=12345,v1=abc123")
                        .content("{\"type\":\"checkout.session.completed\"}"))
                .andExpect(status().isOk());

        verify(stripeService).handleWebhook(any(), eq("t=12345,v1=abc123"));
    }
}
