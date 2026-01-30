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

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notificacoes", indexes = {
        @Index(name = "idx_notificacao_usuario", columnList = "usuario_id"),
        @Index(name = "idx_notificacao_lida", columnList = "lida"),
        @Index(name = "idx_notificacao_tipo", columnList = "tipo"),
        @Index(name = "idx_notificacao_criada_em", columnList = "created_at")
})
public class Notificacao extends BaseEntity {

    public enum TipoNotificacao {
        SISTEMA("Sistema"),
        PROMOCAO("Promoção"),
        AVALIACAO("Avaliação"),
        COMENTARIO("Comentário"),
        MERCADO("Mercado");

        private final String descricao;

        TipoNotificacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notificacao_usuario"))
    private User user;

    @NotBlank(message = "O título é obrigatório")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "O conteúdo é obrigatório")
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoNotificacao tipo;

    @Column(name = "lida", nullable = false)
    private Boolean lida = false;

    @Column(name = "url_acao", length = 500)
    private String urlAcao;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    public void marcarComoLida() {
        this.lida = true;
        this.dataLeitura = LocalDateTime.now();
    }

    public void marcarComoNaoLida() {
        this.lida = false;
        this.dataLeitura = null;
    }
    public Notificacao() {
    }

    public Notificacao(User user, String titulo, String conteudo, TipoNotificacao tipo, Boolean lida, String urlAcao, LocalDateTime dataLeitura) {
        this.user = user;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.lida = lida;
        this.urlAcao = urlAcao;
        this.dataLeitura = dataLeitura;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return this.conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public TipoNotificacao getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoNotificacao tipo) {
        this.tipo = tipo;
    }

    public Boolean getLida() {
        return this.lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public String getUrlAcao() {
        return this.urlAcao;
    }

    public void setUrlAcao(String urlAcao) {
        this.urlAcao = urlAcao;
    }

    public LocalDateTime getDataLeitura() {
        return this.dataLeitura;
    }

    public void setDataLeitura(LocalDateTime dataLeitura) {
        this.dataLeitura = dataLeitura;
    }

}
