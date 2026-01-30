package com.netflix.mercado.dto.notificacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta com informações de notificação")
public class NotificacaoResponse {

    @Schema(description = "ID da notificação", example = "1")
    private Long id;

    @Schema(description = "Título da notificação", example = "Nova promoção disponível")
    private String titulo;

    @Schema(description = "Conteúdo da notificação", example = "Confira a nova promoção no Mercado Central")
    private String conteudo;

    @Schema(description = "Tipo de notificação", example = "PROMOCAO")
    private String tipo;

    @Schema(description = "Indica se a notificação foi lida", example = "false")
    private Boolean lida;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de leitura")
    @JsonProperty("readAt")
    private LocalDateTime readAt;
    public NotificacaoResponse() {
    }

    public NotificacaoResponse(Long id, String titulo, String conteudo, String tipo, Boolean lida, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.lida = lida;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getLida() {
        return this.lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return this.readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

}
