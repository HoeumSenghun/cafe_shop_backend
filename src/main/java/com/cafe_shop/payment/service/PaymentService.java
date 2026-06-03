package com.cafe_shop.payment.service;

import com.cafe_shop.payment.dto.PaymentDtos.CreatePaymentRequest;
import com.cafe_shop.payment.dto.PaymentDtos.PaymentResponse;
import com.cafe_shop.payment.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponse create(CreatePaymentRequest req);
    PaymentResponse getByOrderId(long orderId);
    Page<PaymentResponse> listAll(PaymentStatus status, Pageable pageable);
}

