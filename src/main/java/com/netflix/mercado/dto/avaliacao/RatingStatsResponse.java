package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

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
    public RatingStatsResponse() {
    }

    public RatingStatsResponse(BigDecimal mediaEstrelas, Integer totalAvaliacoes, Map<Integer, Integer> distribuicaoPorEstrela, BigDecimal percentualCincoEstrelas, BigDecimal percentualQuatroEstrelas, BigDecimal percentualTresEstrelas, BigDecimal percentualDoisEstrelas, BigDecimal percentualUmaEstrela) {
        this.mediaEstrelas = mediaEstrelas;
        this.totalAvaliacoes = totalAvaliacoes;
        this.distribuicaoPorEstrela = distribuicaoPorEstrela;
        this.percentualCincoEstrelas = percentualCincoEstrelas;
        this.percentualQuatroEstrelas = percentualQuatroEstrelas;
        this.percentualTresEstrelas = percentualTresEstrelas;
        this.percentualDoisEstrelas = percentualDoisEstrelas;
        this.percentualUmaEstrela = percentualUmaEstrela;
    }

    public BigDecimal getMediaEstrelas() {
        return this.mediaEstrelas;
    }

    public void setMediaEstrelas(BigDecimal mediaEstrelas) {
        this.mediaEstrelas = mediaEstrelas;
    }

    public Integer getTotalAvaliacoes() {
        return this.totalAvaliacoes;
    }

    public void setTotalAvaliacoes(Integer totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public Map<Integer, Integer> getDistribuicaoPorEstrela() {
        return this.distribuicaoPorEstrela;
    }

    public void setDistribuicaoPorEstrela(Map<Integer, Integer> distribuicaoPorEstrela) {
        this.distribuicaoPorEstrela = distribuicaoPorEstrela;
    }

    public BigDecimal getPercentualCincoEstrelas() {
        return this.percentualCincoEstrelas;
    }

    public void setPercentualCincoEstrelas(BigDecimal percentualCincoEstrelas) {
        this.percentualCincoEstrelas = percentualCincoEstrelas;
    }

    public BigDecimal getPercentualQuatroEstrelas() {
        return this.percentualQuatroEstrelas;
    }

    public void setPercentualQuatroEstrelas(BigDecimal percentualQuatroEstrelas) {
        this.percentualQuatroEstrelas = percentualQuatroEstrelas;
    }

    public BigDecimal getPercentualTresEstrelas() {
        return this.percentualTresEstrelas;
    }

    public void setPercentualTresEstrelas(BigDecimal percentualTresEstrelas) {
        this.percentualTresEstrelas = percentualTresEstrelas;
    }

    public BigDecimal getPercentualDoisEstrelas() {
        return this.percentualDoisEstrelas;
    }

    public void setPercentualDoisEstrelas(BigDecimal percentualDoisEstrelas) {
        this.percentualDoisEstrelas = percentualDoisEstrelas;
    }

    public BigDecimal getPercentualUmaEstrela() {
        return this.percentualUmaEstrela;
    }

    public void setPercentualUmaEstrela(BigDecimal percentualUmaEstrela) {
        this.percentualUmaEstrela = percentualUmaEstrela;
    }

}
