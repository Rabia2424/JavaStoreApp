package com.store.storeapp.admin.controllers;

import com.store.storeapp.DTOs.DailySalesSummary;
import com.store.storeapp.Services.impl.OrderService;
import com.store.storeapp.Services.impl.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final OrderService orderService;
    private final ProductService productService;

    public AdminDashboardController(OrderService orderService, ProductService productService ) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("pageSubtitle", "Statistics of Daily Order");

        try {
            List<DailySalesSummary> last14Days = orderService.getLastNDaysSummary(14);

            List<String> labels = last14Days.stream()
                    .map(s -> s.getDate().toString())
                    .toList();
            List<Double> revenues = last14Days.stream()
                    .map(DailySalesSummary::getTotalRevenue)
                    .toList();
            List<Long> orderCounts = last14Days.stream()
                    .map(DailySalesSummary::getOrderCount)
                    .toList();

            model.addAttribute("dailySalesLabels", labels);
            model.addAttribute("dailySalesRevenues", revenues);
            model.addAttribute("dailySalesOrderCounts", orderCounts);

            long todayOrders = orderService.getTodayOrderCount();
            double todayRevenue = orderService.getTodayRevenue();

            double avgOrderValueToday = 0.0;
            if (todayOrders > 0) {
                avgOrderValueToday = todayRevenue / todayOrders;
            }

            model.addAttribute("todayOrders", todayOrders);
            model.addAttribute("todayRevenue", todayRevenue);
            model.addAttribute("avgOrderValueToday", avgOrderValueToday);
            model.addAttribute("cancelledCountToday", orderService.getTodayCancelledCount());
            model.addAttribute("pendingOrders", orderService.getPendingOrderCount());
            model.addAttribute("lowStockCount", productService.getLowStockCount());

            model.addAttribute("topSellingProducts", productService.getTopSellingProducts(5));
            model.addAttribute("lowStockProducts", productService.getLowStockProducts(5));

        } catch (Exception ex) {
            ex.printStackTrace();

            model.addAttribute("topSellingProducts", java.util.Collections.emptyList());
            model.addAttribute("lowStockProducts", java.util.Collections.emptyList());

            String msg = ex.getClass().getSimpleName() + ": " +
                    (ex.getMessage() != null ? ex.getMessage() : "no message");
            model.addAttribute("dashboardError", msg);
        }

        return "admin/dashboard";
    }


}
