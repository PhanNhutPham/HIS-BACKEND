package com.booking.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignDoctorEvent {
    private String userId;
    private String departmentId;
    private String action; // "ASSIGN" or "UNASSIGN"
}
