package com.hutech.VoTranQuocHuy430.repository;

import com.hutech.VoTranQuocHuy430.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Các phương thức tùy biến (nếu cần)
}
