package com.booking.request;

import com.booking.models.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin yêu cầu đặt lịch hẹn")
public class AppointmentRequest {

    private String userId;
    private String fullName;
    private  String patientId;
    private String phoneNumber;
    private String gender;
    private String address;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String email;
    private String appointmentNotes;
    private LocalDateTime appointmentDate;
    private String departmentId;
    private String doctorId;
    private String reason;
    private String symptoms;
}
