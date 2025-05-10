package com.booking.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineCreateEvent {
    private String username;
    private String email;
    private String roleName;
    private String userId;
}
