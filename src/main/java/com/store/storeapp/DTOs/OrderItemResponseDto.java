package com.store.storeapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
}