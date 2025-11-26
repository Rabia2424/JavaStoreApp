package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Order;
import com.store.storeapp.Models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    long countByUserId(Long userId);

    long countByUserIdAndStatusIn(Long userId, Collection<OrderStatus> statuses);

    List<Order> findTop3ByUserIdOrderByOrderDateDesc(Long userId);

}
