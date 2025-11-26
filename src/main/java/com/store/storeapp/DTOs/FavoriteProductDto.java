package com.store.storeapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProductDto {
    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
}
