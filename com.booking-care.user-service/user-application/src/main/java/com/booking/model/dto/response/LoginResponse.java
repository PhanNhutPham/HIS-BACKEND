package com.booking.model.dto.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String id;
    private String username;
    private List<String> roles;
}
