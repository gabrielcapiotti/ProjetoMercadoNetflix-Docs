package com.netflix.mercado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_usuario", columnList = "usuario_id"),
        @Index(name = "idx_audit_acao", columnList = "acao"),
        @Index(name = "idx_audit_entidade", columnList = "tipo_entidade"),
        @Index(name = "idx_audit_criada_em", columnList = "created_at")
})
public class AuditLog extends BaseEntity {

    public enum TipoAcao {
        CRIACAO, ATUALIZACAO, DELECAO, VISUALIZACAO, LOGIN, LOGOUT, ERRO
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_audit_usuario"))
    private User user;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "acao", nullable = false, length = 50)
    private TipoAcao acao;

    @NotBlank
    @Column(name = "tipo_entidade", nullable = false, length = 100)
    private String tipoEntidade;

    @Column(name = "id_entidade")
    private Long idEntidade;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "valores_anteriores", columnDefinition = "TEXT")
    private String valoresAnteriores;

    @Column(name = "valores_novos", columnDefinition = "TEXT")
    private String valoresNovos;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "status_http")
    private Integer statusHttp;
}
