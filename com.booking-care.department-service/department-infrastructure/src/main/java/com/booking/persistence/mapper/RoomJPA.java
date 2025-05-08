package com.booking.persistence.mapper;
import com.booking.domain.models.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJPA extends JpaRepository<Room, String> {
}


