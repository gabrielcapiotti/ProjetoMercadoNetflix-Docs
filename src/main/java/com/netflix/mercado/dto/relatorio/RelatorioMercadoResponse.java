package com.netflix.mercado.dto.relatorio;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ NOVO: DTO para Relatório de Performance de um Mercado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioMercadoResponse {
    private Long mercadoId;
    private String nomeMercado;
    private LocalDateTime dataGeracao;
    private BigDecimal avaliacaoMedia;
    private Long totalAvaliacoes;
    private Long totalComentarios;
    private Long totalPromocoesAtivas;
    private Map<Integer, Long> distribuicaoEstrelas = new HashMap<>();
    private Boolean ativo;
}
