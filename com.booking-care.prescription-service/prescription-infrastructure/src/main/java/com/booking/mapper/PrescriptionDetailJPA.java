package com.booking.mapper;

import com.booking.models.entities.PrescriptionDetail;
import com.booking.models.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionDetailJPA extends JpaRepository<PrescriptionDetail, String> {
    List<PrescriptionDetail> findByMedicineId(String medicineId);

}
