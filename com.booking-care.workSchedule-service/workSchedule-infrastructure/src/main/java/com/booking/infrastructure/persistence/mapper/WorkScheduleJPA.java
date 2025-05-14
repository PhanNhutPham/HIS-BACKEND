package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleJPA extends JpaRepository<WorkSchedule, String> {
}
