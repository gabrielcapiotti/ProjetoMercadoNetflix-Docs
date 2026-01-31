package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Resposta com estatísticas de avaliações")
public class RatingStatsResponse {

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Média de avaliações (0-5)", example = "4.35")
    @JsonProperty("mediaAvaliacoes")
    private Double mediaAvaliacoes;

    @Schema(description = "Quantidade de avaliações com 5 estrelas", example = "19")
    @JsonProperty("cincoEstrelas")
    private Long cincoEstrelas;

    @Schema(description = "Quantidade de avaliações com 4 estrelas", example = "12")
    @JsonProperty("quatroEstrelas")
    private Long quatroEstrelas;

    @Schema(description = "Quantidade de avaliações com 3 estrelas", example = "6")
    @JsonProperty("tresEstrelas")
    private Long tresEstrelas;

    @Schema(description = "Quantidade de avaliações com 2 estrelas", example = "3")
    @JsonProperty("doisEstrelas")
    private Long doisEstrelas;

    @Schema(description = "Quantidade de avaliações com 1 estrela", example = "2")
    @JsonProperty("umEstrela")
    private Long umEstrela;
}
