package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medicineCategory")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicineCategory {
    @Id
    @Column(name = "medicineCategory_id")
    private String medicineCategoryId;

    @PrePersist
    public void generateId() {
        this.medicineCategoryId = "medicineCategory" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private String medicineCategoryName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "medicineCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Medicine> medicines = new ArrayList<>();

}
