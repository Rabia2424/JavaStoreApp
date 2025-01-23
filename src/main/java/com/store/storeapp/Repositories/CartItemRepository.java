package com.store.storeapp.Repositories;

import com.store.storeapp.Models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("Select c From CartItem c where c.cart.cartId = :cartId")
    List<CartItem> getCartItemsByCartId(Long cartId);

    @Query("Select c From CartItem c where c.cart.cartId = :cartId and c.product.id = :productId")
    Optional<CartItem> getCartItemByCartIdAndProductId(Long cartId, Long productId);
}
