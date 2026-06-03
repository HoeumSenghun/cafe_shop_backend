package com.cafe_shop.admin.service;

import com.cafe_shop.order.dto.OrderDtos.CashierOrderResponse;
import com.cafe_shop.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface AdminOrderService {
    Page<CashierOrderResponse> listOrders(OrderStatus status, Instant from, Instant to, Pageable pageable);
}
