package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.*;
import com.store.storeapp.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;

    public Order getOrderObject(Cart cart){
        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setTotalAmount(cart.getTotalPayment());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    public Order getOrderById(Long orderId){
        return orderRepository.getOrderById(orderId)
                .orElse(null);
    }

    public void deleteOrderById(Long orderId){
        orderRepository.deleteById(orderId);
    }

    public List<Order> findAllOrders(){
        List<Order> orders = orderRepository.findAll(Sort.by("orderId").descending())
                .stream()
                .filter(order -> !order.getStatus().equals(OrderStatus.DELIVERED))
                .collect(Collectors.toList());
        return orders;
    }
    public Order save(Order order){
        List<CartItem> cartItemList = cartService.getCartByUserId(order.getUserId()).getCartItems();
        List<OrderItem> orderItemList = cartItemList.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItemList);
        return orderRepository.save(order);
    }

    public void update(Order order){
        orderRepository.save(order);
    }
}
