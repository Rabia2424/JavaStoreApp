package com.store.storeapp.Controllers;

import com.store.storeapp.Services.impl.StockNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/notifications")
public class StockNotificationController {

    @Autowired
    private StockNotificationService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam Long productId,
                                           @RequestParam String email) {
        boolean created = service.registerNotification(productId, email);
        if (!created) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("You are already registered for this product notification.");
        }
        return ResponseEntity.ok("We will notify you when this product is back in stock!");
    }
}