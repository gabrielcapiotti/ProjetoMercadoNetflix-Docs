package com.netflix.mercado.dto.relatorio;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ NOVO: DTO para Relatório Geral do Sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioGeralResponse {
    private LocalDateTime dataGeracao;
    private Long totalMercados;
    private Long totalAvaliacoes;
    private Long totalComentarios;
    private Long totalPromocoes;
    private BigDecimal mediaAvaliacoes;
    private String mercadoMelhorAvaliado;
    private BigDecimal avaliacaoMelhorMercado;
    private String mercadoMaisAvaliado;
    private Integer totalAvaliacoesMercadoMaisAvaliado;
}
