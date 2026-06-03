package com.cafe_shop.order.controller;

import com.cafe_shop.common.api.ApiEnvelope;
import com.cafe_shop.order.dto.OrderDtos.CashierOrderResponse;
import com.cafe_shop.order.dto.OrderDtos.CreateOrderRequest;
import com.cafe_shop.order.dto.OrderDtos.OrderResponse;
import com.cafe_shop.order.dto.OrderDtos.PendingOrderSummaryResponse;
import com.cafe_shop.order.dto.OrderDtos.UpdateStatusRequest;
import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.order.service.OrderService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiEnvelope<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Created", orderService.createForCurrentCustomer(req)));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    public ResponseEntity<ApiEnvelope<Page<OrderResponse>>> myOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiEnvelope.ok("OK", orderService.getMyOrders(pageable)));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me/{id}")
    public ResponseEntity<ApiEnvelope<OrderResponse>> myOrderById(@PathVariable long id) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", orderService.getMyOrderById(id)));
    }

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiEnvelope<Page<CashierOrderResponse>>> listAll(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiEnvelope.ok("OK", orderService.listAllForCashier(status, pageable)));
    }

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiEnvelope<List<PendingOrderSummaryResponse>>> pendingSummaries() {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", orderService.listPendingSummaries()));
    }

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<OrderResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", orderService.getById(id)));
    }

    @PreAuthorize("hasAnyRole('CASHIER','ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiEnvelope<OrderResponse>> updateStatus(@PathVariable long id, @Valid @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Updated", orderService.updateStatus(id, req)));
    }
}

