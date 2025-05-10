package com.booking.domain.repositories;

import com.booking.domain.models.entities.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository {
    Medicine save(Medicine medicine);
    Optional<Medicine> findById(String id);
    List<Medicine> findAll();
    void deleteById(String id);
}
