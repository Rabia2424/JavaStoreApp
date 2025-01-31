package com.store.storeapp.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.model.Payment;
import com.iyzipay.request.CreatePaymentRequest;
import com.store.storeapp.Models.*;
import com.store.storeapp.Services.impl.CartService;
import com.store.storeapp.Services.impl.OrderService;
import com.store.storeapp.Services.impl.PaymentService;
import com.store.storeapp.Services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private CartService cartService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam("orderJson") String orderJson, @ModelAttribute CardInfo paymentCardInfo,
                                              BindingResult result,
                                              Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //System.out.println("Generated JSON: " + orderJson);
        Order order = objectMapper.readValue(orderJson, Order.class);

        Options options = new Options();
        options.setApiKey("sandbox-fBfRZFHKRQVy36bnntrP9DLbtBRQRoP4");
        options.setSecretKey("sandbox-n4lxh7iwqjDLq45AUjC28Stutb2BMw7e");
        options.setBaseUrl("https://sandbox-api.iyzipay.com");

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId("123456789");
        request.setPrice(new BigDecimal(order.getTotalAmount()));
        request.setPaidPrice(new BigDecimal(order.getTotalAmount()));
        request.setCurrency(Currency.TRY.name());
        request.setInstallment(1);
        request.setBasketId(String.valueOf(cartService.getCartByUserId(order.getUserId()).getCartId()));
        request.setPaymentChannel(PaymentChannel.WEB.name());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(paymentCardInfo.getCardHolderName());
        paymentCard.setCardNumber(paymentCardInfo.getCardNumber());
        paymentCard.setExpireMonth(paymentCardInfo.getExpireMonth());
        paymentCard.setExpireYear(paymentCardInfo.getExpireYear());
        paymentCard.setCvc(paymentCardInfo.getCvc());
        paymentCard.setRegisterCard(0);
        request.setPaymentCard(paymentCard);

        User user = userService.getUserById(order.getUserId()).orElse(null);

        Buyer buyer = new Buyer();
        buyer.setId(user.getId().toString());
        buyer.setName(user.getName());
        buyer.setSurname(user.getName());
        buyer.setIdentityNumber("111111111111");
        buyer.setEmail(user.getEmail());
        buyer.setRegistrationDate("2013-04-21 15:12:09");
        buyer.setRegistrationAddress(order.getShippingAddress());
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34732");
        request.setBuyer(buyer);


        Address shippingAddress = new Address();
        shippingAddress.setContactName("John Anderson");
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setZipCode("34742");
        shippingAddress.setAddress(order.getShippingAddress());
        request.setShippingAddress(shippingAddress);

        Address billingAddress = new Address();
        billingAddress.setContactName("John Anderson");
        billingAddress.setCity("Istanbul");
        billingAddress.setCountry("Turkey");
        billingAddress.setZipCode("34742");
        billingAddress.setAddress(order.getBillingAddress());
        request.setBillingAddress(billingAddress);

        List<BasketItem> basketItems = new ArrayList<BasketItem>();

        for (CartItem cartItem: cartService.getCartByUserId(order.getUserId()).getCartItems()){
            BasketItem basketItem = new BasketItem();
            basketItem.setId(cartItem.getId().toString());
            basketItem.setName(cartItem.getProduct().getName());
            basketItem.setCategory1(cartItem.getProduct().getCategory().getId().toString());
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(new BigDecimal(cartItem.getTotalPrice()));
            basketItems.add(basketItem);
        }

        request.setBasketItems(basketItems);

        Payment iyzicoPaymentResponse = Payment.create(request, options);

        com.store.storeapp.Models.Payment payment = new com.store.storeapp.Models.Payment();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setTotalPaid(order.getTotalAmount());
        payment.setCurrency(iyzicoPaymentResponse.getCurrency());
        payment.setConversationId(iyzicoPaymentResponse.getConversationId());

        if ("success".equals(iyzicoPaymentResponse.getStatus())) {
            payment.setStatus("SUCCESS");
            payment.setIyzicoPaymentId(iyzicoPaymentResponse.getPaymentId());
            orderService.save(order);
            paymentService.savePayment(payment);
            return "payment/confirmation";
        } else {
            payment.setStatus("FAILURE");
            payment.setErrorMessage(iyzicoPaymentResponse.getErrorMessage());
            return "payment/failure";
        }
    }


}
