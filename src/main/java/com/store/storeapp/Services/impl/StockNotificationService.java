package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.StockNotification;
import com.store.storeapp.Repositories.StockNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockNotificationService {

    @Autowired
    private StockNotificationRepository repository;

    @Autowired
    private ProductService productService;

    @Autowired
    private JavaMailSender mailSender;

    public boolean registerNotification(Long productId, String email) {
        Optional<StockNotification> existing = repository.findByProductIdAndEmail(productId, email);
        if(existing.isPresent()){
            return false;
        }
        StockNotification n = new StockNotification();
        n.setProductId(productId);
        n.setUserEmail(email);
        repository.save(n);

        return true;
    }

    public void notifyUsersIfBackInStock(Long productId) {
        List<StockNotification> list = repository.findByProductIdAndNotifiedFalse(productId);

        for (StockNotification n : list) {
            sendMail(n.getUserEmail(), productId);
            n.setNotified(true);
        }
        repository.saveAll(list);
    }

    private void sendMail(String to, Long productId) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Product Back in Stock!");
        msg.setText("Good news! Product #" + productId + " is now back in stock. Hurry up before it sells out again!");
        mailSender.send(msg);
    }
}
