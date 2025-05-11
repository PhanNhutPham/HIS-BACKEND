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
        this.prescriptionDetailId = "prsD" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }
    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonBackReference
    private Prescription prescription;
    @Column(name = "pres_de_dosage")
    private String dosage;
    @Column(name = "is_out_of_stock")
    private Boolean outOfStock;

    // Getter & Setter
    public Boolean getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(Boolean outOfStock) {
        this.outOfStock = outOfStock;
    }

    @Column(name = "pres_de_frequency")
    private String frequency;

    @Column(name = "pres_de_note")
    private String note;
    @Column(name = "quanlity") // hoặc "quantity" nếu đúng nghĩa
    private Integer quanlity;

    private String medicineId;
    private String medicineName;
}

