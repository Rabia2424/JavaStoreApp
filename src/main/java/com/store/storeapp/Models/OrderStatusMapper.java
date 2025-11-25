package com.store.storeapp.Models;

public class OrderStatusMapper {
    public static OrderStatusView toView(OrderStatus status) {
        return switch (status) {
            case CREATED   -> new OrderStatusView("Awaiting Payment", "gray", 0);
            case PENDING   -> new OrderStatusView("Processing", "orange", 1);
            case SHIPPED   -> new OrderStatusView("Shipped", "blue", 2);
            case DELIVERED -> new OrderStatusView("Delivered", "green", 3);
            case CANCELLED -> new OrderStatusView("Cancelled", "red", -1);
        };
    }

}
