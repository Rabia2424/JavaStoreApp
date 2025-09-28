package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.Category;
import com.store.storeapp.Repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategory(){
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }

    public Category saveCategory(Category category){
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long id){ return categoryRepository.findById(id).orElse(null); }

}
