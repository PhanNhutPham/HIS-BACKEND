package com.booking.kafka;

import com.booking.domain.MedicalRecordStatus;
import com.booking.domain.models.entities.MedicalRecord;
import com.booking.infrastructure.event.AppointmentConfirmed;
import com.booking.infrastructure.persistence.mapper.MedicalRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final MedicalRecordRepository medicalRecordRepository;

    @KafkaListener(topics = "appointment.confirm", groupId = "medicalRecord-group")
    @Transactional
    public void onAppointmentConfirmed(AppointmentConfirmed appointmentConfirmed) {
        MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setAppointmentId(appointmentConfirmed.getAppointmentId());
            medicalRecord.setStatus(MedicalRecordStatus.OPEN);
            medicalRecord.setDoctorId(appointmentConfirmed.getDoctorId());
            medicalRecord.setPatientId(appointmentConfirmed.getPatientId());
            medicalRecord.setVisitDate(appointmentConfirmed.getAppointmentTime());
            // Chuyển đổi appointmentTime thành LocalDate

                medicalRecordRepository.save(medicalRecord);
                System.out.println("✅ Medical record updated for appointmentId: " );
    }

}

