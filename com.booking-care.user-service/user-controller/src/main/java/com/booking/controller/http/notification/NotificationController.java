package com.booking.controller.http.notification;


import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.UserRepository;
import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.OtpRequest;
import com.booking.model.dto.request.PasswordResetVerifyRequest;
import com.booking.model.dto.response.OtpResponse;
import com.booking.model.dto.response.ResetPasswordResponse;
import com.booking.service.email.EmailService;
import com.booking.service.email.IEmailService;
import com.booking.service.user.IUserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
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
//    @PostMapping("/reset-password")
//    public ResponseEntity<String> verifyOtpAndResetPassword(@RequestBody PasswordResetVerifyRequest request) throws DataNotFoundException {
//        boolean success = emailService.verifyOtpAndResetPassword(
//                request.getOtp(),
//                request.getNewPassword(),
//                request.getConfirmNewPassword()
//        );
//        if (success) {
//            return ResponseEntity.ok("Password reset successful.");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP has expired.");
//        }
//    }
    private boolean isValidEmail(String email) {
        // Kiểm tra email có tồn tại trong hệ thống không (có thể query DB)
        return true; // Logic kiểm tra email hợp lệ
    }

    private String generateOtp() {
        int otp = new Random().nextInt(900000) + 100000; // tạo mã OTP 6 chữ số
        return String.valueOf(otp);
    }

    private void saveOtpInCacheOrDb(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setOtp(passwordEncoder.encode(otp));
        user.setExpiryOtp(LocalDateTime.now().plusMinutes(2));
        userRepository.save(user);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpResponse request, HttpSession session) {
        String email = emailService.verifyOtp(request.getOtp());

        if (email == null) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn");
        }

        // Lưu email vào session để dùng ở bước 2
        session.setAttribute("resetEmail", email);
        return ResponseEntity.ok("OTP hop le");
    }

    /**
     * Bước 2: Đặt lại mật khẩu sau khi OTP đã được xác minh
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordResponse request, HttpSession session)
            throws DataNotFoundException {
        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP chua duoc xac minh");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body("Mat khau xac nhan khong khop");
        }

        // Đặt lại mật khẩu
        emailService.updatePasswordByEmail(email, request.getNewPassword());

        // Xóa email khỏi session
        session.removeAttribute("resetEmail");

        return ResponseEntity.ok("Dat lai mat khau thanh cong");
    }
}
