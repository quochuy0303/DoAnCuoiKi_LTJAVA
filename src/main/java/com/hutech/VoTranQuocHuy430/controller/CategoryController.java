package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Category;
import com.hutech.VoTranQuocHuy430.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách category
    @GetMapping
    public String showCategoryList(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/categories-list";
    }

    // Hiển thị form thêm category
    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/add-category";
    }

    // Xử lý thêm category
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/category/add-category";
        }
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    // Hiển thị form sửa category
    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "admin/category/update-category";
    }

    // Xử lý cập nhật category
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute("category") Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "admin/category/update-category";
        }
        categoryService.updateCategory(category);
        return "redirect:/admin/categories";
    }

    // Xử lý xóa category
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return "redirect:/admin/categories";
    }
}
