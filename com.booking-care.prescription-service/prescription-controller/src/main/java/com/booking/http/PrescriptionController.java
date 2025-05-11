package com.booking.http;

import com.booking.models.entities.Prescription;
import com.booking.request.PrescriptionRequest;
import com.booking.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Prescription API", description = "Quản lý đơn thuốc")
@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }
    @PostMapping("/prescriptions")
    public ResponseEntity<?> createPrescriptionWithDetails(@RequestBody PrescriptionRequest request) {
        Prescription prescription = prescriptionService.createPrescriptionWithDetails(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
    }

//    @PostMapping
//    @Operation(summary = "Tạo đơn thuốc mới")
//    public ResponseEntity<Prescription> createPrescription(@RequestBody PrescriptionRequest request) {
//        Prescription prescription = Prescription.builder()
//                .doctorId(request.getDoctorId())
//                .patientId(request.getPatientId())
//                .paymentStatus(request.getPaymentStatus())
//                .presDiagnosis(request.getPresDiagnosis())
//                .presDate(request.getPresDate())
//                .presExpiryDate(request.getPresExpiryDate())
//                .build();
//
//        return ResponseEntity.ok(prescriptionService.createPrescription(prescription));
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin đơn thuốc theo ID")
    public ResponseEntity<Prescription> getPrescriptionById(
            @Parameter(description = "ID của đơn thuốc cần lấy", required = true)
            @PathVariable(name = "id") String id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with ID: " + id)));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả đơn thuốc")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin đơn thuốc")
    public ResponseEntity<Prescription> updatePrescription(
            @Parameter(description = "ID của đơn thuốc cần cập nhật", required = true)
            @PathVariable(name = "id") String id,
            @RequestBody PrescriptionRequest request) {

        Prescription prescription = Prescription.builder()
                .doctorId(request.getDoctorId())
                .patientId(request.getPatientId())
                .paymentStatus(request.getPaymentStatus())
                .presDiagnosis(request.getPresDiagnosis())
                .presDate(request.getPresDate())
                .presExpiryDate(request.getPresExpiryDate())
                .build();

        return ResponseEntity.ok(prescriptionService.updatePrescription(id, prescription));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa đơn thuốc theo ID")
    public ResponseEntity<Void> deletePrescription(
            @Parameter(description = "ID của đơn thuốc cần xóa", required = true)
            @PathVariable(name = "id") String id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
