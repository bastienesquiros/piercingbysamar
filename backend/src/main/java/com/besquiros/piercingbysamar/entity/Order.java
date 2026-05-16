package com.besquiros.piercingbysamar.entity;

import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.entity.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // ── Client ───────────────────────────────────────────────
    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerName;

    @Column(length = 50)
    private String customerPhone;

    // ── Adresse livraison (null si Click & Collect) ──────────
    @Column(length = 255)
    private String shippingAddress;

    @Column(length = 100)
    private String shippingCity;

    @Column(length = 20)
    private String shippingPostalCode;

    @Column(length = 2)
    private String shippingCountry;

    // ── Montants en centimes ─────────────────────────────────
    @Column(nullable = false)
    private Integer subtotalCents;

    @Column(nullable = false)
    @Builder.Default
    private Integer shippingCostCents = 0;

    @Column(nullable = false)
    private Integer totalCents;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "EUR";

    // ── Stripe ───────────────────────────────────────────────
    @Column(length = 255)
    private String stripeSessionId;

    @Column(length = 255)
    private String stripePaymentIntentId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Items ────────────────────────────────────────────────
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Non-null = les données perso ont été effacées (RGPD). */
    @Column
    private LocalDateTime anonymizedAt;
}
