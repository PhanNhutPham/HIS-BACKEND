package com.booking.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String userId;
    private String username;
    private String email;
    private String roleName;
    private String degree;
    private String password;
    private String departmentId; // optional
    private String roomId;
    private String gender;
    private String phone;// optional
}
