package com.store.storeapp.DTOs;

import com.store.storeapp.Models.Category;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;
    private Double discountRate;
    private Double discountedPrice;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    private Category category;
}
