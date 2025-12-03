package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.DTOs.ProductInfoBundle;
import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.CartItem;
import com.store.storeapp.Models.CartStatus;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.CartItemRepository;
import com.store.storeapp.Repositories.CartRepository;
import com.store.storeapp.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private ProductService productService;

    public Cart getCartByUserId(Long userId){
        return cartRepository.getCartByUserId(userId)
                .orElse(null);
    }

    public Cart updateCart(Cart cart){
        return cartRepository.save(cart);
    }

    public Cart deleteCartItemFromCart(Long userId, Product product) throws Exception {
        Cart cart = getCartByUserId(userId);
        Optional<CartItem> existingCartItem;

        existingCartItem = cartItemExistOrNot(cart, product);

        if(existingCartItem.isPresent()){
            CartItem cartItem = existingCartItem.get();
            cart.getCartItems().remove(cartItem);

            cart.calculateTotalPayment(cart.getCartItems());
            return cartRepository.save(cart);
        }else{
            throw new Exception("Cart Item does not found!");
        }
    }

    public Cart deleteOneItemFromCartItem(Long userId, Product product) throws Exception{
        Cart cart = getCartByUserId(userId);
        Optional<CartItem> existingCartItem;

        existingCartItem = cartItemExistOrNot(cart, product);
        if(existingCartItem.isPresent()){
            CartItem cartItem = existingCartItem.get();
            if(cartItem.getQuantity() == 1){
                cart.getCartItems().remove(cartItem);
            }else{
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItem.setTotalPrice(cartItem.getTotalPrice() - product.getPrice());
            }

        }else{
            throw new Exception("Cart Item not found related this product");
        }

        cart.calculateTotalPayment(cart.getCartItems());
        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteAllCartItems(Cart cart) throws Exception {
        List<CartItem> cartItemsCopy = new ArrayList<>(cart.getCartItems());
        for(CartItem cartItem : cartItemsCopy){
            deleteCartItemFromCart(cart.getUserId(), cartItem.getProduct());
        }
    }

    public Cart addToCart(Long userId, Product product){
        Cart cart = getCartByUserId(userId);
        Optional<CartItem> existingCartItem;

        if(cart == null){
            Cart newCart = Cart
                    .builder()
                    .userId(userId)
                    .totalPayment(0.0)
                    .status(CartStatus.ACTIVE)
                    .build();
            cart = newCart;
            cartRepository.save(newCart);
        }

        existingCartItem = cartItemExistOrNot(cart, product);

        if(existingCartItem.isPresent()){
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setTotalPrice(cartItem.getQuantity() * product.getPrice());
        }else{
            CartItem cartItem = CartItem
                    .builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1)
                    .totalPrice(product.getPrice())
                    .build();

            //cartItemRepository.getCartItemsByCartId(cart.getCartId()).add(cartItem);
            cart.getCartItems().add(cartItem);
        }

        cart.setStatus(CartStatus.ACTIVE);
        cart.calculateTotalPayment(cart.getCartItems());
        return cartRepository.save(cart);
    }

    public Optional<CartItem> cartItemExistOrNot(Cart cart, Product product){
            return cart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                    .findFirst();
    }

    public int getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        return cart == null ? 0 : cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public ProductInfoBundle getProductInfo(String token, Long productId){
        Long userId = authService.getUserIdFromToken(token);
        ProductDto productDto = productService.getProductById(productId);
        Product product = productService.mapToProduct(productDto);
        return new ProductInfoBundle(userId, product);
    }

}
