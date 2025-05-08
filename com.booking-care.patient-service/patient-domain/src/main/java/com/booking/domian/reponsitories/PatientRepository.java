package com.booking.domian.reponsitories;

import com.booking.domian.models.entities.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(Long id);
    List<Patient> findAll();
    void deleteById(Long id);
}
