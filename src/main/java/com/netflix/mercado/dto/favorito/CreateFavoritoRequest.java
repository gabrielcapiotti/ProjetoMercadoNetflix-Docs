package com.netflix.mercado.dto.favorito;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(description = "Requisição para adicionar mercado aos favoritos")
public class CreateFavoritoRequest {

    @NotNull(message = "ID do mercado não pode ser nulo")
    @Schema(description = "ID do mercado a ser favoritado", example = "1")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o mercado (opcional)", example = "Compro aqui toda semana")
    private String observacoes;

    @Schema(description = "Prioridade do favorito (1-5, sendo 1 menor)", example = "3")
    private Integer prioridade;
    public CreateFavoritoRequest() {
    }

    public CreateFavoritoRequest(Long mercadoId, String observacoes, Integer prioridade) {
        this.mercadoId = mercadoId;
        this.observacoes = observacoes;
        this.prioridade = prioridade;
    }

    public Long getMercadoId() {
        return this.mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
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
