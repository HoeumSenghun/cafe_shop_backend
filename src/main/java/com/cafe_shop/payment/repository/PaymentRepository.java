package com.cafe_shop.payment.repository;

import com.cafe_shop.payment.model.Payment;
import com.cafe_shop.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);
    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);
}

