package com.booking.controller.http.admin;

import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.infrastructure.kafka.event.CreateWorkScheduleEvent;
import com.booking.infrastructure.persistence.mapper.DoctorJPA;
import com.booking.model.dto.request.AssignDoctorEvent;
import com.booking.model.dto.request.DoctorCreateEvent;
import com.booking.model.dto.response.DoctorResponse;
import com.booking.model.dto.response.ResponseObject;
import com.booking.service.DoctorService;
import com.booking.domain.models.entities.Doctor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Doctor API", description = "Quản lý thông tin bác sĩ")
@RestController
@RequestMapping("/admin/doctors")
public class DoctorAdminController {
    private final DoctorJPA doctorRepository;
    private final DoctorService doctorService;
    private final ObjectMapper objectMapper;
    public DoctorAdminController(DoctorJPA doctorRepository, DoctorService doctorService, ObjectMapper objectMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "assign-doctor-department-topic", groupId = "doctor-service-group-v2")
    public void handleDoctorAssignment(String message) {
        try {
            AssignDoctorEvent event = objectMapper.readValue(message, AssignDoctorEvent.class);

            System.out.println("Received event: Action = " + event.getAction() +
                    ", Doctor ID = " + event.getDoctorId() +
                    ", Department ID = " + event.getDepartmentId());

            doctorService.assignDoctorToDepartment(event.getDoctorId(), event.getDepartmentId(), event.getAction());
        } catch (Exception e) {
            System.err.println("Lỗi khi deserialize AssignDoctorEvent: " + e.getMessage());
        }
    }



    @KafkaListener(topics = "user-created-topic", groupId = "doctor-service-group-v2")
    public void handleDoctorCreatedEvent(String message) {
        try {
            System.out.println("Received message: " + message);  // Log message nhận được
            DoctorCreateEvent event = objectMapper.readValue(message, DoctorCreateEvent.class);
            // Kiểm tra roleName trước khi tạo
            if (!"DOCTOR".equalsIgnoreCase(event.getRoleName())) {
                System.out.println("Không phải role DOCTOR, bỏ qua event");
                return;
            }
            Doctor doctor = Doctor.builder()
                    .fullName(event.getUsername())  // map username -> fullName
                    .email(event.getEmail())
                    .userId(event.getUserId())
                    .department_id(event.getDepartmentId())
                    .room_Id(event.getRoomId())
                    .phoneNumber(event.getPhone())
                    .degree(event.getDegree())
                    .gender(event.getGender())
                    .build();

            doctorService.createDoctor(doctor);
            System.out.println("Tạo bác sĩ tự động thành công: " + doctor.getFullName());
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý event tạo bác sĩ: " + e.getMessage());
        }
    }


//    @GetMapping("/{id}")
//    @Operation(summary = "Lấy thông tin bác sĩ theo ID")
//    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable("id") String id) {
//        return ResponseEntity.ok(doctorService.getDoctor(id));
//    }
@PostMapping("/{doctorId}/work-schedule")
public ResponseEntity<String> createWorkSchedule(
        @PathVariable("doctorId") String doctorId,
        @RequestBody CreateWorkScheduleEvent request) {
    try {
        doctorService.createWorkSchedule(doctorId, request);
        return ResponseEntity.ok("Work schedule event created successfully for doctor ID: " + doctorId);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Failed to create work schedule: " + e.getMessage());
    }

}
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả bác sĩ")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PutMapping("/upload/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable("id") String id, @RequestBody DoctorResponse updatedDoctor) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, updatedDoctor));
    }

    @PostMapping("/uploads/{doctorId}")
    public ResponseEntity<ResponseObject> uploadAvatarDoctor(
            @PathVariable("doctorId") String doctorId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            doctorService.uploadAvatar(doctorId, file);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload doctor avatar successfully")
                    .status(HttpStatus.CREATED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading doctor avatar: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @GetMapping("/avatar/{doctorId}")
    public ResponseEntity<?> getDoctorAvatarById(@PathVariable("doctorId") String doctorId) {
        try {
            // Lấy thông tin bác sĩ từ doctorId
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new DataNotFoundException(ResultCode.DATA_NOT_FOUND));

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
    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá bác sĩ theo ID")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    // dùng để map JSON thành object
//    @PostMapping("/uploads/doctor/{doctorId}")
//    public ResponseEntity<ResponseObject> uploadAvatarDoctor(
//            @PathVariable("doctorId") String doctorId,
//            @RequestParam("file") MultipartFile file
//    ) {
//        try {
//            doctorService.uploadAvatar(doctorId, file);
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .message("Upload doctor avatar successfully")
//                    .status(HttpStatus.CREATED)
//                    .data(null)
//                    .build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ResponseObject.builder()
//                            .message("Error uploading doctor avatar: " + e.getMessage())
//                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .build());
//        }
//    }


}
