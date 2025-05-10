package com.booking.service;

import com.booking.domain.models.entities.MedicineWarehouse;
import com.booking.model.dto.request.MedicineWarehouseRequest;
import com.booking.model.dto.response.MedicineWarehouseResponse;

import java.util.List;

public interface MedicineWarehouseService {
    List<MedicineWarehouse> createMedicineWarehouse(List<MedicineWarehouseRequest> requests);
    MedicineWarehouseResponse getMedicineWarehouse(String id);
    List<MedicineWarehouseResponse> getAllMedicineWarehouses();

    MedicineWarehouseResponse updateMedicineWarehouse(String id, MedicineWarehouseRequest request);

    void deleteMedicineWarehouse(String id);

    boolean checkStock(String medicineId, Integer quantity);
}
