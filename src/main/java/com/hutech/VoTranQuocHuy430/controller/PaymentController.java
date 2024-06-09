package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.DTO.PaymentResDTO;
import com.hutech.VoTranQuocHuy430.config.Config;
import com.hutech.VoTranQuocHuy430.model.CartItem;
import com.hutech.VoTranQuocHuy430.model.Order;
import com.hutech.VoTranQuocHuy430.service.CartService;
import com.hutech.VoTranQuocHuy430.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        List<CartItem> cartItems = cartService.getCartItems();
        Order order = orderService.createOrder(customerName, customerAddress, phoneNumber, email, notes, paymentMethod, cartItems);

        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            ResponseEntity<PaymentResDTO> paymentResponse = orderService.initiateVnpayPayment(req, order);
            return "redirect:" + paymentResponse.getBody().getURL();
        }

        model.addAttribute("message", "Your order has been successfully placed.");
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", order.getOrderDetails());
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
            Order order = orderService.findById(Long.parseLong(orderId)).orElseThrow();
            order.setStatus("PAID");
            orderService.save(order);

            model.addAttribute("status", "Success");
            model.addAttribute("transactionId", vnp_Params.get("vnp_TransactionNo"));
            model.addAttribute("amount", vnp_Params.get("vnp_Amount"));
            model.addAttribute("message", "Your order has been successfully placed. Thank you for your purchase!");

            // Add order details to the model
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", order.getOrderDetails());
            model.addAttribute("customerName", order.getCustomerName());
            model.addAttribute("customerAddress", order.getCustomerAddress());
            model.addAttribute("phoneNumber", order.getPhoneNumber());
            model.addAttribute("email", order.getEmail());
        } else {
            model.addAttribute("status", "Failed");
            model.addAttribute("message", "Payment verification failed.");
        }

        return "cart/confirmVnPay";
    }

}
