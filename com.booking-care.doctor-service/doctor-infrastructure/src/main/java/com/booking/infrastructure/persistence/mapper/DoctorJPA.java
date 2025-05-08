package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.Doctor;
import com.booking.domain.repositories.DoctorRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorJPA extends JpaRepository<Doctor, String> {
    Optional<Doctor> findByUserId(String userId);

}
