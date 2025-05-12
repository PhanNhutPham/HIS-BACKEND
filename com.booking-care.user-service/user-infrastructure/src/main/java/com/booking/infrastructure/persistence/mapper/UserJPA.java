package com.booking.infrastructure.persistence.mapper;


import com.booking.domain.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJPA  extends JpaRepository<User, String> {
    Optional<User> findUserByUserId(String userId);
}
