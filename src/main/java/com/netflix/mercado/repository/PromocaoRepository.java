package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Promocao;
import com.netflix.mercado.entity.Mercado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    @Query("SELECT p FROM Promocao p WHERE p.mercado = :mercado AND p.active = true ORDER BY p.dataValidade DESC")
    Page<Promocao> findByMercado(@Param("mercado") Mercado mercado, Pageable pageable);

    @Query("SELECT p FROM Promocao p WHERE p.codigo = :codigo AND p.active = true")
    Optional<Promocao> findByCodigo(@Param("codigo") String codigo);

    @Query("SELECT p FROM Promocao p WHERE p.ativa = true AND p.dataValidade > :agora AND p.active = true")
    List<Promocao> findActivePromocoes(@Param("agora") LocalDateTime agora);

    @Query("SELECT p FROM Promocao p WHERE p.mercado = :mercado AND p.ativa = true AND p.dataValidade > :agora AND p.active = true")
    Page<Promocao> findActiveByMercado(@Param("mercado") Mercado mercado,
                                        @Param("agora") LocalDateTime agora,
                                        Pageable pageable);

    @Query("SELECT COUNT(p) FROM Promocao p WHERE p.mercado = :mercado AND p.active = true")
    long countByMercado(@Param("mercado") Mercado mercado);

    @Query("SELECT p FROM Promocao p WHERE p.utilizacoesAtuais >= p.maxUtilizacoes AND p.active = true")
    List<Promocao> findExhaustedPromocoes();

    boolean existsByCodigo(String codigo);

    // Métodos por ID para facilitar uso
    Page<Promocao> findByMercadoId(Long mercadoId, Pageable pageable);
    
    @Query("SELECT p FROM Promocao p WHERE p.ativa = true AND p.active = true")
    Page<Promocao> findByAtivaTrue(Pageable pageable);
    
    @Query("SELECT p FROM Promocao p WHERE p.codigo = :codigo AND p.active = true")
    Optional<Promocao> findByCodigoPromocional(@Param("codigo") String codigo);
    
    @Query("UPDATE Promocao p SET p.ativa = false WHERE p.dataValidade < :agora AND p.active = true")
    long desativarPromocoesExpiradas(@Param("agora") LocalDateTime agora);
}
