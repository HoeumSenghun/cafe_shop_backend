package com.cafe_shop.admin.service.impl;

import com.cafe_shop.admin.dto.AdminDtos.CategoryCountResponse;
import com.cafe_shop.admin.dto.AdminDtos.DailyRevenuePoint;
import com.cafe_shop.admin.dto.AdminDtos.DailyRevenueResponse;
import com.cafe_shop.admin.dto.AdminDtos.ProductOverviewResponse;
import com.cafe_shop.admin.dto.AdminDtos.SalesReportResponse;
import com.cafe_shop.admin.service.AdminReportService;
import com.cafe_shop.payment.model.PaymentStatus;
import com.cafe_shop.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final EntityManager em;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public SalesReportResponse salesReport(Instant from, Instant to) {
        Instant rangeFrom = from != null ? from : LocalDate.now(ZoneOffset.UTC).minusDays(30).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant rangeTo = to != null ? to : Instant.now();

        BigDecimal totalRevenue = em.createQuery("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = :success
              and p.createdAt >= :from and p.createdAt < :to
        """, BigDecimal.class)
                .setParameter("success", PaymentStatus.SUCCESS)
                .setParameter("from", rangeFrom)
                .setParameter("to", rangeTo)
                .getSingleResult();

        long successfulPayments = em.createQuery("""
            select count(p)
            from Payment p
            where p.status = :success
              and p.createdAt >= :from and p.createdAt < :to
        """, Long.class)
                .setParameter("success", PaymentStatus.SUCCESS)
                .setParameter("from", rangeFrom)
                .setParameter("to", rangeTo)
                .getSingleResult();

        long ordersInPeriod = em.createQuery("""
            select count(o)
            from Order o
            where o.createdAt >= :from and o.createdAt < :to
        """, Long.class)
                .setParameter("from", rangeFrom)
                .setParameter("to", rangeTo)
                .getSingleResult();

        BigDecimal average = ordersInPeriod == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(ordersInPeriod), 2, RoundingMode.HALF_UP);

        return SalesReportResponse.builder()
                .from(rangeFrom)
                .to(rangeTo)
                .totalRevenue(totalRevenue)
                .successfulPayments(successfulPayments)
                .ordersInPeriod(ordersInPeriod)
                .averageOrderValue(average)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DailyRevenueResponse dailyRevenue(int days) {
        int safeDays = Math.min(Math.max(days, 1), 90);
        Instant start = LocalDate.now(ZoneOffset.UTC).minusDays(safeDays - 1L).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = LocalDate.now(ZoneOffset.UTC).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        List<Tuple> rows = em.createQuery("""
            select date(p.createdAt) as day, coalesce(sum(p.amount), 0) as revenue, count(p) as cnt
            from Payment p
            where p.status = :success
              and p.createdAt >= :start and p.createdAt < :end
            group by date(p.createdAt)
            order by day asc
        """, Tuple.class)
                .setParameter("success", PaymentStatus.SUCCESS)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();

        List<DailyRevenuePoint> points = new ArrayList<>();
        for (Tuple t : rows) {
            points.add(DailyRevenuePoint.builder()
                    .date(toLocalDate(t.get("day")))
                    .revenue((BigDecimal) t.get("revenue"))
                    .paymentCount(((Number) t.get("cnt")).longValue())
                    .build());
        }

        return DailyRevenueResponse.builder()
                .days(safeDays)
                .points(points)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductOverviewResponse productOverview() {
        long total = productRepository.countByIsDeletedFalse();
        long available = productRepository.countByIsDeletedFalseAndIsAvailableTrue();
        long unavailable = productRepository.countByIsDeletedFalseAndIsAvailableFalse();

        List<Tuple> categories = em.createQuery("""
            select p.category as category, count(p) as cnt
            from Product p
            where p.isDeleted = false
            group by p.category
            order by count(p) desc
        """, Tuple.class)
                .getResultList();

        List<CategoryCountResponse> byCategory = categories.stream()
                .map(t -> CategoryCountResponse.builder()
                        .category((String) t.get("category"))
                        .count(((Number) t.get("cnt")).longValue())
                        .build())
                .toList();

        return ProductOverviewResponse.builder()
                .totalProducts(total)
                .availableProducts(available)
                .unavailableProducts(unavailable)
                .byCategory(byCategory)
                .build();
    }

    private static LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate ld) {
            return ld;
        }
        if (value instanceof java.sql.Date sd) {
            return sd.toLocalDate();
        }
        if (value instanceof java.util.Date ud) {
            return ud.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        }
        throw new IllegalStateException("Unexpected date type: " + value.getClass());
    }
}
