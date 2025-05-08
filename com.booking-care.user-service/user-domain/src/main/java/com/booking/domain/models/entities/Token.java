package com.booking.domain.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    private String token_id;

    @PrePersist
    public void generateId() {
        this.token_id = "role_id" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private String jwtId;

    @Column(name = "token", length = 1000)
    private String token;

    @Column(name = "refresh_token", length = 250)
    private String refreshToken;

    @Column(name = "token_type", length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

    @Column(name = "is_mobile", columnDefinition = "TINYINT(1)")
    private boolean isMobile;

    private boolean revoked;
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
