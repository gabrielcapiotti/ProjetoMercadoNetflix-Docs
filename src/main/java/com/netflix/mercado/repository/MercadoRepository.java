package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Mercado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long> {

    Optional<Mercado> findByCnpj(String cnpj);

    Optional<Mercado> findByEmail(String email);

    @Query("SELECT m FROM Mercado m WHERE m.active = true")
    Page<Mercado> findAllActive(Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.active = true AND m.nome LIKE %:nome%")
    Page<Mercado> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.active = true AND m.cidade = :cidade")
    Page<Mercado> findByCidade(@Param("cidade") String cidade, Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.active = true ORDER BY m.avaliacaoMedia DESC")
    Page<Mercado> findByAvaliacaoMedia(Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.active = true AND m.avaliacaoMedia >= :minAvaliacao")
    Page<Mercado> findByAvaliacaoMediaGreaterThanEqual(@Param("minAvaliacao") BigDecimal minAvaliacao, Pageable pageable);

    @Query(value = "SELECT * FROM mercados m WHERE m.active = true AND " +
            "SQRT(POW(m.latitude - :latitude, 2) + POW(m.longitude - :longitude, 2)) * 111 <= :raio " +
            "ORDER BY SQRT(POW(m.latitude - :latitude, 2) + POW(m.longitude - :longitude, 2)) * 111",
            nativeQuery = true)
    List<Mercado> findByProximidade(@Param("latitude") double latitude,
                                     @Param("longitude") double longitude,
                                     @Param("raio") double raio);

    @Query("SELECT COUNT(m) FROM Mercado m WHERE m.active = true")
    long countActiveMarkets();

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
