package com.booking.domian.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {

    @Id
    private String patientId; // camelCase cho biến

    private String userId; // ID từ user-service

    private String fullName;   // Tên đầy đủ bệnh nhân
    private String gender;     // MALE, FEMALE, OTHER
    private LocalDate dateOfBirth; // Ngày sinh

    private String phoneNumber; // Số điện thoại
    private String email;       // Email
    private String address;     // Địa chỉ

    private String nationalId;      // CCCD/CMND
    private String insuranceNumber; // Số bảo hiểm y tế (nếu có)

    private LocalDateTime createTime; // Thời gian tạo
    private LocalDateTime updateTime; // Thời gian cập nhật

    // Hàm này sẽ được gọi mỗi khi một bản ghi được lưu vào cơ sở dữ liệu
    @PrePersist
    public void generateId() {
        if (this.patientId == null) {
            this.patientId = "patient" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        }
        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = now;
        }
    }
}
