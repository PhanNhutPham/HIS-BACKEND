package com.booking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionApprovedEvent {
    private String prescriptionId;
    private String approvalStatus; // Hoặc có thể dùng Enum cho trạng thái approval
}
