package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.model.Order;
import com.hutech.VoTranQuocHuy430.model.PasswordResetToken;
import com.hutech.VoTranQuocHuy430.model.User;
import com.hutech.VoTranQuocHuy430.repository.PasswordResetTokenRepository;
import com.hutech.VoTranQuocHuy430.service.EmailService;
import com.hutech.VoTranQuocHuy430.service.OrderService;
import com.hutech.VoTranQuocHuy430.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final OrderService orderService;
    private final PasswordResetTokenRepository tokenRepository;

    @GetMapping("/user/profile")
    public String getUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        return "users/register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validate đối tượng User
                                   @NotNull BindingResult bindingResult, // Kết quả của quá trình validate
 Model model) {
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định cho người dùng
        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("email", "");
        return "users/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (!userOptional.isPresent()) {
            model.addAttribute("error", "Email không tồn tại");
            return "users/forgot-password";
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(resetToken);

        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendEmail(email, "Đặt lại mật khẩu", "Nhấn vào liên kết để đặt lại mật khẩu: " + resetUrl);

        model.addAttribute("message", "OTP đã được gửi tới email của bạn");
        return "users/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> resetTokenOptional = tokenRepository.findByToken(token);
        if (!resetTokenOptional.isPresent() || resetTokenOptional.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
            return "users/reset-password";
        }
        model.addAttribute("token", token);
        return "users/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        try {
            Optional<PasswordResetToken> resetTokenOptional = tokenRepository.findByToken(token);
            if (!resetTokenOptional.isPresent() || resetTokenOptional.get().getExpiryDate().isBefore(LocalDateTime.now())) {
                model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
                return "users/reset-password";
            }

            Optional<User> userOptional = userService.findByEmail(resetTokenOptional.get().getEmail());
            if (!userOptional.isPresent()) {
                model.addAttribute("error", "Không tìm thấy người dùng");
                return "users/reset-password";
            }

            User user = userOptional.get();
            userService.updatePassword(user, password);
            tokenRepository.delete(resetTokenOptional.get());

            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace(); // Ghi lại thông tin lỗi vào log
            model.addAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
            return "users/reset-password";
        }
    }

    @GetMapping("/user/profile/edit")
    public String showEditForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "users/edit-user";
    }

    @PostMapping("/user/profile/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Tìm người dùng hiện tại theo ID
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Kiểm tra lỗi từ BindingResult trước khi cập nhật các thông tin khác
        if (result.hasErrors()) {
            model.addAttribute("user", existingUser);  // Thêm lại người dùng hiện tại vào model để không mất dữ liệu
            return "users/edit-user";
        }

        // Cập nhật thông tin người dùng
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());

        // Xử lý mật khẩu
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            // Giữ nguyên mật khẩu hiện tại
            user.setPassword(existingUser.getPassword());
        } else {
            // Mã hóa mật khẩu mới
            existingUser.setPassword(userService.encodePassword(user.getPassword()));
        }

        userService.updateUser(existingUser);
        return "redirect:/user/profile";
    }



}

