package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Product;
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

    @Transactional
    public void createProductWithInventory(Product p, MultipartFile f) throws IOException {
        String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images";

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, f.getOriginalFilename());
        System.out.println(fileNameAndPath);
        Files.write(fileNameAndPath, f.getBytes());

        p.setImageUrl("/images/"+ f.getOriginalFilename());

        Product saved = productService.saveProduct(p);
        inventoryService.saveOrUpdateInventory(saved);
    }

    @Transactional
    public void updateProductWithInventory(Long id, Product product, MultipartFile f) throws IOException {
        Product existingProduct = productService.mapToProduct(productService.getProductById(id));
        if(f != null && !f.isEmpty()){
            String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images";

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, f.getOriginalFilename());
            System.out.println(fileNameAndPath);
            Files.write(fileNameAndPath, f.getBytes());

            existingProduct.setImageUrl("/images/"+ f.getOriginalFilename());
        }else{
            existingProduct.setImageUrl(existingProduct.getImageUrl());
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());

        existingProduct.setStockQuantity(product.getStockQuantity());
        productService.saveProduct(existingProduct);
        inventoryService.saveOrUpdateInventory(existingProduct);

    }
}
