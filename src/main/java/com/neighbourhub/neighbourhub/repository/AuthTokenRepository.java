package com.neighbourhub.neighbourhub.repository;

import com.neighbourhub.neighbourhub.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByToken(String token);

    boolean existsByTokenAndRevokedFalse(String token);
}