package com.netflix.mercado.dto.recomendacao;

import com.netflix.mercado.dto.mercado.MercadoResponse;
import lombok.*;

/**
 * ✅ NOVO: DTO para resposta de recomendação de mercado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoRecomendacaoResponse {
    private MercadoResponse mercado;
    private Double pontuacao; // 0-100
    private String motivo;
}
