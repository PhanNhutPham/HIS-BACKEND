package com.booking.domain.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {

    @Id
    @Column(name = "doctor_id")
    private String doctorId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String degree;

    private String avatarDoctor;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String department_id;

    private String userId;

    private String room_Id;

    @PrePersist
    public void generateId() {
        this.doctorId = "doctor" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        this.createTime = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updateTime = LocalDateTime.now();
    }

    public String getDoctor_id() {
        return doctorId;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctorId = doctor_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String room_id) {
        this.department_id = room_id;
    }
}
