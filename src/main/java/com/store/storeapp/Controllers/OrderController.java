package com.store.storeapp.Controllers;

import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.Order;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private CartService cartService;
    @Autowired
    private AuthService authService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/index")
    public String getOrder(@CookieValue("jwt") String token, Model model){
        Long userId = authService.getUserIdFromToken(token);
        Cart cart = cartService.getCartByUserId(userId);
        Order order = orderService.getOrderObject(cart);
        model.addAttribute("order", order);
        return "order/index";
    }


}
