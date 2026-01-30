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
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
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
    public AuditLog() {
    }

    public AuditLog(User user, TipoAcao acao, String tipoEntidade, Long idEntidade, String descricao, String valoresAnteriores, String valoresNovos, String ipOrigem, String userAgent, Integer statusHttp) {
        this.user = user;
        this.acao = acao;
        this.tipoEntidade = tipoEntidade;
        this.idEntidade = idEntidade;
        this.descricao = descricao;
        this.valoresAnteriores = valoresAnteriores;
        this.valoresNovos = valoresNovos;
        this.ipOrigem = ipOrigem;
        this.userAgent = userAgent;
        this.statusHttp = statusHttp;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TipoAcao getAcao() {
        return this.acao;
    }

    public void setAcao(TipoAcao acao) {
        this.acao = acao;
    }

    public String getTipoEntidade() {
        return this.tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public Long getIdEntidade() {
        return this.idEntidade;
    }

    public void setIdEntidade(Long idEntidade) {
        this.idEntidade = idEntidade;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValoresAnteriores() {
        return this.valoresAnteriores;
    }

    public void setValoresAnteriores(String valoresAnteriores) {
        this.valoresAnteriores = valoresAnteriores;
    }

    public String getValoresNovos() {
        return this.valoresNovos;
    }

    public void setValoresNovos(String valoresNovos) {
        this.valoresNovos = valoresNovos;
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

    public Integer getStatusHttp() {
        return this.statusHttp;
    }

    public void setStatusHttp(Integer statusHttp) {
        this.statusHttp = statusHttp;
    }

}
