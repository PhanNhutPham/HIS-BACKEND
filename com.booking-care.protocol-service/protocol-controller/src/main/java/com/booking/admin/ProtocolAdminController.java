package com.booking.admin;

import com.booking.domain.models.entities.Protocol;
import com.booking.domain.models.event.UserCreatedEvent;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.impl.ProtocolServiceImpl;
import com.booking.infrastructure.persistence.mapper.ProtocolJPA;
import com.booking.model.dto.response.ProtocolResponse;
import com.booking.model.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/protocols")
public class ProtocolAdminController {
    private final ObjectMapper objectMapper;
    private final ProtocolServiceImpl protocolService;
    private final ProtocolJPA protocolRepository;


    @KafkaListener(topics = "user-created-topic", groupId = "protocol-service-group-v2")
    public void handleProtocolCreatedEvent(String message) {
        try {
            System.out.println("Received message: " + message);  // Log message nhận được
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
            // Kiểm tra roleName trước khi tạo
            if (!"PROTOCOL".equalsIgnoreCase(event.getRoleName())) {
                System.out.println("Không phải role PROTOCOL, bỏ qua event");
                return;
            }
            Protocol protocol = Protocol.builder()
                    .fullName(event.getUsername())  // map username -> fullName
                    .email(event.getEmail())
                    .userId(event.getUserId())
                    .phoneNumber(event.getPhone())
                    .password(event.getPassword())
                    .gender(event.getGender())
                    .build();

            protocolService.createProtocol(protocol);
            System.out.println("Tạo lễ tân tự động thành công: " + protocol.getFullName());
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý event tạo lễ tân: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả protocol")
    public ResponseEntity<List<ProtocolResponse>> getAllProtocols() {
        return ResponseEntity.ok(protocolService.getAllProtocols());
    }

    @PutMapping("/upload/{id}")
    @Operation(summary = "Cập nhật thông tin protocol theo ID")
    public ResponseEntity<ProtocolResponse> updateProtocol(
            @PathVariable("id") String id,
            @RequestBody ProtocolResponse updatedProtocol
    ) {
        return ResponseEntity.ok(protocolService.updateProtocol(id, updatedProtocol));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá protocol theo ID")
    public ResponseEntity<Void> deleteProtocol(@PathVariable("id") String id) {
        protocolService.deleteProtocol(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/uploads/{protocolId}")
    @Operation(summary = "Tải ảnh đại diện cho protocol")
    public ResponseEntity<ResponseObject> uploadAvatarProtocol(
            @PathVariable("protocolId") String protocolId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            protocolService.uploadAvatar(protocolId, file);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload protocol avatar successfully")
                    .status(HttpStatus.CREATED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading protocol avatar: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @GetMapping("/avatar/{protocolId}")
    @Operation(summary = "Lấy avatar của protocol theo ID")
    public ResponseEntity<?> getProtocolAvatarById(@PathVariable("protocolId") String protocolId) {
        try {
            // Lấy thông tin protocol từ protocolId
            Protocol protocol = protocolRepository.findById(protocolId)
                    .orElseThrow(() -> new DataNotFoundException(ResultCode.DATA_NOT_FOUND));

            String imageName = protocol.getAvatarProtocol();

            // Tạo đường dẫn tới file hình ảnh
            Path imagePath = Paths.get("uploads", imageName);

            // Kiểm tra nếu file tồn tại
            if (Files.exists(imagePath)) {
                UrlResource resource = new UrlResource(imagePath.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // có thể dùng MediaTypeFactory nếu cần xác định động
                        .body(resource);
            } else {
                // Trả về ảnh mặc định nếu avatar không tồn tại
                UrlResource resource = new UrlResource(Paths.get("uploads", "default-avatar.jpg").toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("Error fetching avatar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


}
