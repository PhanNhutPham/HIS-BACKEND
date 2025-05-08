package com.booking.impl;

import com.booking.domain.models.entities.Doctor;
import com.booking.service.DoctorService;
import com.booking.infrastructure.persistence.mapper.DoctorJPA;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorJPA doctorRepository;

    public DoctorServiceImpl(DoctorJPA doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Doctor createDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        return doctorRepository.save(doctor);
    }


    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor updateDoctor(String id, Doctor updatedDoctor) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be null or empty");
        }

        if (updatedDoctor == null) {
            throw new IllegalArgumentException("Updated doctor information cannot be null");
        }

        Doctor doctor = getDoctorByUserId(id);
        doctor.setFullName(updatedDoctor.getFullName());
        doctor.setEmail(updatedDoctor.getEmail());
        doctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        doctor.setGender(updatedDoctor.getGender());
        doctor.setDegree(updatedDoctor.getDegree());
        doctor.setDepartment_id(updatedDoctor.getDepartment_id());

        return doctorRepository.save(doctor);
    }




    @Override
    public Doctor getDoctorByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with user ID: " + userId));
    }


    @Override
    public void deleteDoctorByUserId(String userId) {
        Doctor doctor = getDoctorByUserId(userId);
        doctorRepository.delete(doctor);
    }

    @Override
    public Doctor assignDoctorToDepartment(String userId, String departmentId, String action) {
        Doctor doctor = getDoctorByUserId(userId); // ✅ Fix: dùng findByUserId

        switch (action.toUpperCase()) {
            case "ASSIGN":
                if (departmentId == null || departmentId.isEmpty()) {
                    throw new IllegalArgumentException("Department ID cannot be null or empty for ASSIGN action.");
                }
                if (departmentId.equals(doctor.getDepartment_id())) {
                    System.out.println("Doctor already assigned to this department. Skipping update.");
                    return doctor;
                }
                doctor.setDepartment_id(departmentId);
                break;

            case "UNASSIGN":
                if (doctor.getDepartment_id() == null) {
                    System.out.println("Doctor is already unassigned. Skipping update.");
                    return doctor;
                }
                doctor.setDepartment_id(null);
                break;

            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        return doctorRepository.save(doctor);
    }


}
