package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Cart;
import com.store.storeapp.Models.Order;
import com.store.storeapp.Models.OrderStatus;
import com.store.storeapp.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order getOrderObject(Cart cart){
        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setTotalAmount(cart.getTotalPayment());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }
}
