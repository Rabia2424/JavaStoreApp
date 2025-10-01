package com.store.storeapp.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.storeapp.Models.*;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public String saveOrder(@ModelAttribute Order order, Model model) throws JsonProcessingException {
        Order newOrder = orderService.save(order);
        // Order nesnesini JSON formatına dönüştür
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register Java 8 DateTime modules.
        String orderJson = objectMapper.writeValueAsString(newOrder);
        //System.out.println("Generated JSON: " + orderJson);
        CardInfo paymentCard = new CardInfo();
        model.addAttribute("orderJson", orderJson);
        model.addAttribute("paymentCard", paymentCard);
        //orderService.save(order);
        return "payment/index";
    }


}
