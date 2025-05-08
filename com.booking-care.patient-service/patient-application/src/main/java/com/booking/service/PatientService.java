package com.booking.service;

import com.booking.domian.models.entities.Patient;

import java.util.List;

public interface PatientService {
    Patient createPatient(Patient patient);
    Patient getPatient(String id);
    List<Patient> getAllPatients();
    Patient updatePatient(String id, Patient updatedPatient);
    void deletePatient(String id);
    boolean existsByUserId(String userId);
}
