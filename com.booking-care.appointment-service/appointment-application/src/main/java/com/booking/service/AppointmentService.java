package com.booking.service;

import com.booking.models.entities.Appointment;
import com.booking.request.AppointmentRequest;

import java.util.List;

public interface AppointmentService {

    // Phương thức tạo lịch hẹn mới từ AppointmentRequest
    Appointment createAppointment(AppointmentRequest appointmentRequest);

    // Phương thức cập nhật lịch hẹn theo ID
    Appointment updateAppointment(Appointment appointment, String id);
    List<Appointment> updateAppointmentsWithPatientId(String userId, String patientId);
    // Phương thức lấy lịch hẹn theo ID
    Appointment getAppointmentById(String id);

    // Phương thức xóa lịch hẹn theo ID
    Appointment deleteAppointment(String id);

    // Phương thức lấy tất cả các lịch hẹn
    List<Appointment> getAllAppointments();

    // Phương thức xóa tất cả lịch hẹn của bệnh nhân theo patientId
    void deleteAllAppointments(String patientId);
}
