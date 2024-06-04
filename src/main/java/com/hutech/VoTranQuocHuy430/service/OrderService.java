package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.model.CartItem;
import com.hutech.VoTranQuocHuy430.model.Order;
import com.hutech.VoTranQuocHuy430.model.OrderDetail;
import com.hutech.VoTranQuocHuy430.repository.OrderDetailRepository;
import com.hutech.VoTranQuocHuy430.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;

    public Order createOrder(String customerName, String customerAddress, String phoneNumber, String email, String notes, String paymentMethod, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerAddress(customerAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setOrderDate(new Date()); // Đặt ngày đặt hàng là ngày hiện tại
        order = orderRepository.save(order); // Lưu đơn hàng vào cơ sở dữ liệu

        // Lưu các mục đơn hàng vào cơ sở dữ liệu
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }

        // Tùy chọn: xóa giỏ hàng sau khi đặt hàng
        cartService.clearCart();

        return order;
    }
}
