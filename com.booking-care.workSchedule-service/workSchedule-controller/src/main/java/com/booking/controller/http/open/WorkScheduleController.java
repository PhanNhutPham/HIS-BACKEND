package com.booking.controller.http.open;

import com.booking.domain.models.entities.RegularSchedule;
import com.booking.domain.models.entities.WorkSchedule;
import com.booking.domain.models.entities.Exception;
import com.booking.domain.models.enums.WorkScheduleStatus;
import com.booking.impl.WorkScheduleServiceImpl;
import com.booking.infrastructure.kafka.event.CreateWorkScheduleEvent;
import com.booking.infrastructure.kafka.event.ExceptionDTO;
import com.booking.model.dto.request.ExceptionRequest;
import com.booking.model.dto.request.WorkScheduleRequest;
import com.booking.service.WorkScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-schedules")

public class WorkScheduleController {
    private final WorkScheduleServiceImpl workScheduleService;

    public WorkScheduleController(WorkScheduleServiceImpl workScheduleService) {
        this.workScheduleService = workScheduleService;
    }
    @KafkaListener(topics = "doctor.schedule.created", groupId = "workSchedule-service-group-v2")
    public void listenToWorkScheduleEvent(CreateWorkScheduleEvent event) {
        // Log để kiểm tra sự kiện nhận được từ Kafka
        System.out.println("Received event: " + event);

        // Tạo WorkSchedule từ sự kiện Kafka
        WorkSchedule workSchedule = new WorkSchedule();

        // Lấy thông tin từ event và gán vào WorkSchedule
        workSchedule.setDoctorId(event.getDoctorId());
        workSchedule.setWScheduleStatus(event.getWScheduleStatus());
        workSchedule.setWScheduleHours(String.valueOf(event.getWScheduleHours()));
        workSchedule.setCreatedByStaffId(event.getCreatedByStaffId());

        // Gán endTime từ LocalDate trong event
        workSchedule.setEndTime(event.getEndTime());

        // Tạo RegularSchedule từ RegularScheduleDTO
        RegularSchedule regularSchedule = new RegularSchedule();
        if (event.getRegularSchedule() != null) {
            Map<String, Boolean> dayOfTheWeek = new HashMap<>();
            if (event.getRegularSchedule().isMonday()) dayOfTheWeek.put("MONDAY", true);
            if (event.getRegularSchedule().isTuesday()) dayOfTheWeek.put("TUESDAY", true);
            if (event.getRegularSchedule().isWednesday()) dayOfTheWeek.put("WEDNESDAY", true);
            if (event.getRegularSchedule().isThursday()) dayOfTheWeek.put("THURSDAY", true);
            if (event.getRegularSchedule().isFriday()) dayOfTheWeek.put("FRIDAY", true);
            if (event.getRegularSchedule().isSaturday()) dayOfTheWeek.put("SATURDAY", true);
            if (event.getRegularSchedule().isSunday()) dayOfTheWeek.put("SUNDAY", true);

            // Gán Map vào RegularSchedule
            regularSchedule.setDayOfTheWeek(dayOfTheWeek);
        } else {
            // Nếu regularSchedule là null, đảm bảo gán một Map mặc định cho dayOfTheWeek
            Map<String, Boolean> defaultDayOfTheWeek = new HashMap<>();
            defaultDayOfTheWeek.put("MONDAY", false);
            defaultDayOfTheWeek.put("TUESDAY", false);
            defaultDayOfTheWeek.put("WEDNESDAY", false);
            defaultDayOfTheWeek.put("THURSDAY", false);
            defaultDayOfTheWeek.put("FRIDAY", false);
            defaultDayOfTheWeek.put("SATURDAY", false);
            defaultDayOfTheWeek.put("SUNDAY", false);

            // Gán Map mặc định vào regularSchedule
            regularSchedule.setDayOfTheWeek(defaultDayOfTheWeek);
        }
        workSchedule.setRegularSchedule(regularSchedule);

        // Tạo Exceptions từ các ExceptionDTO trong event
        List<Exception> exceptions = new ArrayList<>();
        if (event.getExceptions() != null) {
            for (ExceptionDTO exceptionDTO : event.getExceptions()) {
                Exception exception = new Exception();
                exception.setEcptStatus("ACTIVE");  // Trạng thái có thể được thay đổi tùy thuộc vào logic
                exception.setEcptNote(exceptionDTO.getNote());
                exception.setEcptReason(exceptionDTO.getReason());
                exception.setEcptTime(exceptionDTO.getDate());
                exception.setWorkSchedule(workSchedule);
                exceptions.add(exception);
            }
        }
        workSchedule.setExceptions(exceptions);

        // Lưu WorkSchedule vào database
        workScheduleService.createWorkSchedule(workSchedule);
    }



    @PostMapping("/create")
    public ResponseEntity<WorkSchedule> createWorkSchedule(@RequestBody @Valid WorkScheduleRequest request) {
        // Log xem request có chứa dữ liệu đúng không
        System.out.println("Regular Schedule: " + request.getRegularSchedule());

        WorkSchedule workSchedule = new WorkSchedule();
        workSchedule.setDoctorId(request.getDoctorId());
        workSchedule.setWScheduleStatus(request.getWScheduleStatus());
        workSchedule.setWScheduleHours(request.getWScheduleHours());
        workSchedule.setCreatedByStaffId(request.getCreatedByStaffId());
        workSchedule.setEndTime(request.getEndTime());

        RegularSchedule regularSchedule = new RegularSchedule();
        if (request.getRegularSchedule() != null) {
            regularSchedule.setDayOfTheWeek(request.getRegularSchedule().getDayOfTheWeek());
            regularSchedule.setCurrentDay(request.getRegularSchedule().getCurrentDay());
        }
        workSchedule.setRegularSchedule(regularSchedule);

        // Thiết lập exceptions
        List<Exception> exceptions = new ArrayList<>();
        for (ExceptionRequest exceptionRequest : request.getExceptions()) {
            Exception exception = new Exception();
            exception.setEcptStatus(exceptionRequest.getEcptStatus());
            exception.setEcptNote(exceptionRequest.getEcptNote());
            exception.setEcptTime(exceptionRequest.getEcptTime());
            exception.setWorkSchedule(workSchedule);
            exceptions.add(exception);
        }
        workSchedule.setExceptions(exceptions);

        // Lưu WorkSchedule vào database
        workScheduleService.createWorkSchedule(workSchedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(workSchedule);
    }



    @GetMapping("/{id}")
    public ResponseEntity<WorkSchedule> getWorkScheduleById(@PathVariable String id) {
        return ResponseEntity.ok(workScheduleService.getWorkScheduleById(id));
    }

    @GetMapping
    public ResponseEntity<List<WorkSchedule>> getAllWorkSchedules() {
        return ResponseEntity.ok(workScheduleService.getAllWorkSchedules());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkSchedule> updateWorkSchedule(@PathVariable String id, @RequestBody WorkSchedule workSchedule) {
        return ResponseEntity.ok(workScheduleService.updateWorkSchedule(id, workSchedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkSchedule(@PathVariable String id) {
        workScheduleService.deleteWorkSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
