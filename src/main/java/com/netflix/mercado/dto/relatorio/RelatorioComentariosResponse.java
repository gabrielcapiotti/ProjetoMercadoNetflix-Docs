package com.netflix.mercado.dto.relatorio;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ NOVO: DTO para Relatório de Qualidade de Comentários
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioComentariosResponse {
    private LocalDateTime dataGeracao;
    private Long totalComentarios;
    private Long comentariosAtivos;
    private Long comentariosInativos;
    private Long comentariosAguardandoModeração;
    private BigDecimal percentualAtivos;
    private BigDecimal mediaCurtidas;
    private String comentarioMaisCurtido;
}
