package com.booking.models.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prescriptionDetail")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionDetail {
    @Id
    private String prescriptionDetailId;
    @PrePersist
    public void generateId() {
        this.prescriptionDetailId = "prsD" + UUID.randomUUID().toString().replace("-", "");
    }
    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonBackReference
    private Prescription prescription;
    @Column(name = "pres_de_dosage")
    private String dosage;

    @Column(name = "pres_de_frequency")
    private String frequency;

    @Column(name = "pres_de_note")
    private String note;
    @Column(name = "quanlity") // hoặc "quantity" nếu đúng nghĩa
    private Integer quanlity;

    private String medicineId;
    private String medicineName;
}

