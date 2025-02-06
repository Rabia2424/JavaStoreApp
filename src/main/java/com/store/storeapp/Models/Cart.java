package com.store.storeapp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Setter;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    private Long userId;
    private Double totalPayment;

    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,  orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> cartItems = new ArrayList<>();

    public void calculateTotalPayment(List<CartItem> cartItems){
        totalPayment = 0.0;
        for(CartItem cartItem: cartItems){
            totalPayment += cartItem.getTotalPrice();
        }
    }
}
