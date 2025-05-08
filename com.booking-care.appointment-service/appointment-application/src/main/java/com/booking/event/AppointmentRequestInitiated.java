package com.booking.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder  // <--- Thêm dòng này để Lombok tự tạo builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestInitiated {
    private String userId;
    private String fullName;
    private String gender;
    private String address;
    private String nationalId;
    private  String email;
    private String phoneNumber;
    private String dateOfBirth;
}
