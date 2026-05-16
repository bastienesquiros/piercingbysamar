package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.exception.NotFoundException;
import com.besquiros.piercingbysamar.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Anonymise les données personnelles des commandes après 3 ans (RGPD).
 * Les données financières (montants, articles) sont conservées pour la comptabilité.
 *
 * Durée choisie : 3 ans (recommandation CNIL pour données clients e-commerce).
 * Les obligations comptables françaises imposent 10 ans pour les pièces justificatives,
 * d'où l'anonymisation plutôt que la suppression.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GdprAnonymizationService {

    private static final int RETENTION_YEARS = 3;
    private static final String ANONYMIZED = "[anonymisé]";

    private final OrderRepository orderRepository;

    /** Lance l'anonymisation automatique chaque nuit à 2h. */
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledAnonymization() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(RETENTION_YEARS);
        List<Order> candidates = orderRepository.findAnonymizationCandidates(cutoff);
        if (candidates.isEmpty()) return;

        log.info("[RGPD] {} commande(s) à anonymiser (antérieures au {})", candidates.size(), cutoff.toLocalDate());
        candidates.forEach(this::anonymize);
        orderRepository.saveAll(candidates);
        log.info("[RGPD] Anonymisation terminée.");
    }

    /** Anonymise manuellement une commande (à la demande d'un client). */
    @Transactional
    public void anonymizeById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande introuvable : " + orderId));
        if (order.getAnonymizedAt() != null) {
            log.info("[RGPD] Commande {} déjà anonymisée le {}", order.getReference(), order.getAnonymizedAt());
            return;
        }
        anonymize(order);
        orderRepository.save(order);
        log.info("[RGPD] Commande {} anonymisée manuellement.", order.getReference());
    }

    private void anonymize(Order order) {
        order.setCustomerEmail(ANONYMIZED);
        order.setCustomerName(ANONYMIZED);
        order.setCustomerPhone(null);
        order.setShippingAddress(null);
        order.setShippingCity(null);
        order.setShippingPostalCode(null);
        order.setShippingCountry(null);
        order.setNotes(null);
        order.setStripeSessionId(null);
        order.setStripePaymentIntentId(null);
        order.setAnonymizedAt(LocalDateTime.now());
    }
}
