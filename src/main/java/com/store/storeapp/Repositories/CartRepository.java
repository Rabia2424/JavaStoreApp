package com.store.storeapp.Repositories;

import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("Select c From Cart c where c.userId = :userId")
    Optional<Cart> getCartByUserId(Long userId);
}
