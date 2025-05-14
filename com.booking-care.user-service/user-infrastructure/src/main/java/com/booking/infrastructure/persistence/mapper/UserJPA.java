package com.booking.infrastructure.persistence.mapper;


import com.booking.domain.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJPA  extends JpaRepository<User, String> {
    Optional<User> findUserByUserId(String userId);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.nameRole = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
}
