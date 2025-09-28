package com.store.storeapp.jobs;

import com.store.storeapp.Services.impl.InventoryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReservationCleanupJob {

    private final InventoryService inventoryService;

    public ReservationCleanupJob(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @Scheduled(fixedDelay = 300_000)
    public void cleanup() {
        int released = inventoryService.releaseExpired();
        if (released > 0) {
            System.out.println("Released expired reservations: " + released);
        }
    }
}