package com.booking.controller.http.user;

import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.UserRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.InvalidPasswordException;
import com.booking.model.dto.request.PasswordResetVerifyRequest;
import com.booking.model.dto.request.ResetPasswordRequest;
import com.booking.model.dto.request.UpdateUserRequest;
import com.booking.model.dto.response.UserResponse;
import com.booking.service.user.IUserService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.booking.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.booking.model.dto.response.ResponseObject;
import com.booking.exceptions.ValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

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
            UserResponse userResponse = convertToUserResponse(user);

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("User fetched successfully")
                    .data(userResponse)
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
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setProfileImage(user.getProfileImage());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setGender(user.getGender());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCreate_at(user.getCreate_at());
        response.setUpdate_at(user.getUpdate_at());
        return response;
    }
    @GetMapping("/avatar/{userId}")
    public ResponseEntity<?> getAvatar(@PathVariable("userId") String userId) {
        try {
            // Lấy thông tin người dùng từ cơ sở dữ liệu
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

            // Lấy tên file ảnh từ thông tin người dùng
            String imageName = user.getProfileImage();

            // Tạo đường dẫn tới file hình ảnh
            Path imagePath = Paths.get("uploads", imageName);

            // Kiểm tra nếu hình ảnh tồn tại
            if (Files.exists(imagePath)) {
                // Trả về hình ảnh
                UrlResource resource = new UrlResource(imagePath.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Có thể thay đổi tùy theo định dạng ảnh
                        .body(resource);
            } else {
                // Trả về hình ảnh mặc định nếu không tìm thấy ảnh avatar
                UrlResource resource = new UrlResource(Paths.get("uploads", "default-avatar.jpg").toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("Error fetching image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
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
//                            .message("Error uploading imagpermissionse: " + e.getMessage())
//                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .build());
//        }
//    }

}
