package com.booking.impl;

import com.booking.domain.models.entities.Department;
import com.booking.domain.models.entities.Room;
import com.booking.persistence.mapper.DepartmentJPA;
import com.booking.persistence.mapper.RoomJPA;
import com.booking.services.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomJPA roomRepository;
    private final DepartmentJPA departmentRepository;

    public RoomServiceImpl(RoomJPA roomRepository, DepartmentJPA departmentRepository) {
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Room createRoom(Room room) {
        Department department = departmentRepository.findById(room.getDepartment().getDepartment_id())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoa"));
        room.setDepartment(department);
        return roomRepository.save(room);
    }

    @Override
    public Room getRoom(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room updateRoom(String id, Room updatedRoom) {
        Room room = getRoom(id);

        room.setRoomNumber(updatedRoom.getRoomNumber());

        Department department = departmentRepository.findById(updatedRoom.getDepartment().getDepartment_id())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoa"));
        room.setDepartment(department);

        return roomRepository.save(room);
    }

    @Override
    public boolean deleteRoom(String id) {
        if (!roomRepository.existsById(id)) return false;
        roomRepository.deleteById(id);
        return true;
    }
}
