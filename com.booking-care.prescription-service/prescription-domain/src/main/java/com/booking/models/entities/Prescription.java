package com.booking.models.entities;
import com.booking.models.enums.PrescriptionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescription")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prescription {
    @Id
    private String prescriptionId;
    @PrePersist
    public void generateId() {
        this.prescriptionId = "prs" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PrescriptionDetail> prescriptionDetails = new ArrayList<>();
    private String paymentStatus;
    private String doctorId;
    private String patientId;
    private String presDiagnosis;

    @Enumerated(EnumType.STRING)
    @Column(name = "prescription_status")
    private PrescriptionStatus status = PrescriptionStatus.PENDING;


    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date presDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date presExpiryDate;

}
