package com.booking.services;

import com.booking.domain.models.entities.Room;

import java.util.List;

public interface RoomService {
    Room createRoom(Room room);

    Room getRoom(String id);

    List<Room> getAllRooms();

    Room updateRoom(String id, Room room);

    boolean deleteRoom(String id);
}
