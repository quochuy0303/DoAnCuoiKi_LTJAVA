package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.model.CartItem;
import com.hutech.VoTranQuocHuy430.model.Product;
import com.hutech.VoTranQuocHuy430.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;

@Service
@SessionScope
public class CartService {

    private List<CartItem> cartItems = new ArrayList<>();
    Map<Long, CartItem> cartItemMap = new HashMap<>();
    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        CartItem existingCartItem = cartItems.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId() == productId);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalPrice() {
        return cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }
}
