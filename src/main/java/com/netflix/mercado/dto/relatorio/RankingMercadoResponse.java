package com.netflix.mercado.dto.relatorio;

import lombok.*;
import java.math.BigDecimal;

/**
 * ✅ NOVO: DTO para Item do Ranking de Mercados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingMercadoResponse {
    private Integer posicao;
    private String nome;
    private String cidade;
    private String estado;
    private BigDecimal avaliacaoMedia;
    private Long totalAvaliacoes;
}
