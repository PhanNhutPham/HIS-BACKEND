package com.booking.service.email;

import com.booking.exceptions.DataNotFoundException;
import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendOtpEmail(String email, String otp) throws MessagingException;
    boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException;
    void sendPasswordEmail(String toEmail, String password) throws MessagingException;

    String verifyOtp(String otp);
    void updatePasswordByEmail(String email, String newPassword) throws DataNotFoundException;
}