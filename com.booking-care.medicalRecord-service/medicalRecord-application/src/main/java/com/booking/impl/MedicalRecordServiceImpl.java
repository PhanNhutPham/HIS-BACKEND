package com.booking.impl;

import com.booking.domain.models.entities.MedicalRecord;
import com.booking.infrastructure.persistence.mapper.MedicalRecordRepository;
import com.booking.service.MedicalRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Override
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        logger.info("Creating new medical record for patient ID: {}", medicalRecord.getPatientId());
        return medicalRecordRepository.save(medicalRecord);
    }

    @Override
    public MedicalRecord updateMedicalRecord(String id, MedicalRecord medicalRecord) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medical record not found"));

        updateFields(existing, medicalRecord);

        logger.info("Updating medical record with ID: {}", id);
        return medicalRecordRepository.save(existing);
    }

    @Override
    public MedicalRecord getMedicalRecordById(String id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medical record not found"));
    }

    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        logger.info("Fetching all medical records");
        return medicalRecordRepository.findAll();
    }

    @Override
    public void deleteMedicalRecord(String id) {
        logger.info("Deleting medical record with ID: {}", id);
        medicalRecordRepository.deleteById(id);
    }

    /**
     * Helper method to update fields of the existing medical record
     */
    private void updateFields(MedicalRecord existing, MedicalRecord medicalRecord) {
        existing.setNote(medicalRecord.getNote());
        existing.setDoctorId(medicalRecord.getDoctorId());
        existing.setDiagnosis(medicalRecord.getDiagnosis());
        existing.setSymptoms(medicalRecord.getSymptoms());
        existing.setTreatmentPlan(medicalRecord.getTreatmentPlan());
        existing.setStatus(medicalRecord.getStatus());
    }
}
