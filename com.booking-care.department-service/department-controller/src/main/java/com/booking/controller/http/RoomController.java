package com.booking.controller.http;

import com.booking.controller.requests.RoomRequest;
import com.booking.domain.models.entities.Room;
import com.booking.domain.models.entities.Department;
import com.booking.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Room API", description = "Quản lý thông tin phòng")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    private Room toRoomEntity(RoomRequest request) {
        Department department = new Department();
        department.setDepartment_id(request.getDepartmentId());
        department.setName(request.getDepartmentName());

        return Room.builder()
                .roomNumber(request.getRoomNumber())
                .department(department)
                .build();
    }

    @PostMapping
    @Operation(summary = "Tạo phòng mới")
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequest request) {
        if (request.getRoomNumber() == null || request.getRoomNumber().isEmpty()
                || request.getDepartmentId() == null || request.getDepartmentId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Room createdRoom = roomService.createRoom(toRoomEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin phòng")
    public ResponseEntity<Room> updateRoom(
            @Parameter(description = "ID của phòng cần cập nhật", required = true)
            @PathVariable(name = "id") String id,
            @RequestBody RoomRequest request) {

        if (request.getRoomNumber() == null || request.getRoomNumber().isEmpty()
                || request.getDepartmentId() == null || request.getDepartmentId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Room updatedRoom = roomService.updateRoom(id, toRoomEntity(request));
        return ResponseEntity.ok(updatedRoom);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin phòng theo ID")
    public ResponseEntity<Room> getRoom(
            @Parameter(description = "ID của phòng cần lấy", required = true)
            @PathVariable(name = "id") String id) {

        return ResponseEntity.ok(roomService.getRoom(id));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả phòng")
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá phòng theo ID")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "ID của phòng cần xoá", required = true)
            @PathVariable(name = "id") String id) {

        boolean isDeleted = roomService.deleteRoom(id);
        return isDeleted ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
