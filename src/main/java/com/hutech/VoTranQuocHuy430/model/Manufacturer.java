package com.hutech.VoTranQuocHuy430.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String address;
    private String phoneNumber;
    private String description;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    @JsonBackReference // Chú thích này giúp tránh vòng lặp JSON
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
