package com.booking.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCreatedEvent {
    private String userId;
    private String patientId;
}
