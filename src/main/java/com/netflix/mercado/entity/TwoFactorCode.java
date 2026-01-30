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

import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_codes", indexes = {
        @Index(name = "idx_2fa_usuario", columnList = "usuario_id"),
        @Index(name = "idx_2fa_codigo", columnList = "codigo", unique = true),
        @Index(name = "idx_2fa_expiracao", columnList = "data_expiracao")
})
public class TwoFactorCode extends BaseEntity {

    @NotBlank(message = "O código é obrigatório")
    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_2fa_usuario"))
    private User user;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(name = "utilizado", nullable = false)
    private Boolean utilizado = false;

    @Column(name = "data_utilizacao")
    private LocalDateTime dataUtilizacao;

    @Column(name = "metodo_envio", nullable = false, length = 20)
    private String metodoEnvio;

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "max_tentativas", nullable = false)
    private Integer maxTentativas = 3;

    public boolean ehValido() {
        return !utilizado && LocalDateTime.now().isBefore(dataExpiracao) && tentativas < maxTentativas;
    }

    public void marcarUtilizado() {
        this.utilizado = true;
        this.dataUtilizacao = LocalDateTime.now();
    }

    public void incrementarTentativas() {
        this.tentativas++;
    }

    public boolean podeReusar() {
        return this.tentativas < this.maxTentativas;
    }
    public TwoFactorCode() {
    }

    public TwoFactorCode(String codigo, User user, LocalDateTime dataExpiracao, Boolean utilizado, LocalDateTime dataUtilizacao, String metodoEnvio, Integer tentativas, Integer maxTentativas) {
        this.codigo = codigo;
        this.user = user;
        this.dataExpiracao = dataExpiracao;
        this.utilizado = utilizado;
        this.dataUtilizacao = dataUtilizacao;
        this.metodoEnvio = metodoEnvio;
        this.tentativas = tentativas;
        this.maxTentativas = maxTentativas;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDataExpiracao() {
        return this.dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Boolean getUtilizado() {
        return this.utilizado;
    }

    public void setUtilizado(Boolean utilizado) {
        this.utilizado = utilizado;
    }

    public LocalDateTime getDataUtilizacao() {
        return this.dataUtilizacao;
    }

    public void setDataUtilizacao(LocalDateTime dataUtilizacao) {
        this.dataUtilizacao = dataUtilizacao;
    }

    public String getMetodoEnvio() {
        return this.metodoEnvio;
    }

    public void setMetodoEnvio(String metodoEnvio) {
        this.metodoEnvio = metodoEnvio;
    }

    public Integer getTentativas() {
        return this.tentativas;
    }

    public void setTentativas(Integer tentativas) {
        this.tentativas = tentativas;
    }

    public Integer getMaxTentativas() {
        return this.maxTentativas;
    }

    public void setMaxTentativas(Integer maxTentativas) {
        this.maxTentativas = maxTentativas;
    }

}
