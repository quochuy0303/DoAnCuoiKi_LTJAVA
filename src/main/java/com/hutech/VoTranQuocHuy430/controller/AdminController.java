package com.hutech.VoTranQuocHuy430.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/index";  // Đây là tệp index.html trong thư mục templates
    }

    @GetMapping("/other-page")
    public String otherPage() {
        return "admin/404";  // Đây là tệp other_pages.html trong thư mục templates
    }
}
