package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r where r.orderId = :orderId and r.status = 'ACTIVE'")
    List<Reservation> findActiveByOrderId(@Param("orderId") Long orderId);

    @Query("select r from Reservation r where r.status = 'ACTIVE' and r.expiresAt <= :now")
    List<Reservation> findExpired(@Param("now") LocalDateTime now);
}
