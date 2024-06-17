package com.hutech.VoTranQuocHuy430.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring6.processor.SpringOptionFieldTagProcessor;

@Controller
@RequestMapping("/")
public class ContactController {
    @GetMapping("contact")
    public String showCotact() {
        return "/contact/contact";
    }

}
