package com.cafe_shop.order.mapper;

import com.cafe_shop.order.dto.OrderDtos.OrderItemResponse;
import com.cafe_shop.order.dto.OrderDtos.OrderResponse;
import com.cafe_shop.order.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .lineTotal(i.getLineTotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(o.getId())
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .createdAt(o.getCreatedAt())
                .items(items)
                .build();
    }
}

