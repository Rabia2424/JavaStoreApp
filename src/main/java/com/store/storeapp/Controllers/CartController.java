package com.store.storeapp.Controllers;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.DTOs.ProductInfoBundle;
import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.CartItem;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Models.ProductDiscount;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.ProductDiscountService;
import com.store.storeapp.Services.impl.ProductService;
import com.store.storeapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private ProductDiscountService discountService;

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
        ProductInfoBundle info = cartService.getProductInfo(token, productId);
        Long userId = info.userId();
        Product product = info.product();
        ProductDiscount discount = discountService.getValidDiscountByProductId(productId);
        if(discount != null)
        {
            Double priceWithDiscount = product.getPrice() - ((product.getPrice() * discount.getDiscountRate()) / 100);
            product.setPrice(priceWithDiscount);
        }
        cartService.addToCart(userId, product);
        return "redirect:/cart/list";
    }

    @PostMapping("/remove/{productId}")
    public String deleteFromCart(@CookieValue("jwt") String token, @PathVariable("productId") Long productId) throws Exception {
        ProductInfoBundle info = cartService.getProductInfo(token, productId);
        Long userId = info.userId();
        Product product = info.product();
        cartService.deleteCartItemFromCart(userId, product);
        return "redirect:/cart/list";
    }

    @PostMapping("/removeOneItem")
    public String deleteOneItemFromCartItems(@CookieValue("jwt") String token,
                                             @RequestParam("id") Long productId,
                                             @RequestParam("price") Double price) throws Exception {
        ProductInfoBundle info = cartService.getProductInfo(token, productId);
        Long userId = info.userId();
        Product product = info.product();
        product.setPrice(price);
        cartService.deleteOneItemFromCartItem(userId, product);
        return "redirect:/cart/list";
    }

    @PostMapping("/add-json/{id}")
    @ResponseBody
    public ResponseEntity<?> addToCartJson(@CookieValue("jwt") String token, @PathVariable("id") Long productId) {
        try {
            ProductInfoBundle info = cartService.getProductInfo(token, productId);
            Long userId = info.userId();
            Product product = info.product();

            ProductDiscount discount = discountService.getValidDiscountByProductId(productId);

            if(discount != null) {
                Double priceWithDiscount = product.getPrice() - ((product.getPrice() * discount.getDiscountRate()) / 100);
                product.setPrice(priceWithDiscount);
            }

            cartService.addToCart(userId, product);

            int cartCount = cartService.getCartItemCount(userId);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Product added to cart",
                    "cartCount", cartCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<?> getCartCount(@CookieValue("jwt") String token) {
        try {
            Long userId = authService.getUserIdFromToken(token);
            int count = cartService.getCartItemCount(userId);

            return ResponseEntity.ok().body(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.ok().body(Map.of("count", 0));
        }
    }

}
