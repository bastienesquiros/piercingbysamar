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

    /** Commandes non encore anonymisées créées avant une date donnée. */
    @Query("SELECT o FROM Order o WHERE o.anonymizedAt IS NULL AND o.createdAt < :cutoff")
    List<Order> findAnonymizationCandidates(@Param("cutoff") LocalDateTime cutoff);
}
