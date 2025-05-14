package com.booking.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordResponse {
    private String newPassword;
    private String confirmNewPassword;
}
