package com.besquiros.piercingbysamar.repository;

import com.besquiros.piercingbysamar.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    Optional<ProductImage> findByIdAndProductId(Long id, Long productId);

    @Query("SELECT MAX(i.position) FROM ProductImage i WHERE i.product.id = :productId")
    Optional<Integer> findMaxPositionByProductId(Long productId);
}
