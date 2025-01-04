package com.store.storeapp.Controllers;

import com.store.storeapp.Models.Category;
import com.store.storeapp.Services.impl.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/new")
    public String createCategoryForm(Model model){
        Category category = new Category();
        model.addAttribute("category", category);
        return "category/category-create";
    }

    @PostMapping("/new")
    public String createCategoryForm(Category category){
        categoryService.saveCategory(category);
        return "redirect:/products/6";
    }
}
