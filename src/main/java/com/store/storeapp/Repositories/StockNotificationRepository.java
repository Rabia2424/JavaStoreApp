package com.store.storeapp.Repositories;

import com.store.storeapp.Models.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    List<StockNotification> findByProductIdAndNotifiedFalse(Long productId);
    @Query(
            "Select s from StockNotification s where " +
                    "s.productId = :productId and s.userEmail = :email and s.notified = false"
    )
    Optional<StockNotification> findByProductIdAndEmail(Long productId, String email);
}

