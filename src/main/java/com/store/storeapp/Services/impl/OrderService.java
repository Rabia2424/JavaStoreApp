package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.AccountStatsDto;
import com.store.storeapp.DTOs.OrderDetailDto;
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

        return orders.stream()
                .map(this::toOrderResponseDto)
                .toList();
    }

    @Transactional()
    public OrderDetailDto getOrderDetail(Long orderId, Long userId) {

        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatusView statusView = OrderStatusMapper.toView(order.getStatus());

        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();

        return new OrderDetailDto(
                order.getOrderId(),
                order.getOrderDate(),
                statusView,
                order.getTotalAmount(),
                order.getShippingCost(),
                order.getNotes(),
                order.getShipping(),
                order.getBilling(),
                items
        );
    }

    private OrderResponseDto toOrderResponseDto(Order order) {
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
                order.getOrderDate(),
                view,
                order.getTotalAmount(),
                items
        );
    }

    public List<OrderResponseDto> getRecentOrders(Long userId, int limit) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderIdDesc(userId);
        return orders.stream()
                .limit(limit)
                .map(this::toOrderResponseDto)
                .toList();
    }

    public AccountStatsDto getAccountStats(Long userId,
                                           long addressCount,
                                           long favoriteCount) {

        List<Order> orders = orderRepository.findByUserIdOrderByOrderIdDesc(userId);

        long total = orders.size();
        long active = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.DELIVERED)
                .count();

        return new AccountStatsDto(total, active, addressCount, favoriteCount);
    }


}
