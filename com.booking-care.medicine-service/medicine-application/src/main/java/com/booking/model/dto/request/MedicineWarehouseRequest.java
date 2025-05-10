package com.booking.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineWarehouseRequest {
    private String medicineQuantity;
    private LocalDateTime medicineExpirationDay;
    private LocalDateTime dayOfEntry;
    private String medicine_id;
}
