package com.store.storeapp.Services.impl;

import com.store.storeapp.Exceptions.OutOfStockException;
import com.store.storeapp.Models.Inventory;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Models.Reservation;
import com.store.storeapp.Models.ReservationStatus;
import com.store.storeapp.Repositories.InventoryRepository;
import com.store.storeapp.Repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepo;
    private final ReservationRepository reservationRepo;

    public InventoryService(InventoryRepository inventoryRepo, ReservationRepository reservationRepo) {
        this.inventoryRepo = inventoryRepo;
        this.reservationRepo = reservationRepo;
    }

    @Transactional
    public void saveOrUpdateInventory(Product product){
        if(product.getId() == null){
            throw new IllegalStateException("Product must be persisted!");
        }

        int onHand  = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        Inventory inv = inventoryRepo.findForUpdate(product.getId())
                .orElseGet(() ->{
                    Inventory i = new Inventory();
                    i.setProduct(product);
                    i.setReserved(0);
                    return i;
                });

        int reserved = inv.getReserved() != null ? inv.getReserved() : 0;
        if(onHand < reserved){
            throw new IllegalArgumentException("On-hand ("+onHand+") cannot be less than reserved ("+reserved+").");
        }

        inv.setOnHand(onHand);
        inventoryRepo.save(inv);
    }

    @Transactional
    public void reserveForOrder(Long orderId, Long productId, int qty, Duration ttl) {

        int updated = inventoryRepo.tryReserve(productId, qty);
        if (updated == 0) {
            throw new OutOfStockException("Yetersiz stok (available < qty)");
        }


        Reservation res = new Reservation();
        res.setOrderId(orderId);
        res.setProduct(new Product());
        res.getProduct().setId(productId);
        res.setQty(qty);
        res.setExpiresAt(LocalDateTime.now().plus(ttl));
        res.setStatus(ReservationStatus.ACTIVE);

        Reservation saved = reservationRepo.save(res);

        System.out.println("Reservation created: " + saved);
    }

    @Transactional
    public void reserveForOrderWithLock(Long orderId, Long productId, int qty, Duration ttl) {
        Inventory inv = inventoryRepo.findForUpdate(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));

        int available = inv.getOnHand() - inv.getReserved();
        if (available < qty) {
            throw new OutOfStockException("Yetersiz stok (available=" + available + ")");
        }
        inv.setReserved(inv.getReserved() + qty);
        inventoryRepo.save(inv);

        Reservation res = new Reservation();
        res.setOrderId(orderId);
        res.setProduct(inv.getProduct());
        res.setQty(qty);
        res.setExpiresAt(LocalDateTime.now().plus(ttl));
        res.setStatus(ReservationStatus.ACTIVE);
        reservationRepo.save(res);
    }

    @Transactional
    public void consumeReservations(Long orderId) {
        List<Reservation> actives = reservationRepo.findActiveByOrderId(orderId);
        for (Reservation r : actives) {
            Inventory inv = inventoryRepo.findForUpdate(r.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory not found"));

            System.out.println("DEBUG reserved=" + inv.getReserved() +
                    ", onHand=" + inv.getOnHand() +
                    ", qty=" + r.getQty());

            inv.setReserved(inv.getReserved() - r.getQty());
            inv.setOnHand(inv.getOnHand() - r.getQty());
            inventoryRepo.save(inv);

            System.out.println("AFTER reserved=" + inv.getReserved() +
                    ", onHand=" + inv.getOnHand());

            r.setStatus(ReservationStatus.CONSUMED);
        }
        reservationRepo.saveAll(actives);
    }


    @Transactional
    public void releaseReservations(Long orderId) {
        List<Reservation> actives = reservationRepo.findActiveByOrderId(orderId);
        for (Reservation r : actives) {
            Inventory inv = inventoryRepo.findForUpdate(r.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory not found"));

            System.out.println("Debug reserved=" + inv.getReserved()
                    + ", onHand=" + inv.getOnHand()
                    + ", qty=" + r.getQty()
                    );

            inv.setReserved(inv.getReserved() - r.getQty());
            inventoryRepo.save(inv);

            System.out.println("After reserved=" + inv.getReserved()
                    + ", onHand=" + inv.getOnHand()
                    );

            r.setStatus(ReservationStatus.RELEASED);
        }
        reservationRepo.saveAll(actives);
        System.out.println(actives);
    }


    @Transactional
    public int releaseExpired() {
        List<Reservation> expired = reservationRepo.findExpired(LocalDateTime.now());
        for (Reservation r : expired) {
            Inventory inv = inventoryRepo.findForUpdate(r.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory not found"));

            inv.setReserved(inv.getReserved() - r.getQty());
            inventoryRepo.save(inv);

            r.setStatus(ReservationStatus.RELEASED);
        }
        reservationRepo.saveAll(expired);
        return expired.size();
    }


}
