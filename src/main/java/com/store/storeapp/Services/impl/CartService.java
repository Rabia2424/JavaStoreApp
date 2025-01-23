package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.CartItem;
import com.store.storeapp.Models.CartStatus;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.CartItemRepository;
import com.store.storeapp.Repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public Cart getCartByUserId(Long userId){
        return cartRepository.getCartByUserId(userId)
                .orElse(null);
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
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItem.setTotalPrice(cartItem.getTotalPrice() - product.getPrice());
        }else{
            throw new Exception("Cart Item not found related this product");
        }

        cart.calculateTotalPayment(cart.getCartItems());
        return cartRepository.save(cart);
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

            cart.getCartItems().add(cartItem);
        }

        cart.calculateTotalPayment(cart.getCartItems());
        return cartRepository.save(cart);
    }

    public Optional<CartItem> cartItemExistOrNot(Cart cart, Product product){
        if(cart.getCartItems() != null){
            return cart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                    .findFirst();
        }else{
            return Optional.empty();
        }
    }

}
