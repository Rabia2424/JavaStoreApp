package com.store.storeapp.Repositories;

import com.store.storeapp.Models.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
    @Query("Select d from ProductDiscount d where d.product.id = :productId" +
    " AND d.isActive = true" +
    " AND d.startDate <= CURRENT_TIMESTAMP" +
    " AND (d.endDate IS NULL OR d.endDate >= CURRENT_TIMESTAMP)")
    ProductDiscount getValidDiscountByProductId(Long productId);

    @Modifying
    @Query("UPDATE ProductDiscount d SET d.isActive = false WHERE d.endDate < CURRENT_TIMESTAMP AND d.isActive = true")
    void deactivateExpiredDiscounts();
}
