package com.example.pesca.repository;

import com.example.pesca.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByStatus(Order.Status status);

    Optional<Order> findTopByStatusOrderByIdDesc(Order.Status status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'CONFIRMED'")
    BigDecimal sumRevenueConfirmed();
}