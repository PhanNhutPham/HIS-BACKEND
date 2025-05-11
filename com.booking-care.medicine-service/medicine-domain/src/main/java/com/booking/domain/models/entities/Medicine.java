package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medicine")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicine {
    @Id
    @Column(name = "medicine_id")
    private String medicineId;

    @PrePersist
    public void generateId() {
        this.medicineId = "medicine" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private String medicineName;
    private String medicineStatus;
    private String medicineAvatar;
    private String medicinePrice;
    private LocalDateTime create_at;
    private LocalDateTime update_at;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosage_form_id", referencedColumnName = "dosage_form_id")
    @JsonIgnore
    private DosageForm dosageForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicineCategory_id", referencedColumnName = "medicineCategory_id")
    @JsonIgnore
    private MedicineCategory medicineCategory;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MedicineWarehouse> medicineWarehouse;

}


