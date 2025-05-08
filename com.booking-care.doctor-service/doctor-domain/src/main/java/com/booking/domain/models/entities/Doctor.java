package com.booking.domain.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
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
    private String doctor_id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String degree;

    private Date createTime;

    private Date updateTime;

    private String department_id;
    private String room_Id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    @PrePersist
    public void generateId() {
        this.doctor_id = "doctor" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        this.createTime = new Date();
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updateTime = new Date();
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String room_id) {
        this.department_id = room_id;
    }
}
