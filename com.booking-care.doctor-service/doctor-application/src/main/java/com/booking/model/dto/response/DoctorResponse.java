package com.booking.model.dto.response;

import com.booking.domain.models.entities.Doctor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@Data
public class DoctorResponse {
    private String doctorId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String degree;
    private String avatarDoctor;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String departmentId;

    public DoctorResponse(Doctor doctor) {
        this.doctorId = doctor.getDoctorId();
        this.fullName = doctor.getFullName();
        this.email = doctor.getEmail();
        this.phoneNumber = doctor.getPhoneNumber();
        this.gender = doctor.getGender();
        this.degree = doctor.getDegree();
        this.avatarDoctor = doctor.getAvatarDoctor();
        this.createTime = doctor.getCreateTime();
        this.updateTime = doctor.getUpdateTime();
        this.departmentId = doctor.getDepartment_id();
    }

}
