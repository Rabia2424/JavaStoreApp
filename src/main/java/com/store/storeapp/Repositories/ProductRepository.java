package com.store.storeapp.Repositories;

import com.store.storeapp.DTOs.TopSellingProductDto;
import com.store.storeapp.Models.OrderStatus;
import com.store.storeapp.Models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByName(String url);

    @Query("Select p From Product p where p.id = :id")
    Product getProductByProductId(Long id);

    @Query("Select p From Product p where p.category.id = :categoryId ")
    Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);


    @Query("Select p From Product p where (:categoryId is null or p.category.id = :categoryId) " +
            " and (:minPrice is null or p.price >= :minPrice) " +
            "and (:maxPrice is null or p.price <= :maxPrice)")
    Page<Product> findFilteredProducts(@Param("categoryId") Long categoryId,
                                       @Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice,
                                       Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderItem o ON o.product.id = p.id " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(o.id) DESC")
    Page<Product> getPopularProducts(Pageable pageable);

    @Query("""
  select p from Product p
    join ProductDiscount d on d.product = p
   where d.isActive = true
     and d.startDate <= CURRENT_TIMESTAMP
     and (d.endDate is null or d.endDate >= CURRENT_TIMESTAMP)
   group by p.id
   order by max(d.discountRate) desc
""")
    Page<Product> findDiscountedProducts(Pageable pageable);

    @Query("""
       select new com.store.storeapp.DTOs.TopSellingProductDto(
            p.sku,
            p.name,
            p.price,
            sum(oi.quantity),
            sum(oi.totalPrice)
       )
       from OrderItem oi
       join oi.product p
       join oi.order o
       where o.status <> :cancelled
       group by p.sku, p.name, p.price
       order by sum(oi.quantity) desc
       """)
    Page<TopSellingProductDto> findTopSellingProducts(@Param("cancelled") OrderStatus cancelled,
                                                      Pageable pageable);
}
