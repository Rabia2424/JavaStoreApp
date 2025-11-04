package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndProduct_Id(Long userId, Long productId);

    @Transactional
    void deleteByUserIdAndProduct_Id(Long userId, Long productId);

    long countByProduct_Id(Long productId);

    @Query("select f.product.id from Favorite f where f.userId = :userId")
    List<Long> findProductIdsByUserId(Long userId);

    Page<Favorite> findByUserId(Long userId, Pageable pageable);
}