package com.cafe_shop.payment.mapper;

import com.cafe_shop.payment.dto.PaymentDtos.PaymentResponse;
import com.cafe_shop.payment.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }
}

