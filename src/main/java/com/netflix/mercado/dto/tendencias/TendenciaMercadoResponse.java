package com.netflix.mercado.dto.tendencias;

import lombok.*;
import java.math.BigDecimal;

/**
 * ✅ NOVO: DTO para tendência de um mercado específico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TendenciaMercadoResponse {
    private Long mercadoId;
    private String nomeMercado;
    private String cidade;
    private String estado;
    private BigDecimal avaliacaoMedia;
    private Long totalAvaliacoes;
    private BigDecimal crescimento; // percentual de crescimento
    private String tendencia; // ALTA, ESTÁVEL, BAIXA
}
