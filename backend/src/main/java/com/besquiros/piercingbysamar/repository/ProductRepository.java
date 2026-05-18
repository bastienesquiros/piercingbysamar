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

    boolean existsByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND EXISTS (SELECT v FROM ProductVariant v WHERE v.product = p AND v.active = true)")
    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category.id = :categoryId " +
           "AND EXISTS (SELECT v FROM ProductVariant v WHERE v.product = p AND v.active = true)")
    Page<Product> findByCategoryIdAndActiveTrue(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.material = :material " +
           "AND EXISTS (SELECT v FROM ProductVariant v WHERE v.product = p AND v.active = true)")
    Page<Product> findByMaterialAndActiveTrue(@Param("material") Material material, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.nickelFree = true " +
           "AND EXISTS (SELECT v FROM ProductVariant v WHERE v.product = p AND v.active = true)")
    Page<Product> findByNickelFreeTrueAndActiveTrue(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN p.variants v " +
           "WHERE p.active = true " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "     OR (v.active = true AND LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%')))) " +
           "AND EXISTS (SELECT v2 FROM ProductVariant v2 WHERE v2.product = p AND v2.active = true) " +
           "ORDER BY CASE WHEN LOWER(p.name) LIKE LOWER(CONCAT(:q, '%')) THEN 0 ELSE 1 END, p.name ASC")
    Page<Product> searchByName(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId " +
           "     OR (p.category.parent IS NOT NULL AND p.category.parent.id = :categoryId)) " +
           "AND (:material IS NULL OR p.material = :material) " +
           "AND (:nickelFree IS NULL OR p.nickelFree = :nickelFree) " +
           "AND EXISTS (SELECT v FROM ProductVariant v WHERE v.product = p AND v.active = true)")
    Page<Product> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("material") Material material,
            @Param("nickelFree") Boolean nickelFree,
            Pageable pageable);
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.variants v WHERE " +
           "(:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "            OR LOWER(v.sku)  LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findAdminByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.variants v WHERE " +
           "(:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "            OR LOWER(v.sku)  LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND p.active = :active")
    Page<Product> findAdminByNameAndActive(
            @Param("name") String name,
            @Param("active") boolean active,
            Pageable pageable);
}
