package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "department")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Department {

    @Id
    @Column(name = "department_id")
    @Schema(description = "ID của khoa")
    private String department_id;

    @PrePersist
    public void generateId() {
        if (this.department_id == null || this.department_id.isEmpty()) {
            this.department_id = "dpmnt" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Room> rooms;

    @Schema(description = "Tên khoa")
    private String name;
}

