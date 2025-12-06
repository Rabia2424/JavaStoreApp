package com.store.storeapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductDto {

    private String sku;
    private String name;
    private Double price;
    private Long totalSold;
    private Double totalRevenue;
}
