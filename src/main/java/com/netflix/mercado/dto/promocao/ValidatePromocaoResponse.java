package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta da validação de promoção")
public class ValidatePromocaoResponse {

    @Schema(description = "Indica se a promoção é válida", example = "true")
    private Boolean valida;

    @Schema(description = "Valor do desconto a ser aplicado", example = "15.00")
    private BigDecimal desconto;

    @Schema(description = "Mensagem sobre a validação", example = "Promoção válida e disponível")
    private String mensagem;

    @Schema(description = "Motivo se a promoção for inválida", example = "Promoção expirada")
    private String motivo;

    @Schema(description = "Utilizações restantes", example = "55")
    @JsonProperty("utilizacoesRestantes")
    private Integer utilizacoesRestantes;
}
