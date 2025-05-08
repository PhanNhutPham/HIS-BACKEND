package com.booking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequest {
    @NotBlank(message = "Full name is required")
    private String patientFullName;

    private String patientAddress;

    @NotBlank(message = "Email is required")
    private String patientEmail;

    private String patientCard;

    private String patientPhoneNumber;

    private String patientGender;

    private Date patientDOB;

    private String patient_id;
}
