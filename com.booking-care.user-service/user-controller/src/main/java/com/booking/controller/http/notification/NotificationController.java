package com.booking.controller.http.notification;


import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.OtpRequest;
import com.booking.model.dto.request.PasswordResetVerifyRequest;
import com.booking.service.email.IEmailService;
import com.booking.service.user.IUserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final IEmailService emailService;
    private final IUserService userService;
    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) throws MessagingException {
        String email = otpRequest.getEmail();

        // Kiểm tra email có tồn tại trong hệ thống hay không (tùy vào logic của bạn)
        if (email == null || !isValidEmail(email)) {
            return ResponseEntity.badRequest().body("Email không hợp lệ");
        }

        // Tạo mã OTP tự động
        String otp = generateOtp();

        // Gửi OTP vào email
        emailService.sendOtpEmail(email, otp);

        // Lưu OTP vào DB/Redis nếu cần xác minh sau
        // có thể lưu vào DB/Cache để đối chiếu sau khi người dùng nhập OTP
        saveOtpInCacheOrDb(email, otp);

        return ResponseEntity.ok("OTP đã được gửi tới email " + email);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> verifyOtpAndResetPassword(@RequestBody PasswordResetVerifyRequest request) throws DataNotFoundException {
        boolean success = userService.verifyOtpAndResetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP has expired.");
        }
    }
    private boolean isValidEmail(String email) {
        // Kiểm tra email có tồn tại trong hệ thống không (có thể query DB)
        return true; // Logic kiểm tra email hợp lệ
    }

    private String generateOtp() {
        int otp = new Random().nextInt(900000) + 100000; // tạo mã OTP 6 chữ số
        return String.valueOf(otp);
    }

    private void saveOtpInCacheOrDb(String email, String otp) {
        // Lưu OTP vào Redis hoặc Database nếu bạn muốn xác minh sau này
        // Example: cache.put(email, otp);
    }

}
