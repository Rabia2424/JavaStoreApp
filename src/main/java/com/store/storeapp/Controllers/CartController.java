package com.store.storeapp.Controllers;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.CartItem;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.ProductService;
import com.store.storeapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;
    @Autowired
    private AuthService authService;

    @GetMapping("/list")
    public String getCart(@CookieValue("jwt") String token, Model model){
        Long userId = authService.getUserIdFromToken(token);
        Cart cart = cartService.getCartByUserId(userId);
        if(cart == null){
            cart = new Cart();
            cart.setCartItems(new ArrayList<>());
            cart.setTotalPayment(0.0);
        }
        model.addAttribute("cart", cart);
        return "cart/index";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@CookieValue("jwt") String token, @PathVariable("id") Long productId){
        Long userId = authService.getUserIdFromToken(token);
        ProductDto productDto = productService.getProductById(productId);
        Product product = productService.mapToProduct(productDto);
        cartService.addToCart(userId, product);
        return "redirect:/cart/list";
    }

    @PostMapping("/remove/{productId}")
    public String deleteFromCart(@CookieValue("jwt") String token, @PathVariable("productId") Long productId) throws Exception {
        Long userId = authService.getUserIdFromToken(token);
        ProductDto productDto = productService.getProductById(productId);
        Product product = productService.mapToProduct(productDto);
        cartService.deleteCartItemFromCart(userId, product);
        return "redirect:/cart/list";
    }

    @PostMapping("/removeOneItem/{productId}")
    public String deleteOneItemFromCartItems(@CookieValue("jwt") String token, @PathVariable("productId") Long productId) throws Exception {
        Long userId = authService.getUserIdFromToken(token);
        ProductDto productDto = productService.getProductById(productId);
        Product product = productService.mapToProduct(productDto);
        cartService.deleteOneItemFromCartItem(userId, product);
        return "redirect:/cart/list";
    }

}
