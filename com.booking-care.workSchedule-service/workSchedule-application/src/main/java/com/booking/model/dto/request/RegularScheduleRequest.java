package com.booking.model.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Data
public class RegularScheduleRequest {
    private Map<String, Boolean> dayOfTheWeek; // Hoặc nếu bạn dùng Enum Map thì sửa lại kiểu dữ liệu cho phù hợp
    private LocalDate currentDay;
}
