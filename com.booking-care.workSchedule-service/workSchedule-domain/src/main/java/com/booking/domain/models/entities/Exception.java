package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exception")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Exception {

    @Id
    private String ecpt_id;

    @PrePersist
    public void generateId() {
        this.ecpt_id = "ecpt" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private String ecptStatus;
    private String ecptNote;
    private String ecptReason;
    private LocalDate ecptTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wSchedule_id", referencedColumnName = "wSchedule_id")
    @JsonBackReference
    private WorkSchedule workSchedule;
}
