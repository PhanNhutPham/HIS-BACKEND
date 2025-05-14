package com.booking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentConfirmed {

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate appointmentTime;
    private String status; // Ví dụ: "CONFIRMED"
}
