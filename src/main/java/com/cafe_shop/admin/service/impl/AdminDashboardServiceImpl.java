package com.cafe_shop.admin.service.impl;

import com.cafe_shop.admin.dto.AdminDtos.DashboardResponse;
import com.cafe_shop.admin.dto.AdminDtos.OrderStatusCountResponse;
import com.cafe_shop.admin.dto.AdminDtos.OverviewStatsResponse;
import com.cafe_shop.admin.dto.AdminDtos.SalesSummaryResponse;
import com.cafe_shop.admin.dto.AdminDtos.TopProductResponse;
import com.cafe_shop.admin.service.AdminDashboardService;
import com.cafe_shop.order.model.OrderStatus;
import com.cafe_shop.order.repository.OrderRepository;
import com.cafe_shop.payment.model.PaymentStatus;
import com.cafe_shop.payment.repository.PaymentRepository;
import com.cafe_shop.product.repository.ProductRepository;
import com.cafe_shop.user.model.RoleName;
import com.cafe_shop.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        Instant start = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = LocalDate.now(ZoneOffset.UTC).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant monthStart = LocalDate.now(ZoneOffset.UTC).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        BigDecimal todaySales = sumPayments(start, end);
        BigDecimal monthToDateSales = sumPayments(monthStart, end);
        BigDecimal allTimeSales = em.createQuery("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = :success
        """, BigDecimal.class)
                .setParameter("success", PaymentStatus.SUCCESS)
                .getSingleResult();

        long todayOrders = orderRepository.findTodayOrders(start, end).size();
        long pending = orderRepository.countByStatus(OrderStatus.PENDING);
        long paid = orderRepository.countByStatus(OrderStatus.PAID);

        List<TopProductResponse> topProducts = topProductsToday(start, end);

        SalesSummaryResponse summary = SalesSummaryResponse.builder()
                .todaySales(todaySales)
                .todayOrders(todayOrders)
                .pendingOrders(pending)
                .paidOrders(paid)
                .build();

        OverviewStatsResponse overview = OverviewStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalCustomers(userRepository.countByRoleName(RoleName.CUSTOMER))
                .totalStaff(userRepository.countByRoleName(RoleName.CASHIER) + userRepository.countByRoleName(RoleName.ADMIN))
                .totalProducts(productRepository.countByIsDeletedFalse())
                .availableProducts(productRepository.countByIsDeletedFalseAndIsAvailableTrue())
                .unavailableProducts(productRepository.countByIsDeletedFalseAndIsAvailableFalse())
                .monthToDateSales(monthToDateSales)
                .allTimeSales(allTimeSales)
                .ordersByStatus(ordersByStatus())
                .build();

        return DashboardResponse.builder()
                .salesSummary(summary)
                .overview(overview)
                .topProducts(topProducts)
                .build();
    }

    private BigDecimal sumPayments(Instant from, Instant to) {
        return em.createQuery("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = :success
              and p.createdAt >= :start and p.createdAt < :end
        """, BigDecimal.class)
                .setParameter("success", PaymentStatus.SUCCESS)
                .setParameter("start", from)
                .setParameter("end", to)
                .getSingleResult();
    }

    private List<TopProductResponse> topProductsToday(Instant start, Instant end) {
        List<Tuple> top = em.createQuery("""
            select i.product.id as productId, i.product.name as name, sum(i.quantity) as qty
            from OrderItem i
            join i.order o
            where o.status in (:paid, :done)
              and o.createdAt >= :start and o.createdAt < :end
            group by i.product.id, i.product.name
            order by sum(i.quantity) desc
        """, Tuple.class)
                .setParameter("paid", OrderStatus.PAID)
                .setParameter("done", OrderStatus.DONE)
                .setParameter("start", start)
                .setParameter("end", end)
                .setMaxResults(10)
                .getResultList();

        return top.stream()
                .map(t -> TopProductResponse.builder()
                        .productId(((Number) t.get("productId")).longValue())
                        .name((String) t.get("name"))
                        .quantitySold(((Number) t.get("qty")).longValue())
                        .build())
                .toList();
    }

    private List<OrderStatusCountResponse> ordersByStatus() {
        return Arrays.stream(OrderStatus.values())
                .map(status -> OrderStatusCountResponse.builder()
                        .status(status)
                        .count(orderRepository.countByStatus(status))
                        .build())
                .toList();
    }
}
