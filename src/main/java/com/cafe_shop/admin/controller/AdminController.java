package com.cafe_shop.admin.controller;

import com.cafe_shop.admin.dto.AdminDtos.CreateStaffRequest;
import com.cafe_shop.admin.dto.AdminDtos.DailyRevenueResponse;
import com.cafe_shop.admin.dto.AdminDtos.DashboardResponse;
import com.cafe_shop.admin.dto.AdminDtos.ProductOverviewResponse;
import com.cafe_shop.admin.dto.AdminDtos.SalesReportResponse;
import com.cafe_shop.admin.dto.AdminDtos.UpdateUserEnabledRequest;
import com.cafe_shop.admin.dto.AdminDtos.UserResponse;
import com.cafe_shop.admin.service.AdminDashboardService;
import com.cafe_shop.admin.service.AdminOrderService;
import com.cafe_shop.admin.service.AdminReportService;
import com.cafe_shop.admin.service.AdminUserService;
import com.cafe_shop.common.api.ApiEnvelope;
import com.cafe_shop.order.dto.OrderDtos.CashierOrderResponse;
import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.user.model.RoleName;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Tag(name = "Admin")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final AdminUserService adminUserService;
    private final AdminOrderService adminOrderService;
    private final AdminReportService adminReportService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiEnvelope<DashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminDashboardService.dashboard()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiEnvelope<Page<UserResponse>>> listUsers(
            @RequestParam(required = false) RoleName role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminUserService.listUsers(role, pageable)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiEnvelope<UserResponse>> getUser(@PathVariable long id) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminUserService.getById(id)));
    }

    @PostMapping("/users/staff")
    public ResponseEntity<ApiEnvelope<UserResponse>> createStaff(@Valid @RequestBody CreateStaffRequest req) {
        return ResponseEntity.ok(ApiEnvelope.ok("Created", adminUserService.createStaff(req)));
    }

    @PatchMapping("/users/{id}/enabled")
    public ResponseEntity<ApiEnvelope<UserResponse>> setUserEnabled(
            @PathVariable long id,
            @Valid @RequestBody UpdateUserEnabledRequest req
    ) {
        return ResponseEntity.ok(ApiEnvelope.ok("Updated", adminUserService.setEnabled(id, req.enabled())));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiEnvelope<Page<CashierOrderResponse>>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminOrderService.listOrders(status, from, to, pageable)));
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<ApiEnvelope<SalesReportResponse>> salesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminReportService.salesReport(from, to)));
    }

    @GetMapping("/reports/revenue/daily")
    public ResponseEntity<ApiEnvelope<DailyRevenueResponse>> dailyRevenue(
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminReportService.dailyRevenue(days)));
    }

    @GetMapping("/reports/products/overview")
    public ResponseEntity<ApiEnvelope<ProductOverviewResponse>> productOverview() {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminReportService.productOverview()));
    }
}
