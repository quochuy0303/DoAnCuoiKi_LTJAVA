package com.hutech.VoTranQuocHuy430.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/product-management")
    public String showProductManagement() {
        return "product-management";
    }
}
