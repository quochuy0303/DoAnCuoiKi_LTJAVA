package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.*;
import com.hutech.VoTranQuocHuy430.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders();
        List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
        List<Brand> brands = brandService.getAllBrands();
        List<User> users = userService.getAllUsers();

        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("brands", brands);
        model.addAttribute("users", users);

        return "admin/index";
    }

    @GetMapping("/other-page")
    public String otherPage() {
        return "admin/404";  // Đây là tệp other_pages.html trong thư mục templates
    }
}
