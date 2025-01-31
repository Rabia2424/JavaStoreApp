package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("Select o from Order o where o.orderId = :orderId")
    Optional<Order> getOrderById(Long orderId);
}
