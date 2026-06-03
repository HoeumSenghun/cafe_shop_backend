package com.cafe_shop.order.dto;

import com.cafe_shop.order.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderDtos {

    @Builder
    public record CreateOrderItemRequest(
            @NotNull Long productId,
            @Min(1) int quantity
    ) {}

    @Builder
    public record CreateOrderRequest(
            @NotEmpty List<@Valid CreateOrderItemRequest> items
    ) {}

    @Builder
    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {}

    @Builder
    public record OrderResponse(
            Long id,
            OrderStatus status,
            BigDecimal totalAmount,
            Instant createdAt,
            List<OrderItemResponse> items
    ) {}

    @Builder
    public record UpdateStatusRequest(
            @NotNull OrderStatus status
    ) {}

    @Builder
    public record CashierOrderResponse(
            Long id,
            OrderStatus status,
            BigDecimal totalAmount,
            Instant createdAt,
            String customerFullName,
            String customerEmail,
            List<OrderItemResponse> items
    ) {}

    @Builder
    public record PendingOrderSummaryResponse(
            Long id,
            BigDecimal totalAmount,
            Instant createdAt,
            String customerFullName,
            int itemCount
    ) {}
}

