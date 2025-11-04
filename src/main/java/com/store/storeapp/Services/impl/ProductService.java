package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.ProductDto;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Models.ProductDiscount;
import com.store.storeapp.Models.ProductSpecs;
import com.store.storeapp.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private ProductRepository productRepository;
    @Autowired
    private ProductDiscountService discountService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FavoriteService favoriteService;
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

    public Page<Product> getPopularProducts(Pageable pageable){
        return productRepository.getPopularProducts(pageable);
    }

    @Transactional
    public Product saveProduct(Product product){
        return productRepository.save(product);
    }


    @Transactional
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

    public List<ProductDto> mapToProductDtoWithDiscounts(List<Product> products){
        return products.stream()
                .map((product) -> {
                    ProductDiscount productDiscount = discountService.getValidDiscountByProductId(product.getId());
                    ProductDto productDto =  mapToProductDto(product);
                    double rate = (productDiscount != null) ? productDiscount.getDiscountRate() : 0.0;
                    productDto.setDiscountRate(rate);

                    double discountedPrice = product.getPrice() * (1 - rate / 100);;
                    productDto.setDiscountedPrice(discountedPrice);

                    return productDto;
                }).collect(Collectors.toList());
    }

    public List<ProductDto> mapToProductDtoWithDiscountsAndFavorited(
            List<Product> products, @Nullable Long userId) {

        List<ProductDto> dtos = mapToProductDtoWithDiscounts(products);

        if (userId == null || products.isEmpty()) return dtos;
        List<Long> productIds = products.stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());

        Set<Long> likedProductIds = new HashSet<>(
                favoriteService.favoritesOfUserAsSet(userId)
        );

        for (ProductDto dto : dtos) {
            dto.setFavorited(likedProductIds.contains(dto.getId()));
        }
        return dtos;
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

    public Page<Product> searchFilterPage(
            String q,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(dir, sortBy);
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = Specification
                .where(ProductSpecs.queryLike(q))
                .and(ProductSpecs.inCategory(categoryId))
                .and(ProductSpecs.priceBetween(minPrice, maxPrice));

        return productRepository.findAll(spec, pageable);
    }


    public List<ProductDto> getTopDiscounted(int limit) {
        Page<Product> page = productRepository.findDiscountedProducts(PageRequest.of(0, limit));
        return mapToProductDtoWithDiscounts(page.getContent());
    }



//    public Page<Product> findAll(Pageable pageable) {
//        return productRepository.findAll(pageable);
//    }
//
//    public Page<Product> findAllByCategoryId(Pageable pageable, Long categoryId) { return productRepository.getProductsByCategoryId(categoryId,pageable);}
//
//    public Page<Product> findFilteredProducts(Pageable pageable,
//                                              @Param("categoryId") Long categoryId,
//                                              @Param("minPrice") Double minPrice,
//                                              @Param("maxPrice") Double maxPrice){
//        return productRepository.findFilteredProducts(categoryId, minPrice, maxPrice, pageable);
//    }


}
