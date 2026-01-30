package com.netflix.mercado.dto.promocao;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualizar promoção")
public class UpdatePromocaoRequest {

    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    @Schema(description = "Descrição da promoção", example = "Desconto especial em produtos selecionados")
    private String descricao;

    @DecimalMin(value = "0.0", inclusive = false, message = "Desconto deve ser maior que 0")
    @DecimalMax(value = "100.0", message = "Desconto não pode ser maior que 100%")
    @Schema(description = "Percentual de desconto (0-100)", example = "20.00")
    @JsonProperty("percentualDesconto")
    private BigDecimal percentualDesconto;

    @DecimalMin(value = "0.0", message = "Valor máximo de desconto não pode ser negativo")
    @Schema(description = "Valor máximo de desconto permitido", example = "75.00")
    @JsonProperty("valorDescontoMaximo")
    private BigDecimal valorDescontoMaximo;

    @DecimalMin(value = "0.0", message = "Valor mínimo de compra não pode ser negativo")
    @Schema(description = "Valor mínimo de compra para usar promoção", example = "150.00")
    @JsonProperty("valorMinimoCompra")
    private BigDecimal valorMinimoCompra;

    @Future(message = "Data de validade deve ser no futuro")
    @Schema(description = "Data de validade da promoção", example = "2025-12-31T23:59:59")
    @JsonProperty("dataValidade")
    private LocalDateTime dataValidade;

    @Min(value = 1, message = "Máximo de utilizações deve ser no mínimo 1")
    @Schema(description = "Máximo de vezes que a promoção pode ser utilizada", example = "200")
    @JsonProperty("maxUtilizacoes")
    private Integer maxUtilizacoes;
}
