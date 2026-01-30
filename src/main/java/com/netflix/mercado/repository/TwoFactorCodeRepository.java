package com.netflix.mercado.repository;

import com.netflix.mercado.entity.TwoFactorCode;
import com.netflix.mercado.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {

    @Query("SELECT t FROM TwoFactorCode t WHERE t.codigo = :codigo AND t.active = true")
    Optional<TwoFactorCode> findByCodigo(@Param("codigo") String codigo);

    @Query("SELECT t FROM TwoFactorCode t WHERE t.user = :user AND t.utilizado = false AND t.active = true ORDER BY t.dataExpiracao DESC")
    List<TwoFactorCode> findValidCodigosByUser(@Param("user") User user);

    @Query("SELECT t FROM TwoFactorCode t WHERE t.user = :user AND t.dataExpiracao > :agora AND t.utilizado = false AND t.active = true")
    Optional<TwoFactorCode> findLatestValidCodigoForUser(@Param("user") User user, @Param("agora") LocalDateTime agora);

    @Query("SELECT t FROM TwoFactorCode t WHERE t.dataExpiracao < :agora AND t.active = true")
    List<TwoFactorCode> findExpiredCodigos(@Param("agora") LocalDateTime agora);

    @Query("SELECT COUNT(t) FROM TwoFactorCode t WHERE t.user = :user AND t.utilizado = false AND t.active = true")
    long countValidCodigosByUser(@Param("user") User user);

    boolean existsByCodigo(String codigo);
}
