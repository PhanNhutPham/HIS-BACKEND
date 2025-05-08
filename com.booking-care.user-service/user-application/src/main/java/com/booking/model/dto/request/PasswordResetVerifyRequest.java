package com.booking.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetVerifyRequest {
    private String email;
    private String otp;
    private String newPassword;
}
