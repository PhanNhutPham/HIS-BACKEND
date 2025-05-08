package com.booking.domain.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission {


    @Id
    @Column(name = "permission_id")
    private String permissionId;

    @PrePersist
    public void generateId() {
        this.permissionId = "permission_id" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    @Column(name = "name_permission", nullable = false, length = 35)
    private String namePermissions;

    @Column(name = "description_permission", length = 150)
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Role> role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
