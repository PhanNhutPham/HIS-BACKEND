package com.booking.impl;

import com.booking.domain.models.entities.WorkSchedule;
import com.booking.infrastructure.persistence.mapper.WorkScheduleJPA;
import com.booking.service.WorkScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleJPA workScheduleRepository;

    public WorkScheduleServiceImpl(WorkScheduleJPA workScheduleRepository) {
        this.workScheduleRepository = workScheduleRepository;
    }

    @Override
    public WorkSchedule createWorkSchedule(WorkSchedule workSchedule) {
        // Tạo mới lịch làm việc cho bác sĩ
        return workScheduleRepository.save(workSchedule);
    }

    @Override
    public WorkSchedule getWorkScheduleById(String id) {
        // Lấy lịch làm việc theo ID
        return workScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkSchedule not found with ID: " + id));
    }

    @Override
    public List<WorkSchedule> getAllWorkSchedules() {
        // Lấy danh sách tất cả lịch làm việc
        return workScheduleRepository.findAll();
    }

    @Override
    public WorkSchedule updateWorkSchedule(String id, WorkSchedule updatedWorkSchedule) {
        // Cập nhật thông tin lịch làm việc
        WorkSchedule workSchedule = getWorkScheduleById(id);

        workSchedule.setDoctorId(updatedWorkSchedule.getDoctorId());
        workSchedule.setCreatedByStaffId(updatedWorkSchedule.getCreatedByStaffId());
        workSchedule.setWScheduleStatus(updatedWorkSchedule.getWScheduleStatus());
        workSchedule.setWScheduleHours(updatedWorkSchedule.getWScheduleHours());
        workSchedule.setEndTime(updatedWorkSchedule.getEndTime());
        workSchedule.setRegularSchedule(updatedWorkSchedule.getRegularSchedule());

        return workScheduleRepository.save(workSchedule);
    }

    @Override
    public void deleteWorkSchedule(String id) {
        // Xóa lịch làm việc theo ID
        workScheduleRepository.deleteById(id);
    }
}
