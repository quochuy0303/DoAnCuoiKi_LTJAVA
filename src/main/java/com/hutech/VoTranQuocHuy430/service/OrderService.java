package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.DTO.PaymentResDTO;
import com.hutech.VoTranQuocHuy430.config.Config;
import com.hutech.VoTranQuocHuy430.model.CartItem;
import com.hutech.VoTranQuocHuy430.model.Order;
import com.hutech.VoTranQuocHuy430.model.OrderDetail;
import com.hutech.VoTranQuocHuy430.repository.OrderDetailRepository;
import com.hutech.VoTranQuocHuy430.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    private  OrderRepository orderRepository;

    @Autowired
    private  OrderDetailRepository orderDetailRepository;

    @Autowired
    private  CartService cartService;

    public double calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }

    public Order createOrder(String customerName, String customerAddress, String phoneNumber, String email, String notes, String paymentMethod, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerAddress(customerAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setOrderDate(new Date()); // Đặt ngày đặt hàng là ngày hiện tại
        order.setTotalAmount(calculateTotalAmount(cartItems)); // Set total amount
        order = orderRepository.save(order); // Lưu đơn hàng vào cơ sở dữ liệu

        // Lưu các mục đơn hàng vào cơ sở dữ liệu
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getProduct().getPrice()); // Set unit price
            orderDetailRepository.save(detail);
        }

        // Tùy chọn: xóa giỏ hàng sau khi đặt hàng
        cartService.clearCart();

        return order;
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }


    public ResponseEntity<PaymentResDTO> initiateVnpayPayment(HttpServletRequest req, Order order) throws UnsupportedEncodingException {
        double totalAmount = order.getTotalAmount() * 100; // VNPay yêu cầu số tiền phải nhân với 100

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = Config.getIpAddress(req);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.getVnp_Version());
        vnp_Params.put("vnp_Command", Config.getVnp_Command());
        vnp_Params.put("vnp_TmnCode", Config.getVnp_TmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf((long) totalAmount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.getVnp_ReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Add vnp_CreateDate and vnp_ExpireDate
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        PaymentResDTO paymentResDTO = new PaymentResDTO();
        paymentResDTO.setStatus("ok");
        paymentResDTO.setMessage("successfully");
        paymentResDTO.setURL(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(paymentResDTO);
    }
}
