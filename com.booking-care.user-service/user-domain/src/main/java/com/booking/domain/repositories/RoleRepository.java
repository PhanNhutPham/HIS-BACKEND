package com.booking.domain.repositories;

import com.booking.domain.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    @Query("SELECT r FROM Role r WHERE r.nameRole = :roleName")
    Optional<Role> findByName(@Param("roleName")String roleName);
}
