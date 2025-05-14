package com.booking.domain.models.entities;


import com.booking.domain.models.enums.WorkScheduleStatus;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "work_schedule")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSchedule {

    @Id
    private String wSchedule_id;

    @PrePersist
    public void generateId() {
        this.wSchedule_id = "wSchedule" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private String doctorId;      // ID của người dùng (bác sĩ, lễ tân...)
//    private String userRole;    // DOCTOR, RECEPTIONIST, ...

    @Enumerated(EnumType.STRING)
    private WorkScheduleStatus wScheduleStatus;

    private String wScheduleHours;
    private String createdByStaffId;

    private LocalDate endTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rSchedule_id", referencedColumnName = "rSchedule_id")
    @JsonManagedReference
    private RegularSchedule regularSchedule;


    @OneToMany(mappedBy = "workSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exception> exceptions = new ArrayList<>();

}
