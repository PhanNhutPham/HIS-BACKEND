package com.booking.model.dto.request;

import com.booking.domain.models.enums.WorkScheduleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class WorkScheduleRequest {
    private String doctorId; // ID của bác sĩ
    private WorkScheduleStatus wScheduleStatus; // Trạng thái của lịch làm việc
    private String wScheduleHours; // Thời gian làm việc (ví dụ: "08:00-12:00")
    private String createdByStaffId; // ID của nhân viên tạo lịch làm việc
    private LocalDate endTime; // Thời gian kết thúc của lịch làm việc
    private RegularScheduleRequest regularSchedule; // Lịch làm việc định kỳ (mỗi ngày trong tuần)
    private List<ExceptionRequest> exceptions; // Danh sách ngoại lệ (ngày nghỉ, lý do nghỉ)


}
