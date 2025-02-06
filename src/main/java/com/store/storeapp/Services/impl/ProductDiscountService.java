package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.ProductDiscount;
import com.store.storeapp.Repositories.ProductDiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDiscountService {
    @Autowired
    ProductDiscountRepository discountRepository;
    public ProductDiscount getValidDiscountByProductId(Long productId){
        return discountRepository.getValidDiscountByProductId(productId);
    }
}
