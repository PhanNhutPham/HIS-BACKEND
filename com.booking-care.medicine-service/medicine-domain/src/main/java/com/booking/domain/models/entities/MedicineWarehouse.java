package com.booking.domain.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "medicineWarehouse")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicineWarehouse {
    @Id
    @Column(name = "medicineWarehouse_id")
    private String medicineWarehouseId;

    @PrePersist
    public void generateId() {
        this.medicineWarehouseId = "medicineWarehouse" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private  Integer medicineQuantity;

    private LocalDateTime medicineExpirationDay;
    private  LocalDateTime dayOfEntry;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
}
