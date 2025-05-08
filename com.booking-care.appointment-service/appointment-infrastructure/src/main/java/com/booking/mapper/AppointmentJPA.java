package com.booking.mapper;

import com.booking.models.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentJPA extends JpaRepository<Appointment, String> {
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByUserIdAndPatientIdIsNull(String userId);

}
