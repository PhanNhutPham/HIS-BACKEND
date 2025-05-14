package com.booking.infrastructure.event;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppointmentConfirmed {

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate appointmentTime;
    private String status; // Ví dụ: "CONFIRMED"
}
