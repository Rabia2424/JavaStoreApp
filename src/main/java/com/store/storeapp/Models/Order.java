package com.store.storeapp.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    @NotNull
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="fullName",   column=@Column(name="ship_full_name")),
            @AttributeOverride(name="line1",      column=@Column(name="ship_line1")),
            @AttributeOverride(name="line2",      column=@Column(name="ship_line2")),
            @AttributeOverride(name="district",   column=@Column(name="ship_district")),
            @AttributeOverride(name="city",       column=@Column(name="ship_city")),
            @AttributeOverride(name="country",    column=@Column(name="ship_country")),
            @AttributeOverride(name="postalCode", column=@Column(name="ship_postal_code")),
            @AttributeOverride(name="phone",      column=@Column(name="ship_phone"))
    })
    private AddressSnapshot shipping;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="fullName",   column=@Column(name="bill_full_name")),
            @AttributeOverride(name="line1",      column=@Column(name="bill_line1")),
            @AttributeOverride(name="line2",      column=@Column(name="bill_line2")),
            @AttributeOverride(name="district",   column=@Column(name="bill_district")),
            @AttributeOverride(name="city",       column=@Column(name="bill_city")),
            @AttributeOverride(name="country",    column=@Column(name="bill_country")),
            @AttributeOverride(name="postalCode", column=@Column(name="bill_postal_code")),
            @AttributeOverride(name="phone",      column=@Column(name="bill_phone"))
    })
    private AddressSnapshot billing;

    @NotNull
    private Double shippingCost;
    @Nullable
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdOn;
    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,  orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();
}
