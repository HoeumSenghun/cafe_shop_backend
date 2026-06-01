package com.cafe_shop.order.repository;

import com.cafe_shop.order.model.Order;
import com.cafe_shop.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);

    @Query("""
        select o from Order o
        where o.createdAt >= :start and o.createdAt < :end
        order by o.createdAt desc
    """)
    List<Order> findTodayOrders(Instant start, Instant end);

    long countByStatus(OrderStatus status);
}

