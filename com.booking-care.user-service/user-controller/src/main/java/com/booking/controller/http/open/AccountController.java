package com.booking.controller.http.open;

import com.booking.domain.models.entities.User;

import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.PasswordResetRequest;
import com.booking.model.dto.request.PasswordResetVerifyRequest;
import com.booking.model.dto.request.UserRegisterRequest;
import com.booking.model.dto.response.ResponseObject;

import com.booking.service.user.IUserService;
import jakarta.validation.Valid;
import com.booking.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUsers(@Valid @RequestBody UserRegisterRequest userRequest,
                                                      BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = userService.createUser(userRequest);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("Account registration successful")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) throws DataNotFoundException {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP sent to your email.");
    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<String> verifyOtpAndResetPassword(@RequestBody PasswordResetVerifyRequest request) throws DataNotFoundException {
//        boolean success = userService.verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
//        if (success) {
//            return ResponseEntity.ok("Password reset successful.");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP has expired.");
//        }
//    }
}
