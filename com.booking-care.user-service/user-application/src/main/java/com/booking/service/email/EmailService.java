package com.booking.service.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender javaMailSender;

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
                "<p>Mã OTP này có hiệu lực trong 1 phút.</p>" +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ.</p>" +
                "<br>" +
                "<p>Trân trọng,</p>" +
                "</body>" +
                "</html>";

        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }
}
