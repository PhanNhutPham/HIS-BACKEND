package com.booking.model.dto.request;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignDoctorEvent implements Serializable {

    private String userId;
    private String departmentId;
    private String action; // ASSIGN, UNASSIGN, etc.
}
