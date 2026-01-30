package com.netflix.mercado.repository;

import com.netflix.mercado.entity.RefreshToken;
import com.netflix.mercado.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT r FROM RefreshToken r WHERE r.user = :user AND r.revogado = false AND r.active = true ORDER BY r.dataExpiracao DESC")
    List<RefreshToken> findValidTokensByUser(@Param("user") User user);

    @Query("SELECT r FROM RefreshToken r WHERE r.user = :user AND r.active = true")
    List<RefreshToken> findByUser(@Param("user") User user);

    @Query("SELECT COUNT(r) FROM RefreshToken r WHERE r.user = :user AND r.revogado = false AND r.active = true")
    long countValidTokensByUser(@Param("user") User user);

    @Query("UPDATE RefreshToken r SET r.revogado = true WHERE r.user = :user AND r.active = true")
    void revokeAllTokensForUser(@Param("user") User user);

    @Query("SELECT r FROM RefreshToken r WHERE r.dataExpiracao < :agora AND r.active = true")
    List<RefreshToken> findExpiredTokens(@Param("agora") Instant agora);
}
