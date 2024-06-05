package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Brand;
import com.hutech.VoTranQuocHuy430.model.Category;
import com.hutech.VoTranQuocHuy430.model.Manufacturer;
import com.hutech.VoTranQuocHuy430.model.Product;
import com.hutech.VoTranQuocHuy430.service.BrandService;
import com.hutech.VoTranQuocHuy430.service.CategoryService;
import com.hutech.VoTranQuocHuy430.service.ManufacturerService;
import com.hutech.VoTranQuocHuy430.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private BrandService brandService;

    @GetMapping
    public String showProductList(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 4;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage = productService.getAllProductsPageable(pageable);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "/products/product-list";
    }

    @GetMapping("/sortAscending")
    public String sortProductsAscending(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 4;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").ascending());
        Page<Product> productPage = productService.getAllProductsPageable(pageable);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sort", "asc");
        return "/products/product-list";
    }

    @GetMapping("/sortDescending")
    public String sortProductsDescending(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 4;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").descending());
        Page<Product> productPage = productService.getAllProductsPageable(pageable);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sort", "desc");
        return "/products/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("brands", brandService.getAllBrands());
        return "/products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, @RequestParam("image") MultipartFile file, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
            model.addAttribute("brands", brandService.getAllBrands());
            return "/products/add-product";
        }
        // Xử lý tải ảnh
        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            Path path = Paths.get("upload-dir/" + fileName);
            Files.write(path, bytes);
            product.setImage(path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        productService.addProduct(product);
        return "redirect:/products";
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        if (product.getCategory() == null) {
            product.setCategory(new Category());
        }
        if (product.getManufacturer() == null) {
            product.setManufacturer(new Manufacturer());
        }
        if (product.getBrand() == null) {
            product.setBrand(new Brand());
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("brands", brandService.getAllBrands());
        return "/products/update-product";
    }


    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
            model.addAttribute("brands", brandService.getAllBrands());
            return "/products/update-product";
        }
        productService.updateProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
