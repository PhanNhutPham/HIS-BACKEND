package com.booking.domain.repositories;

import com.booking.domain.models.entities.MedicineWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineWarehouseRepository extends JpaRepository<MedicineWarehouse, String> {
}
