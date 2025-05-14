package com.booking.model.dto.response;

import com.booking.domain.models.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
@Data
public class UserResponse {
    private String userId;
    private String profileImage;
    private String username;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDateTime create_at;
    private String address;
    private LocalDateTime update_at;


}