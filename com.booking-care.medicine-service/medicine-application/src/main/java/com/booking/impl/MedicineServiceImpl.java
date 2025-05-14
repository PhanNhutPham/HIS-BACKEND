package com.booking.impl;

import com.booking.domain.models.entities.Medicine;
import com.booking.domain.models.entities.MedicineCategory;
import com.booking.exceptions.DataNotFoundException;
import com.booking.infrastructure.persistence.mapper.MedicineCategoryJPA;
import com.booking.infrastructure.persistence.mapper.MedicineJPA;
import com.booking.model.dto.request.MedicineRequest;
import com.booking.model.dto.response.MedicineResponse;
import com.booking.service.IFileService;
import com.booking.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.booking.enums.ResultCode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MedicineServiceImpl implements MedicineService {
    private final MedicineJPA medicineRepository;
    private final MedicineCategoryJPA medicineCategoryJPA;
    private final IFileService fileService;
    @Override
    public List<Medicine> createMedicine(List<MedicineRequest> requests) {
        List<Medicine> medicines = new ArrayList<>();

        for (MedicineRequest request : requests) {
            MedicineCategory category = medicineCategoryJPA.findById(request.getMedicineCategory_id())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Medicine medicine = Medicine.builder()
                    .medicineName(request.getMedicineName())
                    .medicineStatus(request.getMedicineStatus())
                    .create_at(LocalDateTime.now())
                    .update_at(LocalDateTime.now())
                    .medicineAvatar(request.getMedicineAvatar())
                    .medicinePrice(request.getMedicinePrice())
                    .medicineCategory(category)
                    .build();

            medicines.add(medicine);
        }

        return medicineRepository.saveAll(medicines); // Sử dụng saveAll để lưu nhiều đối tượng
    }


    @Override
    public MedicineResponse getMedicine(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));

        MedicineResponse response = new MedicineResponse();
        response.setMedicineId(medicine.getMedicineId());
        response.setMedicineName(medicine.getMedicineName());
        response.setMedicineAvatar(medicine.getMedicineAvatar() != null ? medicine.getMedicineAvatar() : null); // Trả về null nếu không có
        response.setMedicineStatus(medicine.getMedicineStatus());
        response.setCreateTime(medicine.getCreate_at() != null ? medicine.getCreate_at() : null); // Trả về null nếu không có
        response.setUpdateTime(medicine.getUpdate_at() != null ? medicine.getUpdate_at() : null); // Trả về null nếu không có
        response.setMedicinePrice(medicine.getMedicinePrice() != null ? medicine.getMedicinePrice() : null);

        if (medicine.getMedicineCategory() != null) {
            response.setMedicineCategoryName(medicine.getMedicineCategory().getMedicineCategoryName());
        } else {
            response.setMedicineCategoryName(null); // Trả về null nếu không có category
        }

        return response;
    }



    @Override
    public List<MedicineResponse> getAllMedicines() {
        List<Medicine> medicines = medicineRepository.findAll();

        return medicines.stream().map(medicine -> {
            MedicineResponse response = new MedicineResponse();
            response.setMedicineId(medicine.getMedicineId());
            response.setMedicineName(medicine.getMedicineName());
            response.setMedicineAvatar(medicine.getMedicineAvatar() != null ? medicine.getMedicineAvatar() : null); // Trả về null nếu không có
            response.setMedicineStatus(medicine.getMedicineStatus());
            response.setCreateTime(medicine.getCreate_at());
            response.setUpdateTime(medicine.getUpdate_at());
            response.setMedicinePrice(medicine.getMedicinePrice());

            if (medicine.getMedicineCategory() != null || medicine.getDosageForm() != null) {
                response.setMedicineCategoryName(medicine.getMedicineCategory().getMedicineCategoryName());
                response.setDosageFormName(medicine.getDosageForm().getDosageFormName());
            } else {
                response.setMedicineCategoryName(null);
                response.setDosageFormName(null);// Trả về null nếu không có category
            }

            return response;
        }).collect(Collectors.toList());
    }


    @Override
    public void uploadAvatar(String medicineId, MultipartFile file) throws Exception {
        Medicine existingMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        validateFileUpload(file); // Tự viết hàm này hoặc dùng chung với user

        try {
            String imageAvatar = fileService.storeFile(file); // Trả về tên file đã lưu
            existingMedicine.setMedicineAvatar(imageAvatar);  // Gán vào entity
            medicineRepository.save(existingMedicine);        // Lưu lại
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload medicine avatar: " + e.getMessage(), e);
        }
    }

    private void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large! Maximum size is 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }



    @Override
    public MedicineResponse updateMedicine(String id, MedicineRequest request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));

        medicine.setMedicineName(request.getMedicineName());
        medicine.setMedicineStatus(request.getMedicineStatus());
        medicine.setMedicinePrice(request.getMedicinePrice());

        // cập nhật category nếu cần
        MedicineCategory category = medicineCategoryJPA.findById(request.getMedicineCategory_id())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getMedicineCategory_id()));
        medicine.setMedicineCategory(category);

        medicine.setUpdate_at(LocalDateTime.now());
        Medicine saved = medicineRepository.save(medicine);

        MedicineResponse resp = new MedicineResponse();
        resp.setMedicineId(saved.getMedicineId());
        resp.setMedicineName(saved.getMedicineName());
        resp.setMedicineStatus(saved.getMedicineStatus());
        resp.setMedicineAvatar(saved.getMedicineAvatar());
        resp.setMedicineCategoryName(saved.getMedicineCategory().getMedicineCategoryName());
        resp.setMedicinePrice(saved.getMedicinePrice());
        resp.setCreateTime(saved.getCreate_at());
        resp.setUpdateTime(saved.getUpdate_at());
        return resp;
    }

    @Override
    public void deleteMedicine(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));
        medicineRepository.delete(medicine);
    }
}