package com.booking.domain.models.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "protocol")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Protocol {

    @Id
    @Column(name = "protocol_id")
    private String protocolId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String avatarProtocol;

    private String password;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String userId;




    @PrePersist
    public void generateId() {
        this.protocolId = "protocol" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        this.createTime = LocalDateTime.now();
    }
}
