package com.booking.domain.models.entities;

import com.booking.domain.MedicalRecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medical_record")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @Column(name = "medical_record_id")
    private String medicalRecordId;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "appointment_id")
    private String appointmentId;

    @Column(name = "medical_history_id")
    private String medicalHistoryId;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MedicalRecordStatus status;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Thêm các trường sau để hỗ trợ quy trình khám
    @ElementCollection
    @CollectionTable(name = "medical_record_examinations", joinColumns = @JoinColumn(name = "medical_record_id"))
    @Column(name = "examination")
    private List<String> examinations;  // Danh sách các xét nghiệm (làm xét nghiệm gì)

    @Column(name = "prescription", columnDefinition = "TEXT")
    private String prescription;  // Đơn thuốc

    @Column(name = "next_appointment")
    private LocalDate nextAppointment;  // Tái khám (nếu có)

    @PrePersist
    public void onCreate() {
        this.medicalRecordId = "mr" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
