package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Método crucial para buscar un token que viene del cliente
    Optional<RefreshToken> findByToken(String token);

    // Método útil para buscar si un usuario ya tiene un token (evita duplicados)
    Optional<RefreshToken> findByUsuarioId(Long userId);
}