package com.booking.impl;

import com.booking.config.kafka.KafkaProducerConfig;
import com.booking.event.AppointmentConfirmed;
import com.booking.event.AppointmentRequestInitiated;
import com.booking.mapper.AppointmentJPA;
import com.booking.models.entities.Appointment;
import com.booking.models.enums.AppointmentStatus;
import com.booking.request.AppointmentRequest;
import com.booking.service.AppointmentService;
import com.booking.service.kafka.KafkaProducerService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentJPA appointmentRepository;
    private final KafkaProducerService kafkaProducerService;

    public AppointmentServiceImpl(AppointmentJPA appointmentRepository, KafkaProducerService kafkaProducerService) {
        this.appointmentRepository = appointmentRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public Appointment createAppointment(AppointmentRequest request) {
        // Tạo sự kiện AppointmentRequestInitiated để gửi sang Kafka
        AppointmentRequestInitiated event = AppointmentRequestInitiated.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .address(request.getAddress())
                .nationalId(request.getNationalId())
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth().toString())  // Giữ nguyên kiểu LocalDate
                .build();

        // Gửi sự kiện qua KafkaProducerService
        kafkaProducerService.sendUserCreatedEvent(event);

        // Tạo đối tượng Appointment từ dữ liệu yêu cầu
        Appointment appointment = Appointment.builder()
                .userId(request.getUserId())
                .doctorId(request.getDoctorId())
                .departmentId(request.getDepartmentId())
                .appointmentDate(request.getAppointmentDate())
                .reason(request.getReason())
                .symptoms(request.getSymptoms())
                .status(AppointmentStatus.SCHEDULED) // Mặc định khi tạo mới
                .appointmentNotes(request.getAppointmentNotes())
                .createdAt(LocalDateTime.now())  // Thêm thời gian tạo
                .updatedAt(LocalDateTime.now())  // Thêm thời gian cập nhật
                .build();

        // Lưu lịch hẹn mới vào cơ sở dữ liệu
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment updateAppointment(Appointment updatedAppointment, String id) {
        Appointment appointment = getAppointmentById(id);

        appointment.setUserId(updatedAppointment.getUserId());
        appointment.setPatientId(updatedAppointment.getPatientId());
        appointment.setDoctorId(updatedAppointment.getDoctorId());
        appointment.setDepartmentId(updatedAppointment.getDepartmentId());
        appointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        appointment.setReason(updatedAppointment.getReason());
        appointment.setSymptoms(updatedAppointment.getSymptoms());
        appointment.setStatus(updatedAppointment.getStatus());
        appointment.setAppointmentNotes(updatedAppointment.getAppointmentNotes());
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> updateAppointmentsWithPatientId(String userId, String patientId) {
        List<Appointment> appointments = appointmentRepository.findByUserIdAndPatientIdIsNull(userId);
        for (Appointment a : appointments) {
            a.setPatientId(patientId);
            a.setUpdatedAt(LocalDateTime.now());
        }
        return appointmentRepository.saveAll(appointments);
    }

    @Override
    public Appointment getAppointmentById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    @Override
    public Appointment deleteAppointment(String id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.deleteById(id);
        return appointment;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public void deleteAllAppointments(String patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        appointmentRepository.deleteAll(appointments);
    }

    // Phương thức xác nhận lịch hẹn
    @Override
    public Appointment confirmAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CONFIRMED);  // Cập nhật trạng thái thành CONFIRMED
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentConfirmed event = new AppointmentConfirmed (
                savedAppointment.getAppointmentId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getStatus().name()
        );
        kafkaProducerService.sendAppointmentConfirmEvent(event);
        return appointmentRepository.save(savedAppointment);
    }

    @Override
    public Appointment rejectAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED); // Thay đổi trạng thái thành CANCELLED
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }
}
