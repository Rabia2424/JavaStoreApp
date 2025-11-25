package com.store.storeapp.Admin.Controllers;

import com.store.storeapp.Models.Order;
import com.store.storeapp.Models.OrderStatus;
import com.store.storeapp.Services.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public String showOrders(Model model){
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "admin/order/order-list";
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Long id,
                                Model model,
                                @RequestParam("status") String status) {
        Optional<Order> optionalOrder = orderService.getOrderById(id);
        try{
            if(optionalOrder.isPresent()){
                Order order = optionalOrder.get();
                OrderStatus orderStatus = OrderStatus.valueOf(status);
                order.setStatus(orderStatus);
                orderService.update(order);
            }
        }catch(IllegalArgumentException e){
            model.addAttribute("order", optionalOrder.orElse(null));
            model.addAttribute("error", "Invalid status value!");
            return "admin/order/order-list";
        }
        return "redirect:/admin/order/list";
    }

    @PostMapping("/remove/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/admin/order/list";
    }
}
