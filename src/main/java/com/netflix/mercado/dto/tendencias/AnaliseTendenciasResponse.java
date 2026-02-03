package com.netflix.mercado.dto.tendencias;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ NOVO: DTO para resposta de análise de tendências
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseTendenciasResponse {
    private LocalDateTime dataAnalise;
    private BigDecimal crescimentoMedio;
    private Long mercadosEmAlta;
    private Long totalMercados;
    private List<TendenciaMercadoResponse> topCrescimento;
    private List<TendenciaMercadoResponse> topDeclinio;
}
