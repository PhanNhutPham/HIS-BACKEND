package com.booking.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DosageFormResponse {
    private String dosageFormId;
    private String dosageFormName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}