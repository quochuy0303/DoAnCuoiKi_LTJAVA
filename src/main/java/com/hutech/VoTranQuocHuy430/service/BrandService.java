package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.model.Brand;
import com.hutech.VoTranQuocHuy430.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {
    private final BrandRepository brandRepository;

    // Lấy danh sách tất cả thương hiệu
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // Lấy thương hiệu bằng ID
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    // Thêm mới thương hiệu
    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    // Cập nhật thông tin thương hiệu
    public Brand updateBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    // Xóa thương hiệu bằng ID
    public void deleteBrandById(Long id) {
        brandRepository.deleteById(id);
    }
}
