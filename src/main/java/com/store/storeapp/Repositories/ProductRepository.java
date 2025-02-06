package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String url);
    @Query("SELECT p FROM Product p WHERE " +
            "p.name LIKE CONCAT('%',:query, '%')" +
            "Or p.description LIKE CONCAT('%', :query, '%')")
    List<Product> searchProducts(String query);

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
}
