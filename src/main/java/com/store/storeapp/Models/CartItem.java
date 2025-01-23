package com.store.storeapp.Models;

import lombok.*;

import javax.persistence.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    private Integer quantity;
    private Double totalPrice;

//    public void calculateTotalPrice(){
//        totalPrice = 0.0;
//        if(product != null && quantity != null){
//            totalPrice = product.getPrice() * quantity;
//        }
//    }
}
