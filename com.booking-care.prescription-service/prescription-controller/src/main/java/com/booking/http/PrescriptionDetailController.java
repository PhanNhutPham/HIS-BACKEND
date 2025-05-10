package com.booking.http;

import com.booking.mapper.PrescriptionJPA;
import com.booking.models.entities.PrescriptionDetail;
import com.booking.request.PrescriptionDetailRequest;
import com.booking.service.PrescriptionDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "PrescriptionDetail API", description = "Quản lý chi tiết đơn thuốc")
@RestController
@RequestMapping("/api/prescription-detail")
public class PrescriptionDetailController {

    private final PrescriptionDetailService prescriptionDetailService;
    private final PrescriptionJPA prescriptionRepository;

    // Constructor injection for prescriptionRepository
    public PrescriptionDetailController(PrescriptionDetailService prescriptionDetailService, PrescriptionJPA prescriptionRepository) {
        this.prescriptionDetailService = prescriptionDetailService;
        this.prescriptionRepository = prescriptionRepository;
    }

    @PostMapping
    @Operation(summary = "Tạo nhiều chi tiết đơn thuốc mới")
    public ResponseEntity<List<PrescriptionDetail>> createPrescriptionDetails(@RequestBody List<PrescriptionDetailRequest> requests) {
        List<PrescriptionDetail> prescriptionDetails = requests.stream().map(request -> {
            PrescriptionDetail prescriptionDetail = PrescriptionDetail.builder()
                    .medicineId(request.getMedicineId())
                    .medicineName(request.getMedicineName())
                    .dosage(request.getDosage())
                    .frequency(request.getPresDe_frequency())
                    .note(request.getPresDe_note())
                    .quanlity(request.getQuantity())
                    .prescription(prescriptionRepository.findById(request.getPrescriptionId())
                            .orElseThrow(() -> new RuntimeException("Prescription not found")))
                    .build();
            return prescriptionDetailService.createPrescriptionDetail(prescriptionDetail);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(prescriptionDetails);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết đơn thuốc theo ID")
    public ResponseEntity<Optional<PrescriptionDetail>> getPrescriptionDetailById(
            @Parameter(description = "ID của chi tiết đơn thuốc cần lấy", required = true)
            @PathVariable(name = "id") String id) {
        return ResponseEntity.ok(prescriptionDetailService.getPrescriptionDetailById(id));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả chi tiết đơn thuốc")
    public ResponseEntity<List<PrescriptionDetail>> getAllPrescriptionDetails() {
        return ResponseEntity.ok(prescriptionDetailService.getAllPrescriptionDetails());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật chi tiết đơn thuốc")
    public ResponseEntity<PrescriptionDetail> updatePrescriptionDetail(
            @Parameter(description = "ID của chi tiết đơn thuốc cần cập nhật", required = true)
            @PathVariable(name = "id") String id,
            @RequestBody PrescriptionDetailRequest request) {

        PrescriptionDetail updatedDetail = PrescriptionDetail.builder()
                .medicineId(request.getMedicineId())
                .medicineName(request.getMedicineName())
                .dosage(request.getDosage())
                .frequency(request.getPresDe_frequency())
                .note(request.getPresDe_note())
                .quanlity(request.getQuantity())
                .build();

        return ResponseEntity.ok(prescriptionDetailService.updatePrescriptionDetail(id, updatedDetail));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa chi tiết đơn thuốc theo ID")
    public ResponseEntity<Void> deletePrescriptionDetail(
            @Parameter(description = "ID của chi tiết đơn thuốc cần xóa", required = true)
            @PathVariable(name = "id") String id) {
        prescriptionDetailService.deletePrescriptionDetail(id);
        return ResponseEntity.noContent().build();
    }
}
