package com.cafe_shop.admin.service;

import com.cafe_shop.admin.dto.AdminDtos.DailyRevenueResponse;
import com.cafe_shop.admin.dto.AdminDtos.ProductOverviewResponse;
import com.cafe_shop.admin.dto.AdminDtos.SalesReportResponse;

import java.time.Instant;

public interface AdminReportService {
    SalesReportResponse salesReport(Instant from, Instant to);
    DailyRevenueResponse dailyRevenue(int days);
    ProductOverviewResponse productOverview();
}
