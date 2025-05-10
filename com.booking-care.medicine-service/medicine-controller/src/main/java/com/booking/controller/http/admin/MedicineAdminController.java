package com.booking.controller.http.admin;

import com.booking.domain.models.entities.Medicine;
import com.booking.model.dto.request.MedicineCreateEvent;
import com.booking.model.dto.request.MedicineRequest;
import com.booking.model.dto.response.MedicineResponse;
import com.booking.model.dto.response.ResponseObject;
import com.booking.service.MedicineCategoryService;
import com.booking.service.MedicineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "Medicine API", description = "Quản lý thông tin thuốc")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/medicines")
public class MedicineAdminController {
    private final MedicineService medicineService;
    private final ObjectMapper objectMapper;



    @KafkaListener(topics = "admin-login-topic", groupId = "medicine-service-group-v2")
    public void handleAdminLoginEvent(String message) {
        try {
            System.out.println("Received message from admin-login-topic: " + message);
            MedicineCreateEvent event = objectMapper.readValue(message, MedicineCreateEvent.class);

            // Chỉ xử lý nếu role là ADMIN
            if (!"ADMIN".equalsIgnoreCase(event.getRoleName())) {
                System.out.println("Không phải role ADMIN, bỏ qua event");
                return;
            }

            // Log hoặc xử lý thêm nếu cần
            System.out.println("ADMIN login event received. User ID: " + event.getUserId());

            // TODO: Xử lý logic nếu bạn muốn cập nhật hệ thống, lưu log, thông báo v.v.
            // adminService.handleAdminLogin(event.getUserId());

        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý admin login event: " + e.getMessage());
        }
    }

   // Thêm thuốc mới
   @PostMapping("/add-medicine")
   public ResponseEntity<List<Medicine>> createMedicines(@RequestBody List<MedicineRequest> requests) {
       List<Medicine> medicines = medicineService.createMedicine(requests);
       return ResponseEntity.ok(medicines);
   }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin thuốc theo ID")
    public ResponseEntity<MedicineResponse> getMedicine(@PathVariable("id") String id) {
        return ResponseEntity.ok(medicineService.getMedicine(id));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Lấy danh sách tất cả thuốc")
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @PostMapping("/uploads/{medicineId}")
    @Operation(summary = "Tải ảnh đại diện cho thuốc")
    public ResponseEntity<ResponseObject> uploadMedicineAvatar(
            @PathVariable("medicineId") String medicineId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            medicineService.uploadAvatar(medicineId, file);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload medicine avatar successfully")
                    .status(HttpStatus.CREATED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading image: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }


    @PutMapping("/update/{id}")
    @Operation(summary = "Cập nhật thuốc theo ID")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable("id") String id,
            @RequestBody MedicineRequest request
    ) {
        MedicineResponse updated = medicineService.updateMedicine(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá thuốc theo ID")
    public ResponseEntity<Map<String, String>> deleteMedicine(@PathVariable("id") String id) {
        medicineService.deleteMedicine(id);
        // Trả về JSON: {"message": "Xoá thuốc theo ID thành công"}
        return ResponseEntity.ok(Collections.singletonMap(
                "message", "Xoá thuốc thành công"
        ));
    }

}

