package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Category;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.CategoryRepository;
import com.store.storeapp.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CatalogAppService {
    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public void createProductWithInventory(Product p, MultipartFile f) throws IOException {
        if (f != null && !f.isEmpty()) {
            String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images";
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Path path = Paths.get(UPLOAD_DIR, f.getOriginalFilename());
            Files.write(path, f.getBytes());
            p.setImageUrl("/images/" + f.getOriginalFilename());
        }

        Product saved = productService.saveProduct(p);

        inventoryService.saveOrUpdateInventory(saved.getId(), saved.getStockQuantity());
    }

    @Transactional
    public void updateProductWithInventory(Long id, Product patch, MultipartFile f) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        if (f != null && !f.isEmpty()) {
            String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images";
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Path path = Paths.get(UPLOAD_DIR, f.getOriginalFilename());
            Files.write(path, f.getBytes());
            existing.setImageUrl("/images/" + f.getOriginalFilename());
        }

        existing.setName(patch.getName());
        existing.setDescription(patch.getDescription());
        existing.setPrice(patch.getPrice());
        existing.setStockQuantity(patch.getStockQuantity());

        if (patch.getCategory() != null && patch.getCategory().getId() != null) {
            Category ref = categoryRepository.getReferenceById(patch.getCategory().getId());
            existing.setCategory(ref);
        } else {
            existing.setCategory(null);
        }
        inventoryService.saveOrUpdateInventory(existing.getId(), existing.getStockQuantity());
    }

}
