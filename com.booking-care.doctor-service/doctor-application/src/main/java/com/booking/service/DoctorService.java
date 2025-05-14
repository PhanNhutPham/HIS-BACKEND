package com.booking.service;

import com.booking.domain.models.entities.Doctor;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.InvalidPasswordException;
import com.booking.infrastructure.kafka.event.CreateWorkScheduleEvent;
import com.booking.model.dto.request.ResetPasswordRequest;
import com.booking.model.dto.response.DoctorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DoctorService {
    Doctor createDoctor(Doctor doctor);
    List<DoctorResponse> getAllDoctors();
    DoctorResponse updateDoctor(String id, DoctorResponse updatedDoctor);
    void deleteDoctor(String id);

    void uploadAvatar(String doctorId, MultipartFile file) throws Exception;

    // Thêm method mới để gán khoa cho bác sĩ
    Doctor assignDoctorToDepartment(String doctorId, String departmentId, String action);

    Doctor getDoctorByUserId(String userId);

    DoctorResponse updateDoctorByUserId(String userId, DoctorResponse updatedDoctor);
    void createWorkSchedule(String doctorId, CreateWorkScheduleEvent request);
    void uploadAvatarByUserId(String userId, MultipartFile file) throws Exception;
}
