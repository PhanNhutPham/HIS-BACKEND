package com.booking.domain.models.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedEvent {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String roleName;
    private String departmentId; // optional
    private String roomId;
    private String gender;
    private String phone;
    private String degree;
}
