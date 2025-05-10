package com.booking.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MedicineCategoryResponse {
    private String medicineCategoryId;
    private String medicineCategoryName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
