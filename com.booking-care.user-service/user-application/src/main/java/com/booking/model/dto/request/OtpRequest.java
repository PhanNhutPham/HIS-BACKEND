package com.booking.model.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OtpRequest {
    // Getters and Setters
    private String email;


    // Constructor
    public OtpRequest(String email) {
        this.email = email;

    }

}
