package com.booking.open;

import com.booking.domain.models.entities.Protocol;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.impl.ProtocolServiceImpl;
import com.booking.infrastructure.persistence.mapper.ProtocolJPA;
import com.booking.model.dto.response.ProtocolResponse;
import com.booking.model.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/protocols")
@RequiredArgsConstructor
public class ProtocolController {
    private final ProtocolServiceImpl protocolService;
    private final ProtocolJPA protocolRepository;

    @GetMapping("/{userId}")
    @Operation(summary = "Lấy thông tin protocol theo User ID")
    public ResponseEntity<Protocol> getProtocolByUserId(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(protocolService.getProtocolByUserId(userId));
    }

    @PutMapping("/upload/{userId}")
    @Operation(summary = "Cập nhật thông tin protocol theo User ID")
    public ResponseEntity<ProtocolResponse> updateProtocolByUserId(
            @PathVariable("userId") String userId,
            @RequestBody ProtocolResponse updatedProtocol) {
        return ResponseEntity.ok(protocolService.updateProtocolByUserId(userId, updatedProtocol));
    }

    @PostMapping("/upload-avatar/{userId}")
    @Operation(summary = "Upload ảnh đại diện cho protocol theo User ID")
    public ResponseEntity<ResponseObject> uploadProtocolAvatarByUserId(
            @PathVariable("userId") String userId,
            @RequestParam("file") MultipartFile file) {

        try {
            protocolService.uploadAvatarByUserId(userId, file);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload image avatar successfully for userId: " + userId)
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

    @GetMapping("/avatar/{protocolId}")
    public ResponseEntity<?> getProtocolImageById(@PathVariable("protocolId") String protocolId) {
        try {
            // Tìm thông tin protocol từ ID
            Protocol protocol = protocolRepository.findByUserId(protocolId)
                    .orElseThrow(() -> new DataNotFoundException(ResultCode.DATA_NOT_FOUND));

            String imageName = protocol.getAvatarProtocol(); // Lấy tên file ảnh

            // Tạo đường dẫn tới file hình ảnh
            Path imagePath = Paths.get("uploads", imageName);

            // Kiểm tra nếu file tồn tại
            if (Files.exists(imagePath)) {
                UrlResource resource = new UrlResource(imagePath.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // hoặc xác định content type từ file extension nếu cần
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
