package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<ProductDto> findAllProducts(){
        List<Product> products = productRepository.findAll();
        return products.stream().map((product) -> mapToProductDto(product)).collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id){
        Product product = productRepository.getProductByProductId(id);
        return mapToProductDto(product);
    }

    public List<ProductDto> searchProducts(String query){
        List<Product> products = productRepository.searchProducts(query);
        return products.stream().map((product) -> mapToProductDto(product)).collect(Collectors.toList());
    }

    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    public ProductDto mapToProductDto(Product product){
        ProductDto productDto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .createdOn(product.getCreatedOn())
                .updatedOn(product.getUpdatedOn())
                .categoryName(product.getCategory().getName())
                .build();
        return productDto;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
