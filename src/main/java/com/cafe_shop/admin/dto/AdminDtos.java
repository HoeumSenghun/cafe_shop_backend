package com.cafe_shop.admin.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public class AdminDtos {

    @Builder
    public record SalesSummaryResponse(
            BigDecimal todaySales,
            long todayOrders,
            long pendingOrders,
            long paidOrders
    ) {}

    @Builder
    public record TopProductResponse(
            Long productId,
            String name,
            long quantitySold
    ) {}

    @Builder
    public record DashboardResponse(
            SalesSummaryResponse salesSummary,
            List<TopProductResponse> topProducts
    ) {}
}

