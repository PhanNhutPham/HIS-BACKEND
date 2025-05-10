package com.booking.service;

import com.booking.domain.models.entities.Medicine;
import com.booking.model.dto.request.MedicineRequest;
import com.booking.model.dto.response.MedicineResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MedicineService {
    List<Medicine> createMedicine(List<MedicineRequest> requests);
    MedicineResponse getMedicine(String id);
    List<MedicineResponse> getAllMedicines();
    MedicineResponse updateMedicine(String id, MedicineRequest request);
    void deleteMedicine(String id);
    void uploadAvatar(String medicineId, MultipartFile file) throws Exception;
}
