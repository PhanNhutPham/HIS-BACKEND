package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.Medicine;
import com.booking.domain.models.entities.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineCategoryJPA extends JpaRepository<MedicineCategory, String> {

}
