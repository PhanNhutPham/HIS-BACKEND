package com.booking.event;

import com.booking.models.enums.PrescriptionStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDetailCreatedEvent {
    private String prescriptionId;
    private PrescriptionStatus status = PrescriptionStatus.PENDING;
    private List<MedicineUsage> medicines;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MedicineUsage {
        private String medicineId;
        private String medicineName;
        private Integer quantity;
        private String dosage;
        private String frequency;
        private String note;
    }
}
