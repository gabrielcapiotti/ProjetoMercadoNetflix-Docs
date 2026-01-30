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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
