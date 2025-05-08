package com.booking.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCreateByEvent {
    private String userId;
    private String fullName;
    private String gender;
    private String address;
    private String email;
    private String nationalId;
    private String phoneNumber;
    private String dateOfBirth;
}
