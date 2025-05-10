package com.booking.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineRequest {
    private String medicineName;
    private String medicineStatus;
    private String medicinePrice;
    private String medicineCategory_id;
    private String medicineAvatar;
}
