package com.hutech.VoTranQuocHuy430.repository;

import com.hutech.VoTranQuocHuy430.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> searchByName(String name);
    @Query("SELECT p FROM Product p JOIN p.orderDetails od GROUP BY p ORDER BY COUNT(od) DESC")
    List<Product> findTop3ByOrderByOrderDetailsDesc();
}
