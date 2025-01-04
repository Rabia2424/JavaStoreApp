package com.store.storeapp.DTOs;

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
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    private String categoryName;
}
