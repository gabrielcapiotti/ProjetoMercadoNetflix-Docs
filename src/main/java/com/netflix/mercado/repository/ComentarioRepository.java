package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Comentario;
import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query("SELECT c FROM Comentario c WHERE c.avaliacao = :avaliacao AND c.comentarioPai IS NULL AND c.active = true")
    Page<Comentario> findRootComentariosByAvaliacao(@Param("avaliacao") Avaliacao avaliacao, Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.avaliacao = :avaliacao AND c.active = true")
    Page<Comentario> findByAvaliacao(@Param("avaliacao") Avaliacao avaliacao, Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.user = :user AND c.active = true ORDER BY c.createdAt DESC")
    Page<Comentario> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.comentarioPai = :comentarioPai AND c.active = true")
    List<Comentario> findRespostasComComentario(@Param("comentarioPai") Comentario comentarioPai);

    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.avaliacao = :avaliacao AND c.active = true")
    long countByAvaliacao(@Param("avaliacao") Avaliacao avaliacao);

    @Query("SELECT c FROM Comentario c WHERE c.moderado = false AND c.active = true ORDER BY c.createdAt DESC")
    Page<Comentario> findUnmoderatedComentarios(Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.curtidas >= :minCurtidas AND c.active = true ORDER BY c.curtidas DESC")
    Page<Comentario> findMostLikedComentarios(@Param("minCurtidas") Long minCurtidas, Pageable pageable);

    // Métodos por ID para facilitar uso
    @Query("SELECT c FROM Comentario c WHERE c.avaliacao.id = :avaliacaoId AND c.comentarioPai IS NULL AND c.active = true")
    Page<Comentario> findByAvaliacaoIdAndComentarioPaiIsNull(@Param("avaliacaoId") Long avaliacaoId, Pageable pageable);
    
    Page<Comentario> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.comentarioPai.id = :comentarioPaiId AND c.active = true")
    Page<Comentario> findByComentarioPaiId(@Param("comentarioPaiId") Long comentarioPaiId, Pageable pageable);
}
