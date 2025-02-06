package com.store.storeapp.Models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProductDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    private double discountRate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
}
