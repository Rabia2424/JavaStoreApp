package com.store.storeapp.Services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.store.storeapp.Models.*;
import com.store.storeapp.Models.Payment;
import com.store.storeapp.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Value("${iyzico.api.key}")
    private String apiKey;

    @Value("${iyzico.secret.key}")
    private String secretKey;

    @Value("${iyzico.base.url}")
    private String baseUrl;

    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }

    @Transactional
    public boolean processPayment(Long orderId, CardInfo paymentCardInfo) throws Exception {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);


        Cart cart = cartService.getCartByUserId(order.getUserId());
        BigDecimal basketTotal = cart.getCartItems().stream()
                .map(ci -> BigDecimal.valueOf(ci.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println(">>> Order amount: " + order.getTotalAmount());

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(orderId.toString());
        request.setPrice(basketTotal);
        request.setPaidPrice(basketTotal);
        request.setCurrency(Currency.TRY.name());
        request.setInstallment(1);
        request.setBasketId(String.valueOf(cart.getCartId()));
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
        AddressSnapshot ship = order.getShipping();
        AddressSnapshot bill = order.getBilling();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Buyer buyer = new Buyer();
        buyer.setId(user.getId().toString());
        String[] nameParts = (ship.getFullName() != null ? ship.getFullName() : " ").split("\\s+", 2);
        buyer.setName(nameParts[0]);
        buyer.setSurname(nameParts.length > 1 ? nameParts[1] : " ");
        buyer.setIdentityNumber("11111111110");
        buyer.setEmail(user.getEmail());
        buyer.setRegistrationDate(user.getCreatedOn().format(fmt));
        buyer.setRegistrationAddress(join(ship));
        buyer.setIp("85.34.78.112");
        buyer.setCity(ship.getCity());
        buyer.setCountry(ship.getCountry());
        buyer.setZipCode(ship.getPostalCode());
        request.setBuyer(buyer);

        Address shippingAddress = new Address();
        shippingAddress.setContactName(ship.getFullName());
        shippingAddress.setCity(ship.getCity());
        shippingAddress.setCountry(ship.getCountry());
        shippingAddress.setZipCode(ship.getPostalCode());
        shippingAddress.setAddress(join(ship));
        request.setShippingAddress(shippingAddress);

        Address billingAddress = new Address();
        billingAddress.setContactName(bill.getFullName());
        billingAddress.setCity(bill.getCity());
        billingAddress.setCountry(bill.getCountry());
        billingAddress.setZipCode(bill.getPostalCode());
        billingAddress.setAddress(join(bill));
        request.setBillingAddress(billingAddress);


        List<BasketItem> basketItems = new ArrayList<BasketItem>();
        for (CartItem cartItem: cart.getCartItems()){
            BasketItem basketItem = new BasketItem();
            basketItem.setId(cartItem.getId().toString());
            basketItem.setName(cartItem.getProduct().getName());
            basketItem.setCategory1(cartItem.getProduct().getCategory().getId().toString());
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(new BigDecimal(cartItem.getTotalPrice()));
            basketItems.add(basketItem);
        }
        request.setBasketItems(basketItems);


        com.iyzipay.model.Payment iyzicoPaymentResponse = com.iyzipay.model.Payment.create(request, options);

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

            orderService.markPending(order.getOrderId(), OrderStatus.PENDING);
            paymentService.savePayment(payment);

            inventoryService.consumeReservations(order.getOrderId());

            cartService.deleteAllCartItems(cart);
            cart.setTotalPayment(cart.getTotalPayment());
            cart.setStatus(CartStatus.COMPLETED);
            cartService.updateCart(cart);
            return true;
        } else {
            payment.setStatus("FAILURE");
            payment.setErrorMessage(iyzicoPaymentResponse.getErrorMessage());

            orderService.markCancelled(order.getOrderId(), OrderStatus.CANCELLED);
            paymentService.savePayment(payment);
            inventoryService.releaseReservations(order.getOrderId());

            return false;
        }
    }

    private String join(AddressSnapshot a) {
        return String.join(", ",
                notBlank(a.getLine1()),
                optional(a.getLine2()),
                notBlank(a.getDistrict()),
                notBlank(a.getCity()),
                notBlank(a.getCountry()),
                "(" + notBlank(a.getPostalCode()) + ")"
        ).replaceAll("(, )+", ", ").replaceAll("^, |, $", "");
    }

    private String notBlank(String s){ return (s==null||s.isBlank()) ? "" : s; }
    private String optional(String s){ return notBlank(s); }

}
