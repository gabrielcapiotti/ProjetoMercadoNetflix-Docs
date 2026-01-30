package com.netflix.mercado.dto.notificacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

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
    public NotificacaoStatsResponse() {
    }

    public NotificacaoStatsResponse(Integer totalNotificacoes, Integer naoLidas, Integer lidas, Integer naoLidasUltimos7Dias) {
        this.totalNotificacoes = totalNotificacoes;
        this.naoLidas = naoLidas;
        this.lidas = lidas;
        this.naoLidasUltimos7Dias = naoLidasUltimos7Dias;
    }

    public Integer getTotalNotificacoes() {
        return this.totalNotificacoes;
    }

    public void setTotalNotificacoes(Integer totalNotificacoes) {
        this.totalNotificacoes = totalNotificacoes;
    }

    public Integer getNaoLidas() {
        return this.naoLidas;
    }

    public void setNaoLidas(Integer naoLidas) {
        this.naoLidas = naoLidas;
    }

    public Integer getLidas() {
        return this.lidas;
    }

    public void setLidas(Integer lidas) {
        this.lidas = lidas;
    }

    public Integer getNaoLidasUltimos7Dias() {
        return this.naoLidasUltimos7Dias;
    }

    public void setNaoLidasUltimos7Dias(Integer naoLidasUltimos7Dias) {
        this.naoLidasUltimos7Dias = naoLidasUltimos7Dias;
    }

}
