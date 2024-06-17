package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.DTO.PaymentResDTO;
import com.hutech.VoTranQuocHuy430.config.Config;
import com.hutech.VoTranQuocHuy430.model.*;
import com.hutech.VoTranQuocHuy430.service.CartService;
import com.hutech.VoTranQuocHuy430.service.OrderService;
import com.hutech.VoTranQuocHuy430.service.UserService;
import com.hutech.VoTranQuocHuy430.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

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
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            model.addAttribute("error", "Your cart is empty. Please add products to your cart before proceeding to checkout.");
            return "redirect:/cart"; // Redirect if cart is empty
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
                              HttpServletRequest request,
                              Model model) throws Exception {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }

        // Lấy user từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderService.createOrder(customerName, customerAddress, phoneNumber, email, notes, paymentMethod, cartItems, user);
        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            order.setOrderStatus(OrderStatus.PENDING_SHIPMENT);
            orderService.save(order);
            ResponseEntity<PaymentResDTO> paymentResponse = orderService.initiateVnpayPayment(request, order);
            return "redirect:" + paymentResponse.getBody().getURL();
        } else if ("cash".equalsIgnoreCase(paymentMethod)) {
            order.setOrderStatus(OrderStatus.PENDING_SHIPMENT);
            orderService.save(order);
        }

        List<OrderDetail> orderDetails = order.getOrderDetails(); // Lấy chi tiết đơn hàng

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails); // Thêm chi tiết đơn hàng vào model
        model.addAttribute("customerName", order.getCustomerName());
        model.addAttribute("customerAddress", order.getCustomerAddress());
        model.addAttribute("phoneNumber", order.getPhoneNumber());
        model.addAttribute("email", order.getEmail());

        return "cart/order-confirmation";
    }


    @GetMapping("/confirmation")
    public String orderConfirmation(@RequestParam(required = false) String vnp_TransactionNo,
                                    @RequestParam(required = false) String vnp_TxnRef,
                                    @RequestParam(required = false) String vnp_Amount,
                                    @RequestParam(required = false) String vnp_ResponseCode,
                                    @RequestParam(required = false) Long orderId,
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

        if (orderId != null) {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
                model.addAttribute("order", order);
                model.addAttribute("orderDetails", orderDetails);
            }
        }

        return "cart/order-confirmation";
    }

    @GetMapping("/history")
    public String orderHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Order> orders = orderService.getOrderHistory(user.getId());
        model.addAttribute("orders", orders);

        return "order/history";
    }

    @GetMapping("/cancel")
    public String cancelOrder(@RequestParam Long orderId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderService.getOrderById(orderId);
        if (order != null && order.getUser().getId().equals(user.getId()) && order.getOrderStatus() == OrderStatus.PENDING_SHIPMENT) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderService.save(order);
        }

        return "redirect:/order/history";
    }

}
