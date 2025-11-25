package com.store.storeapp.DTOs;

import com.store.storeapp.Models.AddressSnapshot;
import com.store.storeapp.Models.OrderStatusView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {

    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatusView status;
    private Double totalAmount;
    private Double shippingCost;
    private String notes;

    private AddressSnapshot shipping;
    private AddressSnapshot billing;

    private List<OrderItemResponseDto> items;
}