package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.OrderItemResponseDto;
import com.store.storeapp.DTOs.OrderResponseDto;
import com.store.storeapp.Models.*;
import com.store.storeapp.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private InventoryService inventoryService;

    public Optional<Order> getOrderById(Long orderId){
        return orderRepository.getOrderById(orderId);
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

    @Transactional
    public Order save(Order order){
        orderRepository.save(order);

        List<CartItem> cartItemList = cartService.getCartByUserId(order.getUserId()).getCartItems();
        List<OrderItem> orderItemList = cartItemList.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            Duration ttl = Duration.ofMinutes(10);
            inventoryService.reserveForOrder(order.getOrderId(),orderItem.getProduct().getId(),
                    orderItem.getQuantity(),ttl);
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItemList);
        return orderRepository.save(order);
    }

    public void update(Order order){
        orderRepository.save(order);
    }

    @Transactional
    public void markPending(Long orderId, OrderStatus status){
        orderRepository.updateStatus(orderId,status);
    }

    @Transactional
    public void markCancelled(Long orderId, OrderStatus status){
        orderRepository.updateStatus(orderId, status);
    }

    public List<OrderResponseDto> getOrdersByUserId(Long userId) {

        List<Order> orders = orderRepository.findByUserIdOrderByOrderIdDesc(userId);

        return orders.stream().map(order -> {

            OrderStatusView view = OrderStatusMapper.toView(order.getStatus());

            List<OrderItemResponseDto> items = order.getOrderItems().stream()
                    .map(i -> new OrderItemResponseDto(
                            i.getProduct().getId(),
                            i.getProductName(),
                            i.getProduct().getImageUrl(),
                            i.getQuantity(),
                            i.getUnitPrice(),
                            i.getTotalPrice()
                    )).toList();

            return new OrderResponseDto(
                    order.getOrderId(),
                    order.getCreatedOn(),
                    view,
                    order.getTotalAmount(),
                    items
            );

        }).toList();
    }

}
