package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.ProductDiscount;
import com.store.storeapp.Repositories.ProductDiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ProductDiscountService {
    @Autowired
    ProductDiscountRepository discountRepository;
    @Transactional
    public ProductDiscount getValidDiscountByProductId(Long productId){
        discountRepository.deactivateExpiredDiscounts();
        return discountRepository.getValidDiscountByProductId(productId);
    }
}
