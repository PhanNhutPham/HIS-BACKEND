package com.booking.impl;

import com.booking.domain.models.entities.Medicine;
import com.booking.domain.models.entities.MedicineWarehouse;
import com.booking.domain.repositories.MedicineWarehouseRepository;
import com.booking.infrastructure.persistence.mapper.MedicineJPA;
import com.booking.infrastructure.persistence.mapper.MedicineWarehouseJPA;
import com.booking.model.dto.request.MedicineWarehouseRequest;
import com.booking.model.dto.response.MedicineWarehouseResponse;
import com.booking.service.MedicineWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequiredArgsConstructor
@Service
public class MedicineWarehouseServiceImpl implements MedicineWarehouseService {
    private final MedicineWarehouseJPA medicineWarehouseRepository;
    private final MedicineJPA medicineRepository;


    @Override
    public List<MedicineWarehouse> createMedicineWarehouse(List<MedicineWarehouseRequest> requests) {
        List<MedicineWarehouse> warehouses = new ArrayList<>();

        for (MedicineWarehouseRequest request : requests) {
            Medicine medicine = medicineRepository.findById(request.getMedicine_id())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            MedicineWarehouse warehouse = MedicineWarehouse.builder()
                    .medicine(medicine)
                    .medicineQuantity(Integer.valueOf(request.getMedicineQuantity()))
                    .medicineExpirationDay(request.getMedicineExpirationDay())
                    .dayOfEntry(request.getDayOfEntry())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            warehouses.add(warehouse);
        }

        return medicineWarehouseRepository.saveAll(warehouses);
    }

    @Override
    public MedicineWarehouseResponse getMedicineWarehouse(String id) {
        MedicineWarehouse warehouse = medicineWarehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho thuốc với ID: " + id));

        return mapToResponse(warehouse);
    }

    @Override
    public List<MedicineWarehouseResponse> getAllMedicineWarehouses() {
        List<MedicineWarehouse> warehouses = medicineWarehouseRepository.findAll();
        return warehouses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private MedicineWarehouseResponse mapToResponse(MedicineWarehouse warehouse) {
        MedicineWarehouseResponse response = new MedicineWarehouseResponse();
        response.setMedicineWarehouseId(warehouse.getMedicineWarehouseId());
        response.setMedicineQuantity(warehouse.getMedicineQuantity());
        response.setMedicineExpirationDay(warehouse.getMedicineExpirationDay());
        response.setDayOfEntry(warehouse.getDayOfEntry());
        response.setCreateTime(warehouse.getCreateTime());
        response.setUpdateTime(warehouse.getUpdateTime());

        Medicine medicine = warehouse.getMedicine();
        if (medicine != null) {
            response.setMedicineName(medicine.getMedicineName());
            response.setMedicineAvatar(medicine.getMedicineAvatar());
            response.setMedicineStatus(medicine.getMedicineStatus());
            response.setMedicinePrice(medicine.getMedicinePrice());

            // Lấy tên loại thuốc
            if (medicine.getMedicineCategory() != null) {
                response.setMedicineCategoryName(medicine.getMedicineCategory().getMedicineCategoryName());
            } else {
                response.setMedicineCategoryName(null);
            }

            // Lấy dạng bào chế
            if (medicine.getDosageForm() != null) {
                response.setDosageFormName(medicine.getDosageForm().getDosageFormName());
            } else {
                response.setDosageFormName(null);
            }
        } else {
            response.setMedicineName(null);
            response.setMedicineAvatar(null);
            response.setMedicineStatus(null);
            response.setMedicinePrice(null);
            response.setMedicineCategoryName(null);
            response.setDosageFormName(null);
        }

        return response;
    }


    @Override
    public MedicineWarehouseResponse updateMedicineWarehouse(String id, MedicineWarehouseRequest request) {
        MedicineWarehouse warehouse = medicineWarehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho thuốc với ID: " + id));

        warehouse.setMedicineQuantity(Integer.valueOf(request.getMedicineQuantity()));
        warehouse.setMedicineExpirationDay(request.getMedicineExpirationDay());
        warehouse.setDayOfEntry(request.getDayOfEntry());

//        // Gán lại thông tin thuốc nếu ID thuốc thay đổi
//        Medicine medicine = medicineRepository.findById(request.getMedicine_id())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + request.getMedicine_id()));
//        warehouse.setMedicine(medicine);

        warehouse.setUpdateTime(LocalDateTime.now());
        MedicineWarehouse saved = medicineWarehouseRepository.save(warehouse);

        return mapToResponse(saved);
    }

    @Override
    public void deleteMedicineWarehouse(String id) {
        MedicineWarehouse warehouse = medicineWarehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho thuốc với ID: " + id));
        medicineWarehouseRepository.delete(warehouse);
    }

    @Override
    public boolean checkStock(String medicineId, Integer quantity) {
        MedicineWarehouse warehouse = medicineWarehouseRepository.findByMedicine_MedicineId(medicineId);

        if (warehouse == null) {
            throw new RuntimeException("Không tìm thấy thuốc trong kho với ID: " + medicineId);
        }

        return warehouse.getMedicineQuantity() >= quantity;
    }



    public boolean isMedicineInStock(String medicineId, int requiredQuantity) {
        return checkStock(medicineId, requiredQuantity);
    }


    // Giả sử bạn có một phương thức để lấy số lượng thuốc trong kho từ database hoặc kho dữ li
}
