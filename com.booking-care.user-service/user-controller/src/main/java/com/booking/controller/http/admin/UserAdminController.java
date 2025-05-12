package com.booking.controller.http.admin;

import com.booking.domain.models.entities.Role;
import com.booking.domain.models.entities.User;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.infrastructure.persistence.mapper.UserJPA;
import com.booking.model.dto.request.UserCreateRequest;
import com.booking.model.dto.request.UserCreatedEvent;
import com.booking.model.dto.response.UserPageResponse;
import com.booking.service.kafka.KafkaProducerService;
import com.booking.service.user.IUserService;
import com.booking.utils.PageConstant;
import jakarta.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.booking.model.dto.response.ResponseObject;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {

    private final IUserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final UserJPA userRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllUsers(
            @RequestParam(name = "pageNumber", defaultValue = PageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = PageConstant.SORT_DIR, required = false) String sortDir
    ) {
        UserPageResponse userPageResponse = userService.findAllUsers(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(userPageResponse)
                .message("Get list of users information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/block/{userId}/{active}")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @PathVariable("userId") String userId,
            @PathVariable("active") boolean active
    ) throws Exception {
        userService.blockOrEnable(userId, active);
        String message = active ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
    @GetMapping("/avatar/{userId}")
    public ResponseEntity<?> getAvatar(@PathVariable("userId") String userId) {
        try {
            // Lấy thông tin người dùng từ cơ sở dữ liệu
            User user = userRepository.findUserByUserId(userId)
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
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createUserByAdmin(
            @Valid @RequestBody UserCreateRequest userCreateRequest,
            BindingResult bindingResult) throws Exception {

        // Kiểm tra validate
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Invalid user creation request: " + bindingResult.toString());
        }

        // Tạo user
        var createdUser = userService.createUserByAdmin(userCreateRequest);

        // Build Kafka event từ cả User và request (bổ sung thông tin phụ)
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(createdUser.getUserId())
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .roleName(
                        createdUser.getRoles().stream()
                                .findFirst()
                                .map(Role::getNameRole)
                                .orElse(null)
                )
                .departmentId(userCreateRequest.getDepartmentId()) // nếu dùng cho bác sĩ
                .roomId(userCreateRequest.getRoomId())
                .password(userCreateRequest.getPassword())
                .phone(userCreateRequest.getPhone())
                .gender(userCreateRequest.getGender())// nếu dùng cho phòng
                .degree(userCreateRequest.getDegree())
                .build();

        // Gửi Kafka Event
        String kafkaResponse = kafkaProducerService.sendUserCreatedEvent(event);

        // Trả kết quả
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("User account created successfully by admin. Kafka response: " + kafkaResponse)
                .data(createdUser)
                .build());
    }
    @PostMapping("/create/protocol")
    public ResponseEntity<ResponseObject> createProtocolByAdmin(
            @Valid @RequestBody UserCreateRequest userCreateRequest,
            BindingResult bindingResult) throws Exception {

        // Kiểm tra validate
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Invalid user creation request: " + bindingResult.toString());
        }

        // Tạo user
        var createdUser = userService.createProtocolByAdmin(userCreateRequest);

        // Build Kafka event từ cả User và request (bổ sung thông tin phụ)
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(createdUser.getUserId())
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .roleName(
                        createdUser.getRoles().stream()
                                .findFirst()
                                .map(Role::getNameRole)
                                .orElse(null)
                )
                .phone(userCreateRequest.getPhone())
                .gender(userCreateRequest.getGender())
                .password(userCreateRequest.getPassword())
                .build();

        // Gửi Kafka Event
        String kafkaResponse = kafkaProducerService.sendUserCreatedEvent(event);

        // Trả kết quả
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("User account created successfully by admin. Kafka response: " + kafkaResponse)
                .data(createdUser)
                .build());
    }
}

