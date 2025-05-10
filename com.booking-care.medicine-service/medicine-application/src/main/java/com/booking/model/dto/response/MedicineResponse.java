package com.booking.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MedicineResponse {
    private String medicineId;
    private String medicineAvatar;
    private String medicineName;
    private String medicineStatus;
    private String medicineCategoryName;
    private String medicinePrice;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
