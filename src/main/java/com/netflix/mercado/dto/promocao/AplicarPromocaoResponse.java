package com.netflix.mercado.dto.promocao;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ NOVO: DTO para resposta de aplicação de promoção
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicarPromocaoResponse {
    private Long promocaoId;
    private String codigoPromocao;
    private BigDecimal valorOriginal;
    private BigDecimal desconto;
    private BigDecimal percentualDesconto;
    private BigDecimal valorFinal;
    private BigDecimal economia; // percentual de economia
    private LocalDateTime dataExpiracao;
    private Long utilizacaoRestante;
}
