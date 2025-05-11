package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.DosageForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DosageFormJPA extends JpaRepository<DosageForm, String> {
}
