package com.netflix.mercado.dto.promocao;

import lombok.*;
import java.math.BigDecimal;

/**
 * ✅ NOVO: DTO para requisição de aplicação de promoção
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicarPromocaoRequest {
    private String codigoPromocao;
    private BigDecimal valorCompra;
}
