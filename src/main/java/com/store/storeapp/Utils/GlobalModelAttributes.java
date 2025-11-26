package com.store.storeapp.Utils;

import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.ProductService;
import com.store.storeapp.Services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private ProductService productService;

    private AuthService authService;
    private CartService cartService;
    @Autowired
    private UserService userService;
    public GlobalModelAttributes(ProductService productService, AuthService authService, CartService cartService) {
        this.productService = productService;
        this.authService = authService;
        this.cartService = cartService;
    }

    @ModelAttribute("popularProducts")
    public Page<Product> addPopularProductsToModel() {
        Pageable pageable = PageRequest.of(0,5);
        return productService.getPopularProducts(pageable);
    }

    @ModelAttribute("cartCount")
    public int getCartItemCount(@CookieValue(value = "jwt", required = false) String token){
        if(token == null || token.isBlank()){
            return 0;
        }
        try{
            Long userId = authService.getUserIdFromToken(token);
            Cart cart = cartService.getCartByUserId(userId);
            return cart != null && cart.getCartItems() != null ? cart.getCartItems().size() : 0;
        }catch(Exception e){
            return 0;
        }
    }

    @ModelAttribute("avatarUrl")
    public String addNavbarAvatar(@CookieValue(value="jwt", required=false) String token) {

        if (token == null || token.isBlank()) return null;

        try {
            Long userId = authService.getUserIdFromToken(token);
            if (userId == null) return null;

            return userService.getUserById(userId)
                    .map(user -> user.getAvatarUrl())
                    .orElse(null);

        } catch (Exception e) {
            return null;
        }
    }

}
