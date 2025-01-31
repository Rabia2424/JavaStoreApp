package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Payment;
import com.store.storeapp.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }
}
