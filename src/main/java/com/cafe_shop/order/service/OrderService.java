package com.cafe_shop.order.service;

import com.cafe_shop.order.dto.OrderDtos.CashierOrderResponse;
import com.cafe_shop.order.dto.OrderDtos.CreateOrderRequest;
import com.cafe_shop.order.dto.OrderDtos.OrderResponse;
import com.cafe_shop.order.dto.OrderDtos.PendingOrderSummaryResponse;
import com.cafe_shop.order.dto.OrderDtos.UpdateStatusRequest;
import com.cafe_shop.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse createForCurrentCustomer(CreateOrderRequest req);
    Page<OrderResponse> getMyOrders(Pageable pageable);
    OrderResponse getMyOrderById(long id);
    Page<CashierOrderResponse> listAllForCashier(OrderStatus status, Pageable pageable);
    List<PendingOrderSummaryResponse> listPendingSummaries();
    OrderResponse getById(long id);
    OrderResponse updateStatus(long id, UpdateStatusRequest req);
}

