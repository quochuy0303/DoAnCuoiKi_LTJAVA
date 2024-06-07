package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.config.Config;
import com.hutech.VoTranQuocHuy430.model.CartItem;
import com.hutech.VoTranQuocHuy430.model.Order;
import com.hutech.VoTranQuocHuy430.service.CartService;
import com.hutech.VoTranQuocHuy430.service.OrderService;
import com.hutech.VoTranQuocHuy430.DTO.PaymentResDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout() {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            // Nếu không có sản phẩm, trả về thông báo lỗi và chuyển hướng người dùng trở lại trang giỏ hàng
            return "redirect:/cart"; // Chuyển hướng đến trang giỏ hàng
        }
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam String customerName,
                              @RequestParam String customerAddress,
                              @RequestParam String phoneNumber,
                              @RequestParam String email,
                              @RequestParam String notes,
                              @RequestParam String paymentMethod,
                              HttpServletRequest request) throws Exception {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }

        Order order = orderService.createOrder(customerName, customerAddress, phoneNumber, email, notes, paymentMethod, cartItems);

        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            ResponseEntity<PaymentResDTO> paymentResponse = orderService.initiateVnpayPayment(request, order);
            return "redirect:" + paymentResponse.getBody().getURL();
        }

        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(@RequestParam String vnp_TransactionNo,
                                    @RequestParam String vnp_TxnRef,
                                    @RequestParam String vnp_Amount,
                                    @RequestParam String vnp_ResponseCode,
                                    Model model) {
        model.addAttribute("status", "Success");
        model.addAttribute("transactionId", vnp_TransactionNo);
        model.addAttribute("amount", vnp_Amount);
        model.addAttribute("message", "Your order has been successfully placed. Thank you for your purchase!");
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
        } else {
            model.addAttribute("status", "Failed");
            model.addAttribute("message", "Payment verification failed.");
        }

        return "cart/order-confirmation";
    }
}
