package com.store.storeapp.DTOs;

import com.store.storeapp.Models.OrderStatusView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatusView status;
    private Double totalPrice;
    private List<OrderItemResponseDto> items;
}
