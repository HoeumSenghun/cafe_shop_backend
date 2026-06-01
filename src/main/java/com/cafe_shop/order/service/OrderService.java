package com.cafe_shop.order.service;

import com.cafe_shop.order.dto.OrderDtos.CreateOrderRequest;
import com.cafe_shop.order.dto.OrderDtos.OrderResponse;
import com.cafe_shop.order.dto.OrderDtos.UpdateStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createForCurrentCustomer(CreateOrderRequest req);
    Page<OrderResponse> getMyOrders(Pageable pageable);
    OrderResponse getMyOrderById(long id);
    OrderResponse getById(long id);
    OrderResponse updateStatus(long id, UpdateStatusRequest req);
}

