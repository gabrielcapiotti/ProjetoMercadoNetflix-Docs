package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Notificacao;
import com.netflix.mercado.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    @Query("SELECT n FROM Notificacao n WHERE n.user = :user AND n.active = true ORDER BY n.createdAt DESC")
    Page<Notificacao> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT n FROM Notificacao n WHERE n.user = :user AND n.lida = false AND n.active = true ORDER BY n.createdAt DESC")
    Page<Notificacao> findUnreadByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.user = :user AND n.lida = false AND n.active = true")
    long countUnreadByUser(@Param("user") User user);

    @Query("SELECT n FROM Notificacao n WHERE n.user = :user AND n.tipo = :tipo AND n.active = true ORDER BY n.createdAt DESC")
    List<Notificacao> findByUserAndTipo(@Param("user") User user, @Param("tipo") Notificacao.TipoNotificacao tipo);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.user = :user AND n.active = true")
    void markAllAsRead(@Param("user") User user);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.id = :id AND n.active = true")
    void markAsRead(@Param("id") Long id);

    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.user = :user AND n.active = true")
    long countByUser(@Param("user") User user);
}
