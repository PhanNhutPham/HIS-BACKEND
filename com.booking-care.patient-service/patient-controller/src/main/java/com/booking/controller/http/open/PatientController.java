package com.booking.controller.http.open;

import com.booking.domian.models.entities.Patient;
import com.booking.model.dto.request.PatientCreate;
import com.booking.model.dto.request.PatientCreateByEvent;
import com.booking.service.KafkaProducerService;
import com.booking.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Patient API", description = "Quản lý thông tin bệnh nhân")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducerService;

    public PatientController(PatientService patientService, ObjectMapper objectMapper, KafkaProducerService kafkaProducerService) {
        this.patientService = patientService;
        this.objectMapper = objectMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "appointment.request.initiated", groupId = "patient-group-v2")
    public void handlePatientCreateByEvent(String message) {
        try {
            System.out.println("Received message from Kafka: " + message);

            // Parse JSON message
            PatientCreateByEvent event = objectMapper.readValue(message, PatientCreateByEvent.class);

            // Kiểm tra nếu userId đã tồn tại
            boolean exists = patientService.existsByUserId(event.getUserId());
            if (exists) {
                System.out.println("Patient đã tồn tại cho userId: " + event.getUserId());
                return;
            }

            // Tạo mới patient
            Patient patient = Patient.builder()
                    .fullName(event.getFullName())
                    .gender(event.getGender())
                    .userId(event.getUserId())
                    .email(event.getEmail())
                    .nationalId(event.getNationalId())
                    .address(event.getAddress())
                    .phoneNumber(event.getPhoneNumber())
                    .dateOfBirth(LocalDate.parse(event.getDateOfBirth()))
                    .build();

            Patient savedPatient = patientService.createPatient(patient);
            System.out.println("Đã tạo patient thành công: " + savedPatient.getPatientId());

            // Gửi event trả ngược về appointment-service
            PatientCreate responseEvent = PatientCreate.builder()
                    .userId(savedPatient.getUserId())
                    .patientId(savedPatient.getPatientId())
                    .build();

            kafkaProducerService.sendPatienCreatedEvent(responseEvent);
            System.out.println("Đã gửi Kafka event patient.created cho userId: " + responseEvent.getUserId());

        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý event tạo patient: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Tạo bệnh nhân mới")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.createPatient(patient));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin bệnh nhân theo ID")
    public ResponseEntity<Patient> getPatient(@PathVariable String id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả bệnh nhân")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin bệnh nhân")
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá bệnh nhân theo ID")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
