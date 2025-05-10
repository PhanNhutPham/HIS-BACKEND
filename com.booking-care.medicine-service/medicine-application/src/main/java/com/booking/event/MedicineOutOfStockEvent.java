package com.booking.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineOutOfStockEvent {
    private String medicineId;
    private String medicineName;
    private int requiredQuantity;
}
