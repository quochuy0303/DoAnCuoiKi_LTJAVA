package com.hutech.VoTranQuocHuy430.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String productName;
    private int quantity;
    private double unitPrice;

    // Ensure productName is set when creating an order detail
    @PrePersist
    @PreUpdate
    private void setProductName() {
        if (product != null) {
            this.productName = product.getName();
        }
    }
}
