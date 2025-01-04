package com.store.storeapp.Controllers;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Category;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Services.impl.CategoryService;
import com.store.storeapp.Services.impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/list")
    public String listProducts(Model model){
//        List<ProductDto> products = productService.findAllProducts();
//        model.addAttribute("products", products);
//        return "product/product-list";
        return getAllProducts(0,"id",false, model);
    }

    @GetMapping ("/search")
    public String searchProducts(@RequestParam("query") String query, Model model){
        List<ProductDto> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        return "product/product-list";
    }

    @GetMapping("/new")
    public String createProductForm(Model model){
        Product product = new Product();
        List<Category> categories = categoryService.findAllCategory();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product/product-create";
    }

    @PostMapping("/new")
    public String saveProduct(@ModelAttribute("product") Product product,@RequestParam("file") MultipartFile file) throws IOException {
        String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images";

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        System.out.println(fileNameAndPath);
        Files.write(fileNameAndPath, file.getBytes());

        product.setImageUrl("/images/"+ file.getOriginalFilename());
        productService.saveProduct(product);
        return "redirect:/products/list";
    }

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable("productId") Long id,Model model){
        List<Category> categories = categoryService.findAllCategory();
        ProductDto productDto = productService.getProductById(id);
        model.addAttribute("productDto", productDto);
        model.addAttribute("categories", categories);
        return "product/product-detail";
    }

    @GetMapping("/page/{page_no}")
    public String getAllProducts(
            @PathVariable("page_no") int page_no,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending,
            Model model
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        int pageSize = 4;
        Pageable pageable = PageRequest.of(page_no, pageSize, sort);
        Page<Product> page2 = productService.findAll(pageable);
        List<ProductDto> products = page2.getContent().stream().map(product -> productService.mapToProductDto(product)).collect(Collectors.toList());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page_no);
        model.addAttribute("totalPages", page2.getTotalPages());
        model.addAttribute("totalItems", page2.getTotalElements());
        return "product/product-list";
    }

}
