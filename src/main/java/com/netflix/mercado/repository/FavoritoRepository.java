package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Favorito;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    @Query("SELECT f FROM Favorito f WHERE f.user = :user AND f.active = true ORDER BY f.prioridade DESC, f.createdAt DESC")
    Page<Favorito> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT f FROM Favorito f WHERE f.user = :user AND f.mercado = :mercado AND f.active = true")
    Optional<Favorito> findByUserAndMercado(@Param("user") User user, @Param("mercado") Mercado mercado);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorito f " +
            "WHERE f.user = :user AND f.mercado = :mercado AND f.active = true")
    boolean existsByUserAndMercado(@Param("user") User user, @Param("mercado") Mercado mercado);

    @Query("SELECT COUNT(f) FROM Favorito f WHERE f.user = :user AND f.active = true")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(f) FROM Favorito f WHERE f.mercado = :mercado AND f.active = true")
    long countByMercado(@Param("mercado") Mercado mercado);

    @Query("SELECT f FROM Favorito f WHERE f.mercado = :mercado AND f.active = true")
    Page<Favorito> findByMercado(@Param("mercado") Mercado mercado, Pageable pageable);
}
