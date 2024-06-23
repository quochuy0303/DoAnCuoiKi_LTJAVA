package com.hutech.VoTranQuocHuy430.repository;

import com.hutech.VoTranQuocHuy430.DTO.MonthlyRevenueDTO;
import com.hutech.VoTranQuocHuy430.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends CrudRepository<Order, Long> {

    @Query("SELECT new com.hutech.VoTranQuocHuy430.DTO.MonthlyRevenueDTO(MONTH(o.orderDate), SUM(o.totalAmount)) " +
            "FROM Order o WHERE YEAR(o.orderDate) = :year GROUP BY MONTH(o.orderDate)")
    List<MonthlyRevenueDTO> findMonthlyRevenue(int year);
}
