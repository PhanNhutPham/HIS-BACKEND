package com.booking.controller.http.admin;

import com.booking.domain.models.entities.MedicineWarehouse;
import com.booking.event.MedicineOutOfStockEvent;
import com.booking.domain.event.PrescriptionDetailCreatedEvent;
import com.booking.model.dto.request.MedicineWarehouseRequest;
import com.booking.model.dto.response.MedicineWarehouseResponse;
import com.booking.service.MedicineWarehouseService;
import com.booking.service.kafka.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Tag(name = "MedicineWarehouse API", description = "Quản lý kho thông tin thuốc")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/medicine-warehouse")
public class MedicineWarehouseAdminController {
    private final MedicineWarehouseService medicineWarehouseService;
    private final KafkaProducerService kafkaProducerService;
    @KafkaListener(
            topics = "prescription-detail-created",
            groupId = "medicine-service-group-v2",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePrescriptionCreated(PrescriptionDetailCreatedEvent event) {
        for (PrescriptionDetailCreatedEvent.MedicineUsage medicineUsage : event.getMedicines()) {
            String medicineId = medicineUsage.getMedicineId();
            int requiredQuantity = medicineUsage.getQuantity();

            boolean isInStock = medicineWarehouseService.checkStock(medicineId, requiredQuantity);

            if (!isInStock) {
                MedicineOutOfStockEvent outOfStockEvent = new MedicineOutOfStockEvent(
                        medicineId,
                        medicineUsage.getMedicineName(),
                        requiredQuantity
                );
                try {
                    kafkaProducerService.sendMedicineOutOfStockEvent(outOfStockEvent);
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Gửi sự kiện thiếu thuốc thất bại: " + e.getMessage());
                    Thread.currentThread().interrupt(); // xử lý interruption đúng cách
                    // KHÔNG throw lại exception nếu không muốn listener bị fail
                }
            }
        }
    }



    // Gửi sự kiện MedicineOutOfStock
    @PostMapping("/add")
    public ResponseEntity<List<MedicineWarehouse>> createMedicineWarehouses(@RequestBody List<MedicineWarehouseRequest> requests) {
        List<MedicineWarehouse> warehouses = medicineWarehouseService.createMedicineWarehouse(requests);
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin kho thuốc theo ID")
    public ResponseEntity<MedicineWarehouseResponse> getMedicineWarehouse(@PathVariable("id") String id) {
        return ResponseEntity.ok(medicineWarehouseService.getMedicineWarehouse(id));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Lấy danh sách tất cả kho thuốc")
    public ResponseEntity<List<MedicineWarehouseResponse>> getAllMedicineWarehouses() {
        return ResponseEntity.ok(medicineWarehouseService.getAllMedicineWarehouses());
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Cập nhật kho thuốc theo ID")
    public ResponseEntity<MedicineWarehouseResponse> updateMedicineWarehouse(
            @PathVariable("id") String id,
            @RequestBody MedicineWarehouseRequest request
    ) {
        MedicineWarehouseResponse updated = medicineWarehouseService.updateMedicineWarehouse(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Xoá kho thuốc theo ID")
    public ResponseEntity<Map<String, String>> deleteMedicineWarehouse(@PathVariable("id") String id) {
        medicineWarehouseService.deleteMedicineWarehouse(id);
        return ResponseEntity.ok(Collections.singletonMap(
                "message", "Xoá kho thuốc thành công"
        ));
    }
}
