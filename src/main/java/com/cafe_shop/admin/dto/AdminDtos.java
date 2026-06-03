package com.cafe_shop.admin.dto;

import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.user.model.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    public record OrderStatusCountResponse(
            OrderStatus status,
            long count
    ) {}

    @Builder
    public record OverviewStatsResponse(
            long totalUsers,
            long totalCustomers,
            long totalStaff,
            long totalProducts,
            long availableProducts,
            long unavailableProducts,
            BigDecimal monthToDateSales,
            BigDecimal allTimeSales,
            List<OrderStatusCountResponse> ordersByStatus
    ) {}

    @Builder
    public record DashboardResponse(
            SalesSummaryResponse salesSummary,
            OverviewStatsResponse overview,
            List<TopProductResponse> topProducts
    ) {}

    @Builder
    public record UserResponse(
            Long id,
            String email,
            String fullName,
            boolean enabled,
            Set<RoleName> roles,
            Instant createdAt
    ) {}

    @Builder
    public record CreateStaffRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 2, max = 120) String fullName,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotNull RoleName role
    ) {}

    @Builder
    public record UpdateUserEnabledRequest(
            @NotNull Boolean enabled
    ) {}

    @Builder
    public record SalesReportResponse(
            Instant from,
            Instant to,
            BigDecimal totalRevenue,
            long successfulPayments,
            long ordersInPeriod,
            BigDecimal averageOrderValue
    ) {}

    @Builder
    public record DailyRevenuePoint(
            LocalDate date,
            BigDecimal revenue,
            long paymentCount
    ) {}

    @Builder
    public record DailyRevenueResponse(
            int days,
            List<DailyRevenuePoint> points
    ) {}

    @Builder
    public record ProductOverviewResponse(
            long totalProducts,
            long availableProducts,
            long unavailableProducts,
            List<CategoryCountResponse> byCategory
    ) {}

    @Builder
    public record CategoryCountResponse(
            String category,
            long count
    ) {}
}
