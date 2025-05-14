package com.booking.controller.http;

import com.booking.controller.requests.DepartmentRequest;
import com.booking.domain.models.entities.Department;
import com.booking.request.AssignDoctorEvent;
import com.booking.request.DepartmentEventRequest;
import com.booking.services.DepartmentService;
import com.booking.services.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Department API", description = "Quản lý thông tin khoa phòng")
@RestController
@RequestMapping("/admin/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final KafkaProducerService kafkaProducerService;

    public DepartmentController(DepartmentService departmentService, KafkaProducerService kafkaProducerService) {
        this.departmentService = departmentService;
        this.kafkaProducerService = kafkaProducerService;
    }
    @PostMapping("/assign-doctor")
    public ResponseEntity<String> assignDoctorToDepartment(@RequestBody AssignDoctorEvent event) {
        kafkaProducerService.sendDoctorAssignmentEvent(event);
        return ResponseEntity.ok("Gửi event gán bác sĩ vào khoa thành công");
    }

    @PostMapping("/add_department")
    @Operation(summary = "Tạo khoa mới")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .build();

        // Lưu vào database trước để JPA sinh ID
        Department saved = departmentService.createDepartment(department);

        // Gửi sự kiện Kafka sau khi đã có ID

        return ResponseEntity.ok(saved);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin khoa theo ID và bao gồm các phòng")
    public ResponseEntity<Department> getDepartment(
            @Parameter(description = "ID của khoa cần lấy", required = true)
            @PathVariable(name = "id") String id) {
        Department department = departmentService.getDepartmentWithRooms(id); // Gọi đúng service
        return ResponseEntity.ok(department);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khoa và các phòng")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khoa")
    public ResponseEntity<Department> updateDepartment(
            @Parameter(description = "ID của khoa cần cập nhật", required = true)
            @PathVariable(name = "id") String id,
            @Valid @RequestBody Department department) {

        // Gửi sự kiện cập nhật qua Kafka
        kafkaProducerService.sendDepartmentEventCreateAsync(new DepartmentEventRequest(
                department.getDepartment_id(), department.getName(), "UPDATE"));

        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá khoa theo ID")
    public ResponseEntity<Void> deleteDepartment(
            @Parameter(description = "ID của khoa cần xoá", required = true)
            @PathVariable(name = "id") String id) {
        departmentService.deleteDepartment(id);

        // Gửi sự kiện xoá qua Kafka
        kafkaProducerService.sendDepartmentEventCreateAsync(new DepartmentEventRequest(id, null, "DELETE"));

        return ResponseEntity.noContent().build();
    }
}
