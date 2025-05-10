package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.Medicine;
import com.booking.domain.models.entities.MedicineWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineWarehouseJPA extends JpaRepository<MedicineWarehouse, String> {
    MedicineWarehouse findByMedicine_MedicineId(String medicineId);
}
