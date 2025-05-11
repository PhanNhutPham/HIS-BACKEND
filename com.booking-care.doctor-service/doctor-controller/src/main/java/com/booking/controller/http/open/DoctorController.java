package com.booking.controller.http.open;

import com.booking.domain.models.entities.Doctor;
import com.booking.model.dto.response.DoctorResponse;
import com.booking.model.dto.response.ResponseObject;
import com.booking.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final ObjectMapper objectMapper;

    public DoctorController(DoctorService doctorService, ObjectMapper objectMapper) {
        this.doctorService = doctorService;
        this.objectMapper = objectMapper;
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
