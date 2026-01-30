package com.netflix.mercado.dto.notificacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com estatísticas de notificações")
public class NotificacaoStatsResponse {

    @Schema(description = "Total de notificações do usuário", example = "15")
    @JsonProperty("totalNotificacoes")
    private Integer totalNotificacoes;

    @Schema(description = "Total de notificações não lidas", example = "5")
    @JsonProperty("naoLidas")
    private Integer naoLidas;

    @Schema(description = "Total de notificações lidas", example = "10")
    @JsonProperty("lidas")
    private Integer lidas;

    @Schema(description = "Notificações não lidas nos últimos 7 dias", example = "3")
    @JsonProperty("naoLidasUltimos7Dias")
    private Integer naoLidasUltimos7Dias;
}
