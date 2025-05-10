package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineJPA extends JpaRepository<Medicine, String> {
}
