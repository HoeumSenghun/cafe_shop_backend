package com.cafe_shop.admin.service.impl;

import com.cafe_shop.admin.service.AdminOrderService;
import com.cafe_shop.order.dto.OrderDtos.CashierOrderResponse;
import com.cafe_shop.order.mapper.OrderMapper;
import com.cafe_shop.order.model.Order;
import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CashierOrderResponse> listOrders(OrderStatus status, Instant from, Instant to, Pageable pageable) {
        Instant rangeFrom = from != null ? from : Instant.EPOCH;
        Instant rangeTo = to != null ? to : Instant.now().plusSeconds(86400);

        Page<Order> page = status == null
                ? orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                        rangeFrom, rangeTo, pageable)
                : orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndStatusOrderByCreatedAtDesc(
                        rangeFrom, rangeTo, status, pageable);
        return page.map(orderMapper::toCashierResponse);
    }
}
