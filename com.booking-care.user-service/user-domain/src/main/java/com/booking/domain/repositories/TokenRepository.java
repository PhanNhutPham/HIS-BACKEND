package com.booking.domain.repositories;

import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByUser(User user);

    Optional<Token> findByToken(String token);

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByJwtId(@Param("jwtId") String jwtId);
}
