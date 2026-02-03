package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Estatísticas de uma promoção")
public class PromocaoStatisticsResponse {

    @JsonProperty("promocaoId")
    @Schema(description = "ID da promoção", example = "1")
    private Long promocaoId;

    @JsonProperty("codigo")
    @Schema(description = "Código da promoção", example = "PROMO2024")
    private String codigo;

    @JsonProperty("ativa")
    @Schema(description = "Se a promoção está ativa e válida", example = "true")
    private Boolean ativa;

    @JsonProperty("utilizacoesAtuais")
    @Schema(description = "Número de vezes já utilizada", example = "45")
    private Long utilizacoesAtuais;

    @JsonProperty("maxUtilizacoes")
    @Schema(description = "Limite máximo de utilizações", example = "100")
    private Long maxUtilizacoes;

    @JsonProperty("utilizacoesRestantes")
    @Schema(description = "Quantas vezes ainda pode ser usada", example = "55")
    private Long utilizacoesRestantes;

    @JsonProperty("percentualUso")
    @Schema(description = "Percentual da promoção já utilizado", example = "45.00")
    private Double percentualUso;

    @JsonProperty("dataValidade")
    @Schema(description = "Data de expiração da promoção", example = "2026-03-03T23:59:59")
    private LocalDateTime dataValidade;

    @JsonProperty("diasAtéExpiração")
    @Schema(description = "Dias restantes até expiração", example = "28")
    private Long diasAtéExpiração;

    @JsonProperty("percentualDesconto")
    @Schema(description = "Percentual de desconto oferecido", example = "15.50")
    private BigDecimal percentualDesconto;

    @JsonProperty("valorDescontoMaximo")
    @Schema(description = "Valor máximo de desconto", example = "50.00")
    private BigDecimal valorDescontoMaximo;
}
