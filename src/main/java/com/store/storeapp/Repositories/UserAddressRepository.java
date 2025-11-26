package com.store.storeapp.Repositories;

import com.store.storeapp.Models.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);
    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAddress a SET a.defaultShipping = false WHERE a.userId = :userId and a.defaultShipping = true")
    void clearDefaultShipping(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAddress a SET a.defaultBilling = false WHERE a.userId = :userId and a.defaultBilling = true")
    void clearDefaultBilling(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
