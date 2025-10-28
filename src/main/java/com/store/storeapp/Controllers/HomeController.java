package com.store.storeapp.Controllers;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Services.impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String showIndexPage(Model model){
        List<ProductDto> discounted = productService.getTopDiscounted(4);
        model.addAttribute("discountedProducts", discounted);
        return "index";
    }
}
