package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "room_number")
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonBackReference
    private Department department;

    @PrePersist
    public void generateId() {
        if (this.roomId == null || this.roomId.isEmpty()) {
            this.roomId = "room" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }
}
