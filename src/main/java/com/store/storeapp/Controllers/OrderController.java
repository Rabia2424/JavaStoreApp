package com.store.storeapp.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.storeapp.DTOs.OrderDetailDto;
import com.store.storeapp.DTOs.OrderResponseDto;
import com.store.storeapp.Models.*;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.AddressService;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private CartService cartService;
    @Autowired
    private AuthService authService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;

    @GetMapping("/index")
    public String getOrder(@CookieValue("jwt") String token, Model model){
        Long userId = authService.getUserIdFromToken(token);

        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return "redirect:/cart/list";
        }
        model.addAttribute("cartItems", cart.getCartItems());

        double subtotal = cart.getCartItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        double shippingCost = subtotal >= 1000 ? 0 : 10;

        Order draft = new Order();
        draft.setUserId(userId);
        draft.setOrderDate(LocalDateTime.now());
        draft.setStatus(OrderStatus.CREATED);
        draft.setTotalAmount(subtotal);
        draft.setShippingCost(shippingCost);
        model.addAttribute("order", draft);


        List<UserAddress> addresses =
                Optional.ofNullable(addressService.listByUser(userId)).orElse(Collections.emptyList());
        List<UserAddress> shippingAddresses = addresses;
        List<UserAddress> billingAddresses  = addresses;

        model.addAttribute("shippingAddresses", shippingAddresses);
        model.addAttribute("billingAddresses", billingAddresses);


        model.addAttribute("hasNoAddress", addresses.isEmpty());
        return "order/index";
    }

    @PostMapping("/add")
    @Transactional
    public String saveOrder( @RequestParam Long userId,
                             @RequestParam Long shippingAddressId,
                             @RequestParam(required = false) Long billingAddressId,
                             @RequestParam(name="sameAsShipping", defaultValue="false") boolean sameAsShipping,
                             @RequestParam(required = false) String notes)
    {
        UserAddress shipUA = addressService.getByIdAndUser(shippingAddressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Shipping address not found"));

        UserAddress billUA = sameAsShipping ? shipUA :
                addressService.getByIdAndUser(billingAddressId, userId)
                        .orElseThrow(() -> new IllegalArgumentException("Billing address not found"));

        Cart cart = cartService.getCartByUserId(userId);
        double subtotal = cart.getCartItems().stream()
                .mapToDouble(CartItem::getTotalPrice).sum();

        double shippingCost = subtotal >= 1000 ? 0 : 10;

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setNotes(notes);
        order.setShippingCost(shippingCost);
        order.setTotalAmount(subtotal);
        order.setShipping(AddressMapper.toSnapshot(shipUA));
        order.setBilling(AddressMapper.toSnapshot(billUA));

        order = orderService.save(order);

        return "redirect:/payment/index?orderId=" + order.getOrderId();
    }

    @GetMapping("/list")
    public String listOrders(@CookieValue("jwt") String token, Model model) {

        Long userId = authService.getUserIdFromToken(token);
        List<OrderResponseDto> orders = orderService.getOrdersByUserId(userId);

        model.addAttribute("orders", orders);
        model.addAttribute("hasOrders", !orders.isEmpty());
        return "order/order-list";
    }


    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable("id") Long orderId,
                              @CookieValue("jwt") String token,
                              Model model) {

        Long userId = authService.getUserIdFromToken(token);

        OrderDetailDto detail = orderService.getOrderDetail(orderId, userId);

        model.addAttribute("order", detail);
        return "order/order-detail";
    }

}
