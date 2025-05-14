package com.booking.domain.repositories;

import com.booking.domain.models.entities.WorkSchedule;

import java.util.List;
import java.util.Optional;

public interface WorkScheduleRepository {
    WorkSchedule save(WorkSchedule workSchedule);
    Optional<WorkSchedule> findById(String id);
    List<WorkSchedule> findAll();
    void deleteById(String id);
}
