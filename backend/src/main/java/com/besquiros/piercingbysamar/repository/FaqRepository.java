package com.besquiros.piercingbysamar.repository;

import com.besquiros.piercingbysamar.entity.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<FaqItem, Long> {
    List<FaqItem> findByActiveTrueOrderByPositionAsc();
    List<FaqItem> findAllByOrderByPositionAsc();
}
