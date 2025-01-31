package com.store.storeapp.Admin.Controllers;

import com.store.storeapp.Models.Order;
import com.store.storeapp.Models.OrderStatus;
import com.store.storeapp.Services.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public String showOrders(Model model){
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "order/order-list";
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Long id,
                                Model model,
                                @RequestParam("status") String status) {
        Order order = orderService.getOrderById(id);
        try{
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            order.setStatus(orderStatus);
            orderService.update(order);
        }catch(IllegalArgumentException e){
            model.addAttribute("order", order);
            model.addAttribute("error", "Invalid status value!");
            return "order/order-list";
        }
        return "redirect:/admin/order/list";
    }

    @PostMapping("/remove/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/admin/order/list";
    }
}
