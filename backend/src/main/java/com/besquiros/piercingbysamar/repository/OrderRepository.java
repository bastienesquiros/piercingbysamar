package com.besquiros.piercingbysamar.repository;

import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.enums.OrderStatus;
import com.besquiros.piercingbysamar.entity.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByReference(String reference);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByOrderType(OrderType orderType, Pageable pageable);
    Page<Order> findByStatusAndOrderType(OrderStatus status, OrderType orderType, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.anonymizedAt IS NULL AND o.createdAt < :cutoff")
    List<Order> findAnonymizationCandidates(@Param("cutoff") LocalDateTime cutoff);

    // ── Stats ──────────────────────────────────────────────────

    @Query(value = "SELECT SUM(total_cents), COUNT(*) FROM orders " +
                   "WHERE status IN (:statuses) AND created_at BETWEEN :from AND :to",
           nativeQuery = true)
    List<Object[]> aggregateRevenue(@Param("statuses") List<String> statuses,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);

    @Query(value = "SELECT DATE(created_at) as day, SUM(total_cents), COUNT(*) FROM orders " +
                   "WHERE status IN (:statuses) AND created_at BETWEEN :from AND :to " +
                   "GROUP BY DATE(created_at) ORDER BY day",
           nativeQuery = true)
    List<Object[]> revenueByDay(@Param("statuses") List<String> statuses,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);

    @Query(value = "SELECT oi.snapshot_product_name, SUM(oi.quantity), SUM(oi.total_cents), MIN(pv.product_id) " +
                   "FROM order_items oi JOIN orders o ON oi.order_id = o.id " +
                   "LEFT JOIN product_variants pv ON pv.id = oi.product_variant_id " +
                   "WHERE o.status IN (:statuses) AND o.created_at BETWEEN :from AND :to " +
                   "GROUP BY oi.snapshot_product_name ORDER BY SUM(oi.quantity) DESC",
           nativeQuery = true)
    List<Object[]> topProducts(@Param("statuses") List<String> statuses,
                               @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);

    @Query(value = "SELECT o.status, COUNT(*) FROM orders o " +
                   "WHERE o.created_at BETWEEN :from AND :to GROUP BY o.status",
           nativeQuery = true)
    List<Object[]> countByStatus(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);

    @Query(value = "SELECT o.reference, o.created_at, o.status, o.order_type, " +
                   "o.customer_name, o.customer_email, o.customer_phone, o.total_cents, " +
                   "STRING_AGG(oi.snapshot_product_name || ' x' || oi.quantity::text, ' | ') " +
                   "FROM orders o LEFT JOIN order_items oi ON oi.order_id = o.id " +
                   "WHERE o.created_at BETWEEN :from AND :to " +
                   "GROUP BY o.id ORDER BY o.created_at DESC",
           nativeQuery = true)
    List<Object[]> exportOrders(@Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}
