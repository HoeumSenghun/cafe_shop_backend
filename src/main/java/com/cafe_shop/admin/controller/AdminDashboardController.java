package com.cafe_shop.admin.controller;

import com.cafe_shop.admin.dto.AdminDtos.DashboardResponse;
import com.cafe_shop.admin.service.AdminDashboardService;
import com.cafe_shop.common.api.ApiEnvelope;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiEnvelope<DashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiEnvelope.ok("OK", adminDashboardService.dashboard()));
    }
}

