package com.hutech.VoTranQuocHuy430.repository;

import com.hutech.VoTranQuocHuy430.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "(:search IS NULL OR p.name LIKE %:search%) AND " +
            "(:category IS NULL OR p.category.name = :category) AND " +
            "(:priceFrom IS NULL OR p.price >= :priceFrom) AND " +
            "(:priceTo IS NULL OR p.price <= :priceTo)")
    Page<Product> searchProducts(String search, String category, Double priceFrom, Double priceTo, Pageable pageable);
    @Query(value = "SELECT p FROM Product p JOIN p.orderDetails od GROUP BY p ORDER BY COUNT(od) DESC")
    List<Product> findTop3ByOrderByOrderDetailsDesc();
    
    @Query("SELECT p.name FROM Product p WHERE p.name LIKE %:keyword%")
    List<String> findProductNamesByKeyword(@Param("keyword") String keyword);
}
