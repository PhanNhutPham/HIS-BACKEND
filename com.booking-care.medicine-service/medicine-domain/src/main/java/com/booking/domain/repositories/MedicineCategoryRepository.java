package com.booking.domain.repositories;

import com.booking.domain.models.entities.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, String> {
}
