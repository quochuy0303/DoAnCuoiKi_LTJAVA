package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.model.Manufacturer;
import com.hutech.VoTranQuocHuy430.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;

    // Lấy danh sách tất cả nhà sản xuất
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    // Lấy nhà sản xuất bằng ID
    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    // Thêm mới nhà sản xuất
    public Manufacturer addManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    // Cập nhật thông tin nhà sản xuất
    public Manufacturer updateManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    // Xóa nhà sản xuất bằng ID
    public void deleteManufacturerById(Long id) {
        manufacturerRepository.deleteById(id);
    }
}
