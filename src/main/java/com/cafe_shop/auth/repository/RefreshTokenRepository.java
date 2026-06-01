package com.cafe_shop.auth.repository;

import com.cafe_shop.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    long deleteByExpiresAtBefore(Instant now);
}
