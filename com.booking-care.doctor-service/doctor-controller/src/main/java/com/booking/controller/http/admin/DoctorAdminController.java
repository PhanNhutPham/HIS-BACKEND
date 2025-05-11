package com.booking.controller.http.admin;

import com.booking.model.dto.request.AssignDoctorEvent;
import com.booking.model.dto.request.DoctorCreateEvent;
import com.booking.model.dto.response.DoctorResponse;
import com.booking.model.dto.response.ResponseObject;
import com.booking.service.DoctorService;
import com.booking.domain.models.entities.Doctor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Doctor API", description = "Quản lý thông tin bác sĩ")
@RestController
@RequestMapping("/admin/doctors")
public class DoctorAdminController {

    private final DoctorService doctorService;
    private final ObjectMapper objectMapper;
    public DoctorAdminController(DoctorService doctorService, ObjectMapper objectMapper) {
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

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả bác sĩ")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PutMapping("/upload/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable("id") String id, @RequestBody DoctorResponse updatedDoctor) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, updatedDoctor));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá bác sĩ theo ID")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    // dùng để map JSON thành object
    @PostMapping("/uploads/doctor/{doctorId}")
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


}
