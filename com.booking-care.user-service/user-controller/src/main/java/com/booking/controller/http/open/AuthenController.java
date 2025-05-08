package com.booking.controller.http.open;

import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;
import com.booking.model.dto.request.LogoutRequest;
import com.booking.model.dto.request.RefreshTokenRequest;
import com.booking.model.dto.response.LogoutResponse;
import com.booking.model.dto.response.ResponseObject;
import com.booking.model.dto.request.UserLoginRequest;
import com.booking.model.dto.request.ValidateTokenRequest;
import com.booking.model.dto.response.LoginResponse;
import com.booking.model.dto.response.ValidateTokenResponse;
import com.booking.service.authen.IAuthenService;
import com.booking.service.token.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import com.booking.exceptions.ValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenController {
    private final IAuthenService authenService;
    private final ITokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request
    ) throws Exception {
        String token = authenService.login(userLoginRequest);
        String userAgent = request.getHeader("User-Agent");
        User userDetail = authenService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getUserId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Login successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }

    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponse> validateToken(@RequestBody ValidateTokenRequest request) throws Exception {
        ValidateTokenResponse result = authenService.validateToken(request);

        if (result.isValid()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        try {
            authenService.logout(logoutRequest);
            return ResponseEntity.ok(LogoutResponse.builder().message("Logout successful").build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(LogoutResponse.builder().message("Logout failed: " + e.getMessage()).build());
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseObject> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User userDetail = authenService.getUserDetailsFromRefreshToken(refreshTokenRequest.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenRequest.getRefreshToken(), userDetail);
        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getUserId()).build();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .data(loginResponse)
                        .message("Refresh token successfully")
                        .status(HttpStatus.OK)
                        .build());

    }

}
