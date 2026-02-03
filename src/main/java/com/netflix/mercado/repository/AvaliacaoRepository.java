package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado = :mercado AND a.active = true")
    Page<Avaliacao> findByMercado(@Param("mercado") Mercado mercado, Pageable pageable);

    @Query("SELECT a FROM Avaliacao a WHERE a.user = :user AND a.active = true")
    Page<Avaliacao> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado = :mercado AND a.user = :user AND a.active = true")
    Optional<Avaliacao> findByMercadoAndUser(@Param("mercado") Mercado mercado, @Param("user") User user);

    @Query("SELECT AVG(a.estrelas) FROM Avaliacao a WHERE a.mercado = :mercado AND a.active = true")
    BigDecimal calcularMediaAvaliacoes(@Param("mercado") Mercado mercado);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.mercado = :mercado AND a.active = true")
    long countByMercado(@Param("mercado") Mercado mercado);

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado = :mercado AND a.estrelas >= :estrelas AND a.active = true")
    Page<Avaliacao> findByMercadoAndEstrelasGreaterThanEqual(@Param("mercado") Mercado mercado,
                                                               @Param("estrelas") Integer estrelas,
                                                               Pageable pageable);

    @Query("SELECT a FROM Avaliacao a WHERE a.verificado = false AND a.active = true ORDER BY a.createdAt DESC")
    Page<Avaliacao> findUnverifiedAvaliacoes(Pageable pageable);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.user = :user AND a.mercado = :mercado AND a.active = true")
    long countByUserAndMercado(@Param("user") User user, @Param("mercado") Mercado mercado);

    // Métodos por ID para facilitar uso
    Page<Avaliacao> findByMercadoId(Long mercadoId, Pageable pageable);
    
    Page<Avaliacao> findByUserId(Long userId, Pageable pageable);
    
    long countByMercadoIdAndEstrelas(Long mercadoId, int estrelas);
    
    @Query("SELECT AVG(a.estrelas) FROM Avaliacao a WHERE a.mercado.id = :mercadoId AND a.active = true")
    Double findAverageEstrelasByMercadoId(@Param("mercadoId") Long mercadoId);
    
    boolean existsByMercadoIdAndUserId(Long mercadoId, Long userId);

    long countByMercadoId(Long mercadoId);
}
