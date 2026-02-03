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

    @Schema(description = "Percentual de aprovação (4-5 estrelas)", example = "73.81")
    @JsonProperty("percentualAprovacao")
    private Double percentualAprovacao;

    @Schema(description = "Percentual de 5 estrelas", example = "45.24")
    @JsonProperty("percentualCincoEstrelas")
    private Double percentualCincoEstrelas;

    @Schema(description = "Percentual de 4 estrelas", example = "28.57")
    @JsonProperty("percentualQuatroEstrelas")
    private Double percentualQuatroEstrelas;

    @Schema(description = "Percentual de 3 estrelas", example = "14.29")
    @JsonProperty("percentualTresEstrelas")
    private Double percentualTresEstrelas;

    @Schema(description = "Percentual de 2 estrelas", example = "7.14")
    @JsonProperty("percentualDoisEstrelas")
    private Double percentualDoisEstrelas;

    @Schema(description = "Percentual de 1 estrela", example = "4.76")
    @JsonProperty("percentualUmEstrela")
    private Double percentualUmEstrela;
}
