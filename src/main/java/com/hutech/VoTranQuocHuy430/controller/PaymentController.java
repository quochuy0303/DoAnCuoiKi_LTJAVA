package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.DTO.PaymentResDTO;
import com.hutech.VoTranQuocHuy430.config.Config;
import com.hutech.VoTranQuocHuy430.model.*;
import com.hutech.VoTranQuocHuy430.service.CartService;
import com.hutech.VoTranQuocHuy430.service.OrderService;
import com.hutech.VoTranQuocHuy430.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @PostMapping("/create-payment")
    public String createPayment(
            HttpServletRequest req,
            @RequestParam String customerName,
            @RequestParam String customerAddress,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String notes,
            @RequestParam String paymentMethod,
            Model model) throws Exception {

        // Lấy user từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lấy danh sách CartItem từ CartService
        List<CartItem> cartItems = cartService.getCartItems();

        // Tạo đơn hàng mới
        Order order = orderService.createOrder(customerName, customerAddress, phoneNumber, email, notes, paymentMethod, cartItems, user);

        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            ResponseEntity<PaymentResDTO> paymentResponse = orderService.initiateVnpayPayment(req, order);
            return "redirect:" + paymentResponse.getBody().getURL();
        }

        List<OrderDetail> orderDetails = order.getOrderDetails(); // Lấy chi tiết đơn hàng
        if (orderDetails == null) {
            orderDetails = orderService.getOrderDetailById(order.getId());
        }

        model.addAttribute("message", "Your order has been successfully placed.");
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("customerName", order.getCustomerName());
        model.addAttribute("customerAddress", order.getCustomerAddress());
        model.addAttribute("phoneNumber", order.getPhoneNumber());
        model.addAttribute("email", order.getEmail());

        return "cart/order-confirmation";
    }

    @GetMapping("/vnpay_return")
    public String vnPayReturn(HttpServletRequest req, Model model) {
        Map<String, String> vnp_Params = new HashMap<>();
        Map<String, String[]> requestParams = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            vnp_Params.put(entry.getKey(), entry.getValue()[0]);
        }

        // Validate vnp_SecureHash
        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        String signValue = Config.hashAllFields(vnp_Params);
        if (signValue.equals(vnp_SecureHash)) {
            // Payment success logic
            String orderId = vnp_Params.get("vnp_TxnRef");
            Order order = orderService.findById(Long.parseLong(orderId)).orElse(null);
            if (order != null) {
                order.setOrderStatus(OrderStatus.COMPLETED); // Hoặc trạng thái thích hợp
                orderService.save(order);

                // Retrieve order details
                List<OrderDetail> orderDetails = order.getOrderDetails();
                if (orderDetails == null) {
                    orderDetails = orderService.getOrderDetailById(order.getId());
                }

                model.addAttribute("status", "Success");
                model.addAttribute("transactionId", vnp_Params.get("vnp_TransactionNo"));
                model.addAttribute("amount", vnp_Params.get("vnp_Amount"));
                model.addAttribute("message", "Your order has been successfully placed. Thank you for your purchase!");

                // Add order details to the model
                model.addAttribute("order", order);
                model.addAttribute("orderDetails", orderDetails);
                model.addAttribute("customerName", order.getCustomerName());
                model.addAttribute("customerAddress", order.getCustomerAddress());
                model.addAttribute("phoneNumber", order.getPhoneNumber());
                model.addAttribute("email", order.getEmail());
            } else {
                model.addAttribute("status", "Failed");
                model.addAttribute("message", "Order not found.");
            }
        } else {
            model.addAttribute("status", "Failed");
            model.addAttribute("message", "Payment verification failed.");
        }

        return "cart/confirmVnPay";
    }
}
