package com.store.storeapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatsDto {
    private long totalOrders;
    private long activeOrders;
    private long addressCount;
    private long favoriteCount;
}
