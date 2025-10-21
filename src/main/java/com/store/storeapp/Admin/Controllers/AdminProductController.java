package com.store.storeapp.Admin.Controllers;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Category;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Services.impl.CatalogAppService;
import com.store.storeapp.Services.impl.CategoryService;
import com.store.storeapp.Services.impl.InventoryService;
import com.store.storeapp.Services.impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    @Autowired
    private CategoryService categoryService;
    private ProductService productService;

    @Autowired
    private CatalogAppService catalogAppService;

    public AdminProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/list")
    public String listProducts(Model model){
        List<ProductDto> products = productService.findAllProducts();
        model.addAttribute("products", products);
        return "product/admin-product-list";
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
    public String saveProduct(@ModelAttribute("product") Product product, @RequestParam("file") MultipartFile file) throws IOException {
        catalogAppService.createProductWithInventory(product, file);
        return "redirect:/products/list";
    }

    @GetMapping("/edit/{id}")
    public String createUpdateForm(@PathVariable Long id, Model model){
        Product product = productService.mapToProduct(productService.getProductById(id));
        List<Category> categories = categoryService.findAllCategory();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product/product-update";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product newProduct,
                                BindingResult result,
                                Model model,
                                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if(result.hasErrors()){
            model.addAttribute("product", newProduct);
            return "product/product-update";
        }
        catalogAppService.updateProductWithInventory(id, newProduct, file);
        return "redirect:/admin/products/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return "redirect:/admin/products/list";
    }

}
