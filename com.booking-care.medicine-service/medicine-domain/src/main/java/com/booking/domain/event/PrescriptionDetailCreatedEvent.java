package com.booking.domain.event;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDetailCreatedEvent {
    private String prescriptionId;
    private String status;
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
