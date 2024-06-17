package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Product;
import com.hutech.VoTranQuocHuy430.service.ProductService;
import com.hutech.VoTranQuocHuy430.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/product/product-list";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("manufacturers", productService.getAllManufacturers());
        model.addAttribute("brands", productService.getAllBrands());
        return "admin/product/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute Product product, BindingResult result, @RequestParam("file") MultipartFile file, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("manufacturers", productService.getAllManufacturers());
            model.addAttribute("brands", productService.getAllBrands());
            return "admin/product/add-product";
        }
        if (file != null && !file.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(file);
            product.setImage(imageUrl);
        }
        productService.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("manufacturers", productService.getAllManufacturers());
        model.addAttribute("brands", productService.getAllBrands());
        return "admin/product/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute Product product, BindingResult result, @RequestParam("file") MultipartFile file, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("manufacturers", productService.getAllManufacturers());
            model.addAttribute("brands", productService.getAllBrands());
            return "admin/product/update-product";
        }
        if (file != null && !file.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(file);
            product.setImage(imageUrl);
        }
        productService.update(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }
}
