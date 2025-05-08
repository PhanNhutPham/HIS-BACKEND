package com.booking.http;

import com.booking.event.AppointmentRequestInitiated;
import com.booking.models.entities.Appointment;

import com.booking.models.enums.AppointmentStatus;
import com.booking.request.AppointmentRequest;
import com.booking.request.PatientCreatedEvent;
import com.booking.service.AppointmentService;
import com.booking.service.kafka.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointment API", description = "Quản lý lịch hẹn khám bệnh")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    public AppointmentController(AppointmentService appointmentService, KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        this.appointmentService = appointmentService;
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "patient.created", groupId = "appointment-group")
    public void handlePatientCreatedEvent(String message) {
        try {
            PatientCreatedEvent event = objectMapper.readValue(message, PatientCreatedEvent.class);
            System.out.println("Received PatientCreatedEvent: " + event);

            // Cập nhật tất cả appointment của userId chưa có patientId
            appointmentService.updateAppointmentsWithPatientId(event.getUserId(), event.getPatientId());

        } catch (Exception e) {
            System.err.println("Error processing PatientCreatedEvent: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Tạo lịch hẹn mới")
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequest request) {

        // Tạo đối tượng Appointment từ AppointmentRequest


        // Gửi sự kiện AppointmentRequestInitiated qua Kafka
        AppointmentRequestInitiated event = AppointmentRequestInitiated.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .gender(request.getGender())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(String.valueOf(request.getDateOfBirth()))
                .build();

        kafkaProducerService.sendUserCreatedEvent(event); // Gửi sự kiện qua Kafka
        Appointment savedAppointment = appointmentService.createAppointment(request);
        return ResponseEntity.ok(savedAppointment);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy lịch hẹn theo ID")
    public ResponseEntity<Appointment> getAppointmentById(
            @Parameter(description = "ID của lịch hẹn", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả lịch hẹn")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật lịch hẹn")
    public ResponseEntity<Appointment> updateAppointment(
            @Parameter(description = "ID của lịch hẹn cần cập nhật", required = true)
            @PathVariable String id,
            @RequestBody AppointmentRequest request) {

        Appointment updated = Appointment.builder()
                .userId(request.getUserId())
               // .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .departmentId(request.getDepartmentId())
                .appointmentDate(request.getAppointmentDate())
                .reason(request.getReason())
                .symptoms(request.getSymptoms())
                //.status(request.getStatus())
                .appointmentNotes(request.getAppointmentNotes())
                .build();

        return ResponseEntity.ok(appointmentService.updateAppointment(updated, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá lịch hẹn theo ID")
    public ResponseEntity<Appointment> deleteAppointment(
            @Parameter(description = "ID của lịch hẹn cần xoá", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(appointmentService.deleteAppointment(id));
    }

    @DeleteMapping("/patient/{patientId}")
    @Operation(summary = "Xoá tất cả lịch hẹn của bệnh nhân theo ID")
    public ResponseEntity<Void> deleteAppointmentsByPatientId(
            @Parameter(description = "ID của bệnh nhân", required = true)
            @PathVariable String patientId) {
        appointmentService.deleteAllAppointments(patientId);
        return ResponseEntity.noContent().build();
    }
}
