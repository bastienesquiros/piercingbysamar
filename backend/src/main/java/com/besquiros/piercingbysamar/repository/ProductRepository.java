package com.besquiros.piercingbysamar.repository;

import com.besquiros.piercingbysamar.entity.Product;
import com.besquiros.piercingbysamar.entity.enums.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlugAndActiveTrue(String slug);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByMaterialAndActiveTrue(Material material, Pageable pageable);

    Page<Product> findByNickelFreeTrueAndActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Product> searchByName(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:material IS NULL OR p.material = :material) " +
           "AND (:nickelFree IS NULL OR p.nickelFree = :nickelFree)")
    Page<Product> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("material") Material material,
            @Param("nickelFree") Boolean nickelFree,
            Pageable pageable);
}
