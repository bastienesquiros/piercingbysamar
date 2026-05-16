package com.besquiros.piercingbysamar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;

/**
 * Récupère les taux de change MAD → EUR/USD depuis open.er-api.com (gratuit, sans clé API).
 * Frankfurter (BCE) ne couvre pas le MAD — on utilise cette alternative.
 * Mise à jour quotidienne à 9h, avec fallback sur les taux précédents si l'API est indisponible.
 */
@Slf4j
@Service
public class ExchangeRateService {

    private static final String API_URL = "https://open.er-api.com/v6/latest/MAD";

    // Taux de secours si l'API est indisponible
    private static final double FALLBACK_EUR = 0.0926;
    private static final double FALLBACK_USD = 0.099;

    private volatile double madToEur = FALLBACK_EUR;
    private volatile double madToUsd = FALLBACK_USD;
    private volatile LocalDate lastUpdated = null;

    private final RestClient restClient = RestClient.builder()
            .defaultHeader("Accept", "application/json")
            .build();

    @PostConstruct
    public void fetchOnStartup() {
        fetchRates();
    }

    @Scheduled(cron = "0 0 9 * * *") // Tous les jours à 9h
    public void fetchRates() {
        try {
            OpenErResponse response = restClient.get()
                    .uri(API_URL)
                    .retrieve()
                    .body(OpenErResponse.class);

            if (response != null && "success".equals(response.result()) && response.rates() != null) {
                Double eur = response.rates().get("EUR");
                Double usd = response.rates().get("USD");
                if (eur != null) madToEur = eur;
                if (usd != null) madToUsd = usd;
                lastUpdated = LocalDate.now();
                log.info("Taux de change mis à jour : 1 MAD = {} EUR / {} USD", madToEur, madToUsd);
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer les taux (open.er-api.com), taux précédents conservés : {}", e.getMessage());
        }
    }

    public double getMadToEur() { return madToEur; }
    public double getMadToUsd() { return madToUsd; }
    public LocalDate getLastUpdated() { return lastUpdated; }

    private record OpenErResponse(String result, String base_code, Map<String, Double> rates) {}
}
