package com.booking.controller.http;

import com.booking.model.dto.request.AssignDoctorEvent;
import com.booking.model.dto.request.DoctorCreateEvent;
import com.booking.service.DoctorService;
import com.booking.domain.models.entities.Doctor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctor API", description = "Quản lý thông tin bác sĩ")
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final ObjectMapper objectMapper;
    public DoctorController(DoctorService doctorService, ObjectMapper objectMapper) {
        this.doctorService = doctorService;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "assign-doctor-department-topic", groupId = "doctor-service-group-v2")
    public void handleDoctorAssignment(String message) {
        try {
            AssignDoctorEvent event = objectMapper.readValue(message, AssignDoctorEvent.class);

            System.out.println("Received event: Action = " + event.getAction() +
                    ", USER ID = " + event.getUserId() +
                    ", Department ID = " + event.getDepartmentId());

            doctorService.assignDoctorToDepartment(event.getUserId(), event.getDepartmentId(), event.getAction());
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
                    .gender(event.getGender())
                    .build();

            doctorService.createDoctor(doctor);
            System.out.println("Tạo bác sĩ tự động thành công: " + doctor.getFullName());
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý event tạo bác sĩ: " + e.getMessage());
        }
    }
    @PostMapping
    @Operation(summary = "Tạo bác sĩ mới")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.createDoctor(doctor));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Lấy thông tin bác sĩ theo User ID")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(doctorService.getDoctorByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả bác sĩ")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin bác sĩ")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá bác sĩ theo ID")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctorByUserId(id);
        return ResponseEntity.noContent().build();
    }
    // dùng để map JSON thành object


}
