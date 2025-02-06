package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
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

    public Page<Product> getPopularProducts(Pageable pageable){
        return productRepository.getPopularProducts(pageable);
    }

    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    public void updateProduct(Product product){
        productRepository.save(product);
    }

    public void deleteProduct(Long productId){
        productRepository.deleteById(productId);
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
                .category(product.getCategory())
                .build();
        return productDto;
    }

    public Product mapToProduct(ProductDto productDto){
        Product product = Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stockQuantity(productDto.getStockQuantity())
                .imageUrl(productDto.getImageUrl())
                .createdOn(productDto.getCreatedOn())
                .updatedOn(productDto.getUpdatedOn())
                .category(productDto.getCategory())
                .build();
        return product;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findAllByCategoryId(Pageable pageable, Long categoryId) { return productRepository.getProductsByCategoryId(categoryId,pageable);}

    public Page<Product> findFilteredProducts(Pageable pageable,
                                              @Param("categoryId") Long categoryId,
                                              @Param("minPrice") Double minPrice,
                                              @Param("maxPrice") Double maxPrice){
        return productRepository.findFilteredProducts(categoryId, minPrice, maxPrice, pageable);
    }
}
