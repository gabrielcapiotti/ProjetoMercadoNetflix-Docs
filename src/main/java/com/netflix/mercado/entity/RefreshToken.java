package com.netflix.mercado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_usuario", columnList = "usuario_id"),
        @Index(name = "idx_refresh_token_token", columnList = "token", unique = true),
        @Index(name = "idx_refresh_token_expiracao", columnList = "data_expiracao")
})
public class RefreshToken extends BaseEntity {

    @NotBlank
    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_usuario"))
    private User user;

    @Column(name = "data_expiracao", nullable = false)
    private Instant dataExpiracao;

    @Column(name = "revogado", nullable = false)
    private Boolean revogado = false;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    public boolean ehValido() {
        return !revogado && Instant.now().isBefore(dataExpiracao);
    }

    public void revogar() {
        this.revogado = true;
    }
    public RefreshToken() {
    }

    public RefreshToken(String token, User user, Instant dataExpiracao, Boolean revogado, String ipOrigem, String userAgent) {
        this.token = token;
        this.user = user;
        this.dataExpiracao = dataExpiracao;
        this.revogado = revogado;
        this.ipOrigem = ipOrigem;
        this.userAgent = userAgent;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getDataExpiracao() {
        return this.dataExpiracao;
    }

    public void setDataExpiracao(Instant dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Boolean getRevogado() {
        return this.revogado;
    }

    public void setRevogado(Boolean revogado) {
        this.revogado = revogado;
    }

    public String getIpOrigem() {
        return this.ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
