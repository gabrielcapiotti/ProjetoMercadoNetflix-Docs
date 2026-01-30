package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com estatísticas de avaliações")
public class RatingStatsResponse {

    @Schema(description = "Média de estrelas (0-5)", example = "4.35")
    @JsonProperty("mediaEstrelas")
    private BigDecimal mediaEstrelas;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Distribuição de avaliações por estrela")
    @JsonProperty("distribuicaoPorEstrela")
    private Map<Integer, Integer> distribuicaoPorEstrela;

    @Schema(description = "Percentual de avaliações com 5 estrelas", example = "45.24")
    @JsonProperty("percentualCincoEstrelas")
    private BigDecimal percentualCincoEstrelas;

    @Schema(description = "Percentual de avaliações com 4 estrelas", example = "28.57")
    @JsonProperty("percentualQuatroEstrelas")
    private BigDecimal percentualQuatroEstrelas;

    @Schema(description = "Percentual de avaliações com 3 estrelas", example = "14.29")
    @JsonProperty("percentualTresEstrelas")
    private BigDecimal percentualTresEstrelas;

    @Schema(description = "Percentual de avaliações com 2 estrelas", example = "7.14")
    @JsonProperty("percentualDoisEstrelas")
    private BigDecimal percentualDoisEstrelas;

    @Schema(description = "Percentual de avaliações com 1 estrela", example = "4.76")
    @JsonProperty("percentualUmaEstrela")
    private BigDecimal percentualUmaEstrela;
}
