package com.store.storeapp.jobs;

import com.store.storeapp.Models.Order;
import com.store.storeapp.Models.OrderStatus;
import com.store.storeapp.Repositories.OrderRepository;
import com.store.storeapp.Services.impl.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCleanupTask {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cancelExpiredOrders() {

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Order> expiredOrders = orderRepository
                .findByStatusAndOrderDateBefore(OrderStatus.CREATED, threshold);

        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            inventoryService.releaseReservations(order.getOrderId());
        }
    }
}

