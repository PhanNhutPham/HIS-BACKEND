package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {
    MedicalRecord findByAppointmentId(String appointmentId);
}
