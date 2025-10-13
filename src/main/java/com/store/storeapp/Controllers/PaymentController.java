package com.store.storeapp.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.model.Payment;
import com.iyzipay.request.CreatePaymentRequest;
import com.store.storeapp.Models.*;
import com.store.storeapp.Services.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/index")
    public String paymentIndex(@RequestParam Long orderId, Model model){
        Optional<Order> optionalOrder = orderService.getOrderById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            model.addAttribute("orderId", orderId);
            model.addAttribute("orderTotal", order.getTotalAmount());
        }
        return "payment/index";
    }


    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Long orderId, @Valid @ModelAttribute CardInfo paymentCardInfo,
                                              BindingResult result,
                                              Model model) throws Exception {

        if(result.hasErrors()){
            model.addAttribute("orderId", orderId);
            return "payment/index";
        }

        boolean successOrNot = paymentService.processPayment(orderId, paymentCardInfo);
        if(successOrNot == true){
            return "payment/confirmation";
        }else{
            return "payment/failure";
        }

    }

}
