package com.booking.mapper;

import com.booking.models.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionJPA extends JpaRepository<Prescription, String> {
}
