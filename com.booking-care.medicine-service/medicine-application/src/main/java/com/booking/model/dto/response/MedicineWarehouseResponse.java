package com.booking.model.dto.response;

import com.booking.domain.models.entities.Medicine;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MedicineWarehouseResponse {
    private String medicineWarehouseId;
    private String medicineAvatar;
    private String medicineName;
    private  String medicineQuantity;
    private String medicineStatus;
    private String medicinePrice;
    private LocalDateTime medicineExpirationDay;
    private  LocalDateTime dayOfEntry;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
