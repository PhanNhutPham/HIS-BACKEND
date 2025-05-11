package com.booking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "FullName cannot be blank")
    @Size(min = 4, max = 15, message = "FullName must be between 4 and 15 characters")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Gender cannot be blank")
    @Size(min = 2, max = 3, message = "Gender must be between 2 and 3 characters")
    private  String gender;

    private  String degree;

    private String doctor_id;
}
