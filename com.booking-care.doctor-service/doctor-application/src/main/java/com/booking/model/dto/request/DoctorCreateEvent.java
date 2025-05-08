package com.booking.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorCreateEvent {
    private String username;
    private String email;
    private String roleName;
    private String userId;
    private String departmentId;
    private String roomId;
    private String gender;
    private String phone;
}
