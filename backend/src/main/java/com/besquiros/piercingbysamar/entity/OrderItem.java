package com.besquiros.piercingbysamar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Nullable : la variante peut être supprimée après la commande */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    // ── Snapshot au moment de l'achat ────────────────────────
    @Column(nullable = false, length = 255)
    private String snapshotProductName;

    @Column(length = 255)
    private String snapshotVariantLabel;

    /** Prix unitaire en centimes au moment de l'achat */
    @Column(nullable = false)
    private Integer unitPriceCents;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer totalCents;
}
