package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.DTO.PaymentResDTO;
import com.hutech.VoTranQuocHuy430.model.CartItem;
import com.hutech.VoTranQuocHuy430.model.Order;
import com.hutech.VoTranQuocHuy430.service.CartService;
import com.hutech.VoTranQuocHuy430.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout() {
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
        } else if ("cash".equalsIgnoreCase(paymentMethod)) {
            order.setStatus("COD"); // Set status for COD
            orderService.save(order);
            return "redirect:/order/confirmation?orderId=" + order.getId();
        }

        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(@RequestParam(required = false) String vnp_TransactionNo,
                                    @RequestParam(required = false) String vnp_TxnRef,
                                    @RequestParam(required = false) String vnp_Amount,
                                    @RequestParam(required = false) String vnp_ResponseCode,
                                    Model model) {
        if (vnp_TransactionNo != null && vnp_TxnRef != null && vnp_Amount != null && vnp_ResponseCode != null) {
            model.addAttribute("status", "Success");
            model.addAttribute("transactionId", vnp_TransactionNo);
            model.addAttribute("amount", vnp_Amount);
            model.addAttribute("message", "Your order has been successfully placed. Thank you for your purchase!");
        } else {
            model.addAttribute("status", "Pending");
            model.addAttribute("message", "Your order has been placed and will be processed. Thank you for your purchase!");
        }
        return "cart/order-confirmation";
    }
}
