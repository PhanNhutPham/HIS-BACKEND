package com.booking.controller.http.user;

import com.booking.domain.models.entities.User;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.InvalidPasswordException;
import com.booking.model.dto.request.PasswordResetVerifyRequest;
import com.booking.model.dto.request.ResetPasswordRequest;
import com.booking.model.dto.request.UpdateUserRequest;
import com.booking.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.booking.model.dto.response.ResponseObject;
import com.booking.exceptions.ValidationException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PatchMapping("/update/{user_id}")
    public ResponseEntity<ResponseObject> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                     @PathVariable("user_id") String user_id,
                                                     BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User updateUser = userService.updateUser(user_id, updateUserRequest);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(updateUser)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/change-password/{user_id}")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                        BindingResult result,
                                                        @PathVariable("user_id") String user_id) {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        try {
            userService.resetPassword(user_id, request);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(null)
                    .message("Reset password successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Invalid old password")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("User not found")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("An error occurred")
                    .data("")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PostMapping("/uploads/{userId}")
    public ResponseEntity<ResponseObject> uploadAvatarUser(
            @PathVariable("userId") String userId,
            @RequestParam("file") MultipartFile file
    ) {

        try {
            userService.uploadAvatar(userId, file);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload image avatar successfully")
                    .status(HttpStatus.CREATED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading image: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseObject> getUserById(@PathVariable("userId") String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("User fetched successfully")
                    .data(user)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("User not found")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("An error occurred: " + e.getMessage())
                    .build());
        }
    }
//    @PostMapping("/uploads/{userId}")
//    public ResponseEntity<ResponseObject> uploadAvatarUser(
//            @PathVariable("userId") String userId,
//            @RequestParam("file") MultipartFile file
//    ) {
//
//        try {
//            userService.uploadAvatar(userId, file);
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .message("Upload image avatar successfully")
//                    .status(HttpStatus.CREATED)
//                    .data(null)
//                    .build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ResponseObject.builder()
//                            .message("Error uploading image: " + e.getMessage())
//                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .build());
//        }
//    }

}
