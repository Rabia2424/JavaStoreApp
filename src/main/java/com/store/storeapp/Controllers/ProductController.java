package com.store.storeapp.Controllers;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Category;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Models.ProductDiscount;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.CategoryService;
import com.store.storeapp.Services.impl.ProductDiscountService;
import com.store.storeapp.Services.impl.ProductService;
import com.store.storeapp.Services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private CategoryService categoryService;
    private ProductService productService;

    @Autowired
    private ProductDiscountService discountService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/list")
    public String getAllProducts(
            @RequestParam(required = false) String q,
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page_no,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            Model model,
            @CookieValue(name = "jwt", required = false) String token
    ) {
        Page<Product> pageResult = productService.searchFilterPage(
            q, categoryId, minPrice, maxPrice, page_no, size, sortBy, direction
        );

        String email = null;
        Long userId = null;
        if (token != null && !token.isBlank()) {
            try {
                userId = authService.getUserIdFromToken(token);
                email = userService.findByUserId(userId)
                        .map(user -> user.getEmail())
                        .orElse(null);
            } catch (Exception ex) { }
        }

        List<ProductDto> products = productService.mapToProductDtoWithDiscountsAndFavorited(pageResult.getContent(), userId);
        List<Category> categories = categoryService.findAllCategory();

        model.addAttribute("email", email);

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());

        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("size", size);

        return "product/product-list";
    }

    @GetMapping("/api")
    @ResponseBody
    public Page<ProductDto> getProductsApi(
            @RequestParam(required = false) String q,
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page_no,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @CookieValue(name = "jwt", required = false) String token
    ){
        Page<Product> pageResult = productService.searchFilterPage(
                q, categoryId, minPrice, maxPrice, page_no, size, sortBy, direction
        );

        Long userId = null;
        if (token != null && !token.isBlank()) {
            try {
                userId = authService.getUserIdFromToken(token);
            } catch (Exception ex) { }
        }

        List<ProductDto> products = productService.mapToProductDtoWithDiscountsAndFavorited(pageResult.getContent(), userId);
        return new PageImpl<> (
            products,
            pageResult.getPageable(),
            pageResult.getTotalElements()
        );
    }

    @GetMapping("/detail/{productId}")
    public String getProductById(@PathVariable("productId") Long id,Model model){
        List<Category> categories = categoryService.findAllCategory();
        ProductDto productDto = productService.getProductById(id);
        ProductDiscount discount = discountService.getValidDiscountByProductId(id);
        model.addAttribute("productDto", productDto);
        model.addAttribute("categories", categories);
        model.addAttribute("discount", discount);
        return "product/product-detail";
    }



}