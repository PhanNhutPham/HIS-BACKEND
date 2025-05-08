package com.booking.models.entities;

import com.booking.models.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {

    @Id
    private String appointmentId; // ID của cuộc hẹn

    private String userId; // ID người dùng từ user-service
    private String patientId; // ID bệnh nhân từ patient-service
    private String doctorId; // ID bác sĩ

    private String departmentId; // ID khoa/phòng khám

    private LocalDateTime appointmentDate; // Ngày giờ cuộc hẹn
    private String reason; // Lý do khám
    private String symptoms; // Triệu chứng bệnh nhân gặp phải

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; // Trạng thái cuộc hẹn: "Scheduled", "Completed", "Cancelled"

    private String appointmentNotes; // Ghi chú (bác sĩ, bệnh nhân)

    private LocalDateTime createdAt; // Thời gian tạo
    private LocalDateTime updatedAt; // Thời gian cập nhật

    // Hàm này được gọi khi cuộc hẹn được tạo mới hoặc cập nhật
    @PrePersist
    public void generateId() {
        if (this.appointmentId == null) {
            this.appointmentId = "appointment" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        }
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }
}
