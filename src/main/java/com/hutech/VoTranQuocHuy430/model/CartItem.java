package com.hutech.VoTranQuocHuy430.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CartItem {
    private Product product;
    private int quantity;
}
