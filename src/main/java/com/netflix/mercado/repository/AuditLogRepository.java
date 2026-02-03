package com.netflix.mercado.repository;

import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE a.user = :user AND a.active = true ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.tipoEntidade = :tipoEntidade AND a.active = true ORDER BY a.createdAt DESC")
    Page<AuditLog> findByTipoEntidade(@Param("tipoEntidade") String tipoEntidade, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.acao = :acao AND a.active = true ORDER BY a.createdAt DESC")
    Page<AuditLog> findByAcao(@Param("acao") AuditLog.TipoAcao acao, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.tipoEntidade = :tipoEntidade AND a.idEntidade = :idEntidade AND a.active = true ORDER BY a.createdAt DESC")
    List<AuditLog> findHistoricoEntidade(@Param("tipoEntidade") String tipoEntidade, @Param("idEntidade") Long idEntidade);

    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :inicio AND :fim AND a.active = true")
    List<AuditLog> findByDataRange(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user = :user AND a.active = true")
    long countByUser(@Param("user") User user);

    @Query("SELECT a.acao as tipo, COUNT(a) as quantidade FROM AuditLog a WHERE a.createdAt BETWEEN :inicio AND :fim AND a.active = true GROUP BY a.acao")
    List<Object> findAtividadesPorTipo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
