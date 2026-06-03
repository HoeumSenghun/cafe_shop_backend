package com.cafe_shop.payment.service.impl;

import com.cafe_shop.common.exception.BusinessException;
import com.cafe_shop.common.exception.ResourceNotFoundException;
import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.order.repository.OrderRepository;
import com.cafe_shop.payment.dto.PaymentDtos.CreatePaymentRequest;
import com.cafe_shop.payment.dto.PaymentDtos.PaymentResponse;
import com.cafe_shop.payment.mapper.PaymentMapper;
import com.cafe_shop.payment.model.Payment;
import com.cafe_shop.payment.model.PaymentStatus;
import com.cafe_shop.payment.repository.PaymentRepository;
import com.cafe_shop.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest req) {
        var order = orderRepository.findById(req.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)) {
            throw new BusinessException("Order already has a successful payment");
        }
        if (order.getTotalAmount() == null || req.amount().compareTo(order.getTotalAmount()) != 0) {
            throw new BusinessException("Payment amount must match order total");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(req.amount());
        payment.setMethod(req.method());
        payment.setStatus(PaymentStatus.SUCCESS);

        var saved = paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByOrderId(long orderId) {
        Payment p = paymentRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return paymentMapper.toResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> listAll(PaymentStatus status, Pageable pageable) {
        Page<Payment> page = status == null
                ? paymentRepository.findAllByOrderByCreatedAtDesc(pageable)
                : paymentRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return page.map(paymentMapper::toResponse);
    }
}

