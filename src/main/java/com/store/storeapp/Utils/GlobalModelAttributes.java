package com.store.storeapp.Utils;

import com.store.storeapp.Models.Product;
import com.store.storeapp.Services.impl.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ProductService productService;

    public GlobalModelAttributes(ProductService productService) {
        this.productService = productService;
    }

    @ModelAttribute("popularProducts")
    public Page<Product> addPopularProductsToModel() {
        Pageable pageable = PageRequest.of(0,5);
        return productService.getPopularProducts(pageable);
    }
}
