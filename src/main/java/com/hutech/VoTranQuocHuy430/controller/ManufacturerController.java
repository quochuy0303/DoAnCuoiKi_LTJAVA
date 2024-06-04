package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Brand;
import com.hutech.VoTranQuocHuy430.model.Manufacturer;
import com.hutech.VoTranQuocHuy430.service.BrandService;
import com.hutech.VoTranQuocHuy430.service.ManufacturerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manufacturers")
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private BrandService brandService;

    // Hiển thị danh sách nhà sản xuất
    @GetMapping
    public String showManufacturerList(Model model) {
        model.addAttribute("manufacturers", manufacturerService.getAllManufacturers());
        return "/manufacturers/manufacturer-list";
    }

    // Hiển thị form thêm nhà sản xuất
    @GetMapping("/add")
    public String showAddManufacturerForm(Model model) {
        model.addAttribute("manufacturer", new Manufacturer());

        // Thêm danh sách các thương hiệu vào model
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);

        return "/manufacturers/add-manufacturer";
    }

    // Xử lý thêm nhà sản xuất
    @PostMapping("/add")
    public String addManufacturer(@Valid Manufacturer manufacturer, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Nếu có lỗi, cần phải thêm lại danh sách thương hiệu vào model
            List<Brand> brands = brandService.getAllBrands();
            model.addAttribute("brands", brands);
            return "/manufacturers/add-manufacturer";
        }
        manufacturerService.addManufacturer(manufacturer);
        return "redirect:/manufacturers";
    }

    // Hiển thị form sửa nhà sản xuất
    @GetMapping("/edit/{id}")
    public String showEditManufacturerForm(@PathVariable Long id, Model model) {
        Manufacturer manufacturer = manufacturerService.getManufacturerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer Id:" + id));
        model.addAttribute("manufacturer", manufacturer);
        model.addAttribute("brands", brandService.getAllBrands());
        return "/manufacturers/update-manufacturer";
    }

    // Xử lý cập nhật nhà sản xuất
    @PostMapping("/update/{id}")
    public String updateManufacturer(@PathVariable Long id, @Valid Manufacturer manufacturer, BindingResult result, Model model) {
        if (result.hasErrors()) {
            manufacturer.setId(id);
            model.addAttribute("brands", brandService.getAllBrands());
            return "/manufacturers/update-manufacturer";
        }
        manufacturerService.updateManufacturer(manufacturer);
        return "redirect:/manufacturers";
    }

    // Xử lý xóa nhà sản xuất
    @GetMapping("/delete/{id}")
    public String deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturerById(id);
        return "redirect:/manufacturers";
    }
}
