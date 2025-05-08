package com.booking.service;

import com.booking.domain.models.entities.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor createDoctor(Doctor doctor);
    Doctor getDoctorByUserId(String userId);              // Thay getDoctor(id) -> getDoctorByUserId(userId)
    List<Doctor> getAllDoctors();
    Doctor updateDoctor(String userId, Doctor updatedDoctor); // Tham chiếu theo userId
    void deleteDoctorByUserId(String userId);             // Thay deleteDoctor(id)
    Doctor assignDoctorToDepartment(String userId, String departmentId, String action); // Thay doctorId -> userId
}
