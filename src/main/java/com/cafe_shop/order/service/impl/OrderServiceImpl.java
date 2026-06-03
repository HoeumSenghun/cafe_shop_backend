package com.cafe_shop.order.service.impl;

import com.cafe_shop.common.exception.BusinessException;
import com.cafe_shop.common.exception.ResourceNotFoundException;
import com.cafe_shop.order.dto.OrderDtos.CashierOrderResponse;
import com.cafe_shop.order.dto.OrderDtos.CreateOrderRequest;
import com.cafe_shop.order.dto.OrderDtos.OrderResponse;
import com.cafe_shop.order.dto.OrderDtos.PendingOrderSummaryResponse;
import com.cafe_shop.order.dto.OrderDtos.UpdateStatusRequest;
import com.cafe_shop.order.mapper.OrderMapper;
import com.cafe_shop.order.model.Order;
import com.cafe_shop.order.model.OrderItem;
import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.order.repository.OrderItemRepository;
import com.cafe_shop.order.repository.OrderRepository;
import com.cafe_shop.product.model.Product;
import com.cafe_shop.product.repository.ProductRepository;
import com.cafe_shop.security.SecurityUtils;
import com.cafe_shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements com.cafe_shop.order.service.OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createForCurrentCustomer(CreateOrderRequest req) {
        var cu = SecurityUtils.currentUser();
        var customer = userRepository.findById(cu.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;
        Order saved = orderRepository.save(order);

        for (var itemReq : req.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            if (product.isDeleted() || !product.isAvailable()) {
                throw new BusinessException("Product not available");
            }
            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem oi = new OrderItem();
            oi.setOrder(saved);
            oi.setProduct(product);
            oi.setQuantity(itemReq.quantity());
            oi.setUnitPrice(unitPrice);
            oi.setLineTotal(lineTotal);
            orderItemRepository.save(oi);

            total = total.add(lineTotal);
        }

        saved.setTotalAmount(total);
        saved = orderRepository.save(saved);
        saved.setItems(saved.getItems()); // keep for mapper; items loaded lazily anyway
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        var cu = SecurityUtils.currentUser();
        return orderRepository.findByCustomerId(cu.userId(), pageable).map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(long id) {
        var cu = SecurityUtils.currentUser();
        Order order = orderRepository.findByIdAndCustomerId(id, cu.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CashierOrderResponse> listAllForCashier(OrderStatus status, Pageable pageable) {
        Page<Order> page = status == null
                ? orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                : orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return page.map(orderMapper::toCashierResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingOrderSummaryResponse> listPendingSummaries() {
        return orderRepository.findTop20ByStatusOrderByCreatedAtDesc(OrderStatus.PENDING).stream()
                .map(orderMapper::toPendingSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(long id, UpdateStatusRequest req) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessException("Cannot modify order after PAID");
        }

        order.setStatus(req.status());
        return orderMapper.toResponse(orderRepository.save(order));
    }
}

