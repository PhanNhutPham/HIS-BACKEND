package com.booking.mapper;

import com.booking.models.entities.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionDetailJPA extends JpaRepository<PrescriptionDetail, String> {
}
