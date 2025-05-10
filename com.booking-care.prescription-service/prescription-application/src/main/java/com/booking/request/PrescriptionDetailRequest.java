package com.booking.request;
import com.booking.models.entities.PrescriptionDetail;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDetailRequest {
    private String medicineName;
    private String dosage;
    private Integer quantity;
    private String medicineId;
    private String prescriptionId;
    private String presDe_frequency;
    private String presDe_note;

}
