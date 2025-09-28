package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.OPTIMISTIC) // veya PESSIMISTIC_WRITE; DB türüne ve trafiğe göre
    @Query("select i from Inventory i where i.productId = :productId")
    Optional<Inventory> findForUpdate(@Param("productId") Long productId);

    // Koşullu UPDATE paterni (oversell’i tek sorguda engellemek için—opsiyonel)
    @Modifying
    @Query("""
       update Inventory i
          set i.reserved = i.reserved + :qty
        where i.productId = :productId
          and (i.onHand - i.reserved) >= :qty
    """)
    int tryReserve(@Param("productId") Long productId, @Param("qty") int qty);
}
