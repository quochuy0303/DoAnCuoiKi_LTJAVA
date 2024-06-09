package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Brand;
import com.hutech.VoTranQuocHuy430.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Hiển thị danh sách thương hiệu
    @GetMapping
    public String showBrandList(Model model) {
        model.addAttribute("brands", brandService.getAllBrands());
        return "admin/brand/brand-list";
    }

    // Hiển thị form thêm thương hiệu
    @GetMapping("/add")
    public String showAddBrandForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "admin/brand/add-brand";
    }

    // Xử lý thêm thương hiệu
    @PostMapping("/add")
    public String addBrand(@Valid @ModelAttribute("brand") Brand brand, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/brand/add-brand";
        }
        brandService.addBrand(brand);
        return "redirect:/admin/brands";
    }

    // Hiển thị form sửa thương hiệu
    @GetMapping("/edit/{id}")
    public String showEditBrandForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.getBrandById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        model.addAttribute("brand", brand);
        return "admin/brand/update-brand";
    }

    // Xử lý cập nhật thương hiệu
    @PostMapping("/update/{id}")
    public String updateBrand(@PathVariable Long id, @Valid @ModelAttribute("brand") Brand brand, BindingResult result, Model model) {
        if (result.hasErrors()) {
            brand.setId(id);
            return "admin/brand/update-brand";
        }
        brandService.updateBrand(brand);
        return "redirect:/admin/brands";
    }

    // Xử lý xóa thương hiệu
    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id) {
        brandService.deleteBrandById(id);
        return "redirect:/admin/brands";
    }
}
