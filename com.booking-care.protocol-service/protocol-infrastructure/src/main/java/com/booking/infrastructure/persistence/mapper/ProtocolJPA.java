package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProtocolJPA extends JpaRepository<Protocol, String> {
    Optional<Protocol> findByUserId(String userId);
}
