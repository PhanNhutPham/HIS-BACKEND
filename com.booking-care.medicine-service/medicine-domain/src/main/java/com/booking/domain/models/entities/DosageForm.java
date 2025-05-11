package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dosage_form")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageForm {

    @Id
    @Column(name = "dosage_form_id")
    private String dosageFormId;

    @PrePersist
    public void generateId() {
        this.dosageFormId = "dosageForm" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    @Column(name = "dosage_form_name", nullable = false)
    private String dosageFormName;  // ví dụ: Viên nén, Chai, Gói...

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "dosageForm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Medicine> medicines = new ArrayList<>();

}