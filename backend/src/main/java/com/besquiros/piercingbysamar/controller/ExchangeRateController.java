package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ExchangeRatesResponse getRates() {
        return new ExchangeRatesResponse(
                "MAD",
                Map.of("EUR", exchangeRateService.getMadToEur(),
                       "USD", exchangeRateService.getMadToUsd()),
                exchangeRateService.getLastUpdated()
        );
    }

    record ExchangeRatesResponse(String base, Map<String, Double> rates, LocalDate date) {}
}
