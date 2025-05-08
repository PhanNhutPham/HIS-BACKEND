package com.booking.impl;

import com.booking.domian.models.entities.Patient;
import com.booking.infrastructure.persistence.mapper.PatientJPA;
import com.booking.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientJPA patientRepository;

    public PatientServiceImpl(PatientJPA patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Patient getPatient(String id) {
        return patientRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient updatePatient(String id, Patient updatedPatient) {
        Patient patient = getPatient(id);
        patient.setFullName(updatedPatient.getFullName());
        patient.setAddress(updatedPatient.getAddress());
        patient.setEmail(updatedPatient.getEmail());
        patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        patient.setNationalId(updatedPatient.getNationalId());
        patient.setGender(updatedPatient.getGender());
        patient.setDateOfBirth(updatedPatient.getDateOfBirth());
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(String id) {
        patientRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public boolean existsByUserId(String userId) {
        return patientRepository.existsByUserId(userId);
    }
}