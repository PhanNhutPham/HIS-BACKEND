package com.booking.controller.http.open;

import com.booking.domain.models.entities.Doctor;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.infrastructure.persistence.mapper.DoctorJPA;
import com.booking.model.dto.response.DoctorResponse;
import com.booking.model.dto.response.ResponseObject;
import com.booking.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final ObjectMapper objectMapper;
    private final DoctorJPA doctorRepository;
    public DoctorController(DoctorService doctorService, ObjectMapper objectMapper, DoctorJPA doctorRepository) {
        this.doctorService = doctorService;
        this.objectMapper = objectMapper;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Lấy thông tin bác sĩ theo User ID")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(doctorService.getDoctorByUserId(userId));
    }

    @PutMapping("/upload/{userId}")
    public ResponseEntity<DoctorResponse> updateDoctorByUserId(@PathVariable("userId") String userId, @RequestBody DoctorResponse updatedDoctor) {
        return ResponseEntity.ok(doctorService.updateDoctorByUserId(userId, updatedDoctor));
    }
    @GetMapping("/avatar/{userId}")
    public ResponseEntity<?> getDoctorAvatar(@PathVariable("userId") String userId) {
        try {
            // Lấy thông tin bác sĩ từ userId
            Doctor doctor = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new DataNotFoundException(ResultCode.DATA_NOT_FOUND));

            // Lấy tên file avatar từ entity Doctor
            String imageName = doctor.getAvatarDoctor();

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
    @PostMapping("/upload-avatar/{userId}")
    public ResponseEntity<ResponseObject> uploadAvatarByUserId(
            @PathVariable("userId") String userId,
            @RequestParam("file") MultipartFile file) {

        try {
            doctorService.uploadAvatarByUserId(userId, file);
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
}
