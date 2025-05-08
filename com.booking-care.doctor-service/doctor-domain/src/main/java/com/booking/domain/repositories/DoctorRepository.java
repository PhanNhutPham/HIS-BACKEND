package com.booking.domain.repositories;

import com.booking.domain.models.entities.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(Long id);
    List<Doctor> findAll();
    void deleteById(Long id);
}
