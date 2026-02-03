package com.netflix.mercado.dto.relatorio;

import lombok.*;
import java.math.BigDecimal;

/**
 * ✅ NOVO: DTO para Mercados com Poucas Avaliações
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPoucasAvaliacoesResponse {
    private Long mercadoId;
    private String nome;
    private String cidade;
    private String estado;
    private Long totalAvaliacoes;
    private BigDecimal avaliacaoMedia;
}
