package com.store.storeapp.Repositories;

import com.store.storeapp.DTOs.DailySalesSummary;
import com.store.storeapp.Models.Order;
import com.store.storeapp.Models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("Select o from Order o where o.orderId = :orderId")
    Optional<Order> getOrderById(Long orderId);

    @Modifying
    @Query("Update Order o set o.status = :status where o.orderId = :id")
    int updateStatus(@Param("id") Long id, @Param("status") OrderStatus status);

    List<Order> findByUserIdOrderByOrderIdDesc(Long userId);

    List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime time);

    Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId);

    @Query(value = """
    WITH RECURSIVE date_range AS (
        SELECT :fromDate as date
        UNION ALL
        SELECT DATE_ADD(date, INTERVAL 1 DAY)
        FROM date_range
        WHERE date < CURDATE()
    )
    SELECT 
        dr.date,
        COALESCE(COUNT(o.order_id), 0) as orderCount,
        COALESCE(SUM(o.total_amount + COALESCE(o.shipping_cost, 0)), 0) as totalRevenue
    FROM date_range dr
    LEFT JOIN orders o ON DATE(o.order_date) = dr.date 
        AND o.status <> 'CANCELLED'
    GROUP BY dr.date
    ORDER BY dr.date
    """, nativeQuery = true)
    List<Object[]> getDailySalesSummaryNative(@Param("fromDate") LocalDate fromDate);

    long countByOrderDateBetweenAndStatusNot(LocalDateTime start,
                                             LocalDateTime end,
                                             OrderStatus status);

    @Query("""
           select coalesce(sum(o.totalAmount + o.shippingCost), 0)
           from Order o
           where o.orderDate between :start and :end
             and o.status <> :cancelled
           """)
    Double sumRevenueBetween(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             @Param("cancelled") OrderStatus cancelled);

    long countByStatus(OrderStatus status);

    long countByStatusAndOrderDateBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end);

}
