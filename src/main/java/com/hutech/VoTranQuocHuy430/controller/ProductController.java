package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Product;
import com.hutech.VoTranQuocHuy430.service.BrandService;
import com.hutech.VoTranQuocHuy430.service.CategoryService;
import com.hutech.VoTranQuocHuy430.service.ManufacturerService;
import com.hutech.VoTranQuocHuy430.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        if (!file.isEmpty()) {
            try {
                // Lưu tệp tải lên vào thư mục /resources/static/images
                String fileName = file.getOriginalFilename();
                File saveFile = new ClassPathResource("static/images").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath(), fileName);
                Files.write(path, file.getBytes());

                // Đặt đường dẫn tệp trong sản phẩm
                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // Xử lý lỗi
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        model.addAttribute("brands", brandService.getAllBrands());
        return "/products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") Product product, @RequestParam("image") MultipartFile file, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
            model.addAttribute("brands", brandService.getAllBrands());
            return "/products/update-product";
        }

        if (!file.isEmpty()) {
            try {
                // Save the uploaded file to the /resources/images directory
                String fileName = file.getOriginalFilename();
                File saveFile = new ClassPathResource("static/images").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath(), fileName);
                Files.write(path, file.getBytes());

                // Set the image path in the product
                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the error
            }
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
