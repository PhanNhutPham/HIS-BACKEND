package com.booking.infrastructure.persistence.mapper;

import com.booking.domian.models.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJPA extends JpaRepository<Patient, Long> {
    boolean existsByUserId(String userId);
}
