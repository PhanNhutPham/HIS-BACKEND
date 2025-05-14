package com.booking.service;

import com.booking.domain.models.entities.WorkSchedule;
import java.util.List;

public interface WorkScheduleService {
    // Tạo mới lịch làm việc cho bác sĩ
    WorkSchedule createWorkSchedule(WorkSchedule workSchedule);

    // Lấy lịch làm việc theo ID
    WorkSchedule getWorkScheduleById(String id);

    // Lấy danh sách tất cả lịch làm việc
    List<WorkSchedule> getAllWorkSchedules();

    // Cập nhật thông tin lịch làm việc
    WorkSchedule updateWorkSchedule(String id, WorkSchedule updatedWorkSchedule);

    // Xóa lịch làm việc theo ID
    void deleteWorkSchedule(String id);
}
