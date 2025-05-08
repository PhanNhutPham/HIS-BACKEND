package com.booking.infrastructure.persistence.mapper;

import com.booking.domain.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJPA extends JpaRepository<Role, String> {
    @Query("SELECT r FROM Role r WHERE r.nameRole = :roleName")
    Role findByName(@Param("roleName")String roleName);
}
