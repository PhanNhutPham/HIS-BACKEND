package com.booking.service.email;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendOtpEmail(String email, String otp) throws MessagingException;
}
