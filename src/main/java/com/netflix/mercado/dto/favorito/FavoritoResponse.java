package com.netflix.mercado.dto.favorito;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta com informações do favorito")
public class FavoritoResponse {

    @Schema(description = "ID do favorito", example = "1")
    private Long id;

    @Schema(description = "Informações do mercado favoritado")
    private MercadoResponse mercado;

    @Schema(description = "ID do usuário proprietário do favorito")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Observações sobre o mercado", example = "Compro aqui toda semana")
    private String observacoes;

    @Schema(description = "Prioridade do favorito (1-5)", example = "3")
    private Integer prioridade;

    @Schema(description = "Data de adição aos favoritos")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de última atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public FavoritoResponse() {
    }

    public FavoritoResponse(Long id, MercadoResponse mercado, Long usuarioId, String observacoes, Integer prioridade, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.mercado = mercado;
        this.usuarioId = usuarioId;
        this.observacoes = observacoes;
        this.prioridade = prioridade;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MercadoResponse getMercado() {
        return this.mercado;
    }

    public void setMercado(MercadoResponse mercado) {
        this.mercado = mercado;
    }

    public Long getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
