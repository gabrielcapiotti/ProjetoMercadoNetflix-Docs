package com.netflix.mercado.repository;

import com.netflix.mercado.entity.HorarioFuncionamento;
import com.netflix.mercado.entity.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {

    @Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado = :mercado AND h.active = true ORDER BY h.diaSemana")
    List<HorarioFuncionamento> findByMercado(@Param("mercado") Mercado mercado);

    @Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado = :mercado AND h.diaSemana = :diaSemana AND h.active = true")
    Optional<HorarioFuncionamento> findByMercadoAndDia(@Param("mercado") Mercado mercado,
                                                        @Param("diaSemana") HorarioFuncionamento.DiaSemana diaSemana);

    @Query("SELECT COUNT(h) FROM HorarioFuncionamento h WHERE h.mercado = :mercado AND h.active = true")
    long countByMercado(@Param("mercado") Mercado mercado);

    @Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado = :mercado AND h.aberto = true AND h.active = true")
    List<HorarioFuncionamento> findOpenHorariosByMercado(@Param("mercado") Mercado mercado);
}
