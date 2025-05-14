package com.booking.http;

import com.booking.impl.AppointmentServiceImpl;
import com.booking.models.entities.Appointment;
import com.booking.models.enums.AppointmentStatus;
import com.booking.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class ProtocolConfirmedController {

    private final AppointmentServiceImpl appointmentService;

    public ProtocolConfirmedController(AppointmentServiceImpl appointmentService) {
        this.appointmentService = appointmentService;
    }


    // API để xác nhận cuộc hẹn
    // Confirm
    @PutMapping("/{appointmentId}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public Appointment confirmAppointment(@PathVariable("appointmentId") String appointmentId) {
        return appointmentService.confirmAppointment(appointmentId);
    }

    // Reject
    @PutMapping("/{appointmentId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public Appointment rejectAppointment(@PathVariable("appointmentId") String appointmentId) {
        return appointmentService.rejectAppointment(appointmentId);
    }
}
