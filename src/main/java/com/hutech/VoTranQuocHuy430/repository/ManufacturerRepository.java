package com.hutech.VoTranQuocHuy430.repository;

import com.hutech.VoTranQuocHuy430.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer,Long> {
}
