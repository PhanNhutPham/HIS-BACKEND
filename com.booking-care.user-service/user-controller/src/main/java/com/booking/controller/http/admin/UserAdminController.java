package com.booking.controller.http.admin;

import com.booking.domain.models.entities.Role;
import com.booking.model.dto.request.UserCreateRequest;
import com.booking.model.dto.request.UserCreatedEvent;
import com.booking.model.dto.response.UserPageResponse;
import com.booking.service.kafka.KafkaProducerService;
import com.booking.service.user.IUserService;
import com.booking.utils.PageConstant;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .phone(userCreateRequest.getPhone())
                .gender(userCreateRequest.getGender())// nếu dùng cho phòng
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

