package com.booking.infrastructure.persistence.mapper;


import com.booking.domain.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJPA  extends JpaRepository<User, String> {

}
