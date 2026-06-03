package com.cafe_shop.payment.controller;

import com.cafe_shop.common.api.ApiEnvelope;
import com.cafe_shop.payment.dto.PaymentDtos.CreatePaymentRequest;
import com.cafe_shop.payment.dto.PaymentDtos.PaymentResponse;
import com.cafe_shop.payment.model.PaymentStatus;
import com.cafe_shop.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @PostMapping
    public ResponseEntity<ApiEnvelope<PaymentResponse>> create(@Valid @RequestBody CreatePaymentRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Created", paymentService.create(req)));
    }

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiEnvelope<Page<PaymentResponse>>> listAll(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiEnvelope.ok("OK", paymentService.listAll(status, pageable)));
    }

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @GetMapping("/by-order")
    public ResponseEntity<ApiEnvelope<PaymentResponse>> getByOrderId(@RequestParam long orderId) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", paymentService.getByOrderId(orderId)));
    }
}

