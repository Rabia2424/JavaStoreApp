package com.store.storeapp.Repositories;

import com.store.storeapp.Models.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
    @Query("Select d from ProductDiscount d where d.product.id = :productId" +
    " and d.endDate > CURRENT_TIMESTAMP" +
    " and d.startDate < CURRENT_TIMESTAMP" +
    " and d.isActive = true")
    ProductDiscount getValidDiscountByProductId(Long productId);
}
