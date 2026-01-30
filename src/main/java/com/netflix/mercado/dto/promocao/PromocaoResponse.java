package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações de promoção")
public class PromocaoResponse {

    @Schema(description = "ID da promoção", example = "1")
    private Long id;

    @Schema(description = "Código da promoção", example = "PROMO2024")
    private String codigo;

    @Schema(description = "Descrição da promoção", example = "Desconto especial em produtos selecionados")
    private String descricao;

    @Schema(description = "Percentual de desconto", example = "15.50")
    @JsonProperty("percentualDesconto")
    private BigDecimal percentualDesconto;

    @Schema(description = "Valor máximo de desconto", example = "50.00")
    @JsonProperty("valorDescontoMaximo")
    private BigDecimal valorDescontoMaximo;

    @Schema(description = "Valor mínimo de compra", example = "100.00")
    @JsonProperty("valorMinimoCompra")
    private BigDecimal valorMinimoCompra;

    @Schema(description = "Indica se a promoção está ativa", example = "true")
    private Boolean ativa;

    @Schema(description = "Total de utilizações", example = "45")
    @JsonProperty("utilizacoes")
    private Integer utilizacoes;

    @Schema(description = "Máximo de utilizações", example = "100")
    @JsonProperty("maxUtilizacoes")
    private Integer maxUtilizacoes;

    @Schema(description = "Data de início", example = "2025-01-30T00:00:00")
    @JsonProperty("dataInicio")
    private LocalDateTime dataInicio;

    @Schema(description = "Data de validade", example = "2025-12-31T23:59:59")
    @JsonProperty("dataValidade")
    private LocalDateTime dataValidade;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
