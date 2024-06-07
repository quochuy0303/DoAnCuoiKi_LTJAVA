package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Product;
import com.hutech.VoTranQuocHuy430.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

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

    @Autowired
    private CartService cartService;



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

    @GetMapping("/details/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        var product = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "products/details-product";
    }

    @PostMapping("/buy-now")
    public String buyNow(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart/checkout";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
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
    public String addProduct(@Validated @ModelAttribute("product") Product product, @RequestParam("image") MultipartFile multipartFile,
                             @NotNull BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
            model.addAttribute("brands", brandService.getAllBrands());
            return "/products/add-product";
        }

        try {

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Sử dụng try-with-resources để đảm bảo rằng InputStream được đóng đúng cách
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            product.setImage("/images/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra.");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
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
