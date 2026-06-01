package com.cafe_shop.payment.dto;

import com.cafe_shop.payment.model.PaymentMethod;
import com.cafe_shop.payment.model.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentDtos {

    @Builder
    public record CreatePaymentRequest(
            @NotNull Long orderId,
            @NotNull @DecimalMin("0.00") BigDecimal amount,
            @NotNull PaymentMethod method
    ) {}

    @Builder
    public record PaymentResponse(
            Long id,
            Long orderId,
            BigDecimal amount,
            PaymentMethod method,
            PaymentStatus status,
            Instant createdAt
    ) {}
}

