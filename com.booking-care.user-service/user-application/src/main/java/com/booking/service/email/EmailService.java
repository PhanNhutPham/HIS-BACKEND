package com.booking.service.email;


import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.UserRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Mã OTP để Đặt lại Mật khẩu của Bạn");

        String htmlContent = "<html>" +
                "<body>" +
                "<h2>Mã OTP Để Đặt lại Mật khẩu</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng sử dụng mã OTP sau để tiếp tục:</p>" +
                "<h3>" + otp + "</h3>" +
                "<p>Mã OTP này có hiệu lực trong 2 phút.</p>" +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ.</p>" +
                "<br>" +
                "<p>Trân trọng,</p>" +
                "</body>" +
                "</html>";

        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    @Override
    public boolean verifyOtpAndResetPassword(String otp, String newPassword, String confirmNewPassword) throws DataNotFoundException {
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        // Lấy tất cả user có OTP còn hiệu lực
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getOtp() != null
                        && passwordEncoder.matches(otp, user.getOtp())
                        && user.getExpiryOtp() != null
                        && user.getExpiryOtp().isAfter(LocalDateTime.now()))
                .toList();

        if (users.isEmpty()) {
            return false;
        }
        User user = users.get(0); // Giả sử OTP là duy nhất

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setExpiryOtp(null);
        userRepository.save(user);

        return true;
    }

    @Override
    public void sendPasswordEmail(String toEmail, String password) throws MessagingException {
        try {
            String subject = "Mật khẩu tài khoản của bạn";
            String body = "<html>" +
                    "<body>" +
                    "<h2>Tài khoản của bạn đã được tạo</h2>" +
                    "<p>Tài khoản của bạn đã được tạo thành công. Dưới đây là mật khẩu tạm thời của bạn:</p>" +
                    "<h3>" + password + "</h3>" +
                    "<p>Mật khẩu này sẽ có hiệu lực trong vòng 1 giờ. Vui lòng thay đổi mật khẩu càng sớm càng tốt.</p>" +
                    "<p>Nếu bạn không yêu cầu tạo tài khoản, vui lòng liên hệ với bộ phận hỗ trợ ngay lập tức.</p>" +
                    "<br>" +
                    "<p>Trân trọng,</p>" +
                    "<p>Đội ngũ hỗ trợ của chúng tôi</p>" +
                    "</body>" +
                    "</html>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();  // Log lỗi chi tiết để dễ dàng xử lý
            throw new MessagingException("Error sending password email", e);
        }
    }

    @Override
    public String verifyOtp(String otp) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getOtp() != null
                        && passwordEncoder.matches(otp, user.getOtp())
                        && user.getExpiryOtp() != null
                        && user.getExpiryOtp().isAfter(LocalDateTime.now()))
                .toList();

        return users.isEmpty() ? null : users.get(0).getEmail();
    }

    @Override
    public void updatePasswordByEmail(String email, String newPassword) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setExpiryOtp(null);
        userRepository.save(user);
    }

}