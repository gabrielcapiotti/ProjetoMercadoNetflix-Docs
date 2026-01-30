package com.netflix.mercado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "favoritos", indexes = {
        @Index(name = "idx_favorito_usuario", columnList = "usuario_id"),
        @Index(name = "idx_favorito_mercado", columnList = "mercado_id"),
        @Index(name = "idx_favorito_unico", columnList = "usuario_id,mercado_id", unique = true),
        @Index(name = "idx_favorito_criada_em", columnList = "created_at")
}, uniqueConstraints = @UniqueConstraint(name = "uk_usuario_mercado", columnNames = {"usuario_id", "mercado_id"}))
public class Favorito extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorito_usuario"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorito_mercado"))
    private Mercado mercado;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "prioridade", nullable = false)
    private Integer prioridade = 0;
    public Favorito() {
    }

    public Favorito(User user, Mercado mercado, String observacoes, Integer prioridade) {
        this.user = user;
        this.mercado = mercado;
        this.observacoes = observacoes;
        this.prioridade = prioridade;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mercado getMercado() {
        return this.mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public String getObservacoes() {
        return this.observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Integer getPrioridade() {
        return this.prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

}
