package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Category;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.CategoryRepository;
import com.store.storeapp.Repositories.ProductRepository;
import com.store.storeapp.Repositories.StockNotificationRepository;
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
    private StockNotificationService notificationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public void createProductWithInventory(Product p, MultipartFile f) {
        if (f != null && !f.isEmpty()) {
            String imageUrl = fileStorageService.store(f, "products");
            p.setImageUrl(imageUrl);
        }

        Product saved = productService.saveProduct(p);

        inventoryService.saveOrUpdateInventory(saved.getId(), saved.getStockQuantity());
    }

    @Transactional
    public void updateProductWithInventory(Long id, Product patch, MultipartFile f) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        if (f != null && !f.isEmpty()) {
            String imageUrl = fileStorageService.store(f, "products");
            existing.setImageUrl(imageUrl);
        }

        existing.setName(patch.getName());
        existing.setDescription(patch.getDescription());
        existing.setPrice(patch.getPrice());

        int oldStock = existing.getStockQuantity();
        existing.setStockQuantity(patch.getStockQuantity());

        if(oldStock == 0 && patch.getStockQuantity() > 0){
            notificationService.notifyUsersIfBackInStock(existing.getId());
        }

        if (patch.getCategory() != null && patch.getCategory().getId() != null) {
            Category ref = categoryRepository.getReferenceById(patch.getCategory().getId());
            existing.setCategory(ref);
        } else {
            existing.setCategory(null);
        }
        inventoryService.saveOrUpdateInventory(existing.getId(), existing.getStockQuantity());
    }

}
