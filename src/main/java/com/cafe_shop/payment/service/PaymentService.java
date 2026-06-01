package com.cafe_shop.payment.service;

import com.cafe_shop.payment.dto.PaymentDtos.CreatePaymentRequest;
import com.cafe_shop.payment.dto.PaymentDtos.PaymentResponse;

public interface PaymentService {
    PaymentResponse create(CreatePaymentRequest req);
    PaymentResponse getByOrderId(long orderId);
}

