package com.netflix.mercado.dto.horario;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resposta com status de funcionamento do mercado")
public class MercadoStatusResponse {

    @Schema(description = "Indica se o mercado está aberto neste momento", example = "true")
    private Boolean aberto;

    @Schema(description = "Próximo horário de abertura", example = "Amanhã às 08:00")
    @JsonProperty("proximaAbertura")
    private String proximaAbertura;

    @Schema(description = "Próximo horário de fechamento", example = "Hoje às 20:00")
    @JsonProperty("proximoFechamento")
    private String proximoFechamento;

    @Schema(description = "Lista de horários para hoje")
    @JsonProperty("horariosHoje")
    private List<HorarioResponse> horariosHoje;

    @Schema(description = "Mensagem de status", example = "Aberto - Fecha às 20:00")
    private String mensagem;
    public MercadoStatusResponse() {
    }

    public MercadoStatusResponse(Boolean aberto, String proximaAbertura, String proximoFechamento, List<HorarioResponse> horariosHoje, String mensagem) {
        this.aberto = aberto;
        this.proximaAbertura = proximaAbertura;
        this.proximoFechamento = proximoFechamento;
        this.horariosHoje = horariosHoje;
        this.mensagem = mensagem;
    }

    public Boolean getAberto() {
        return this.aberto;
    }

    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }

    public String getProximaAbertura() {
        return this.proximaAbertura;
    }

    public void setProximaAbertura(String proximaAbertura) {
        this.proximaAbertura = proximaAbertura;
    }

    public String getProximoFechamento() {
        return this.proximoFechamento;
    }

    public void setProximoFechamento(String proximoFechamento) {
        this.proximoFechamento = proximoFechamento;
    }

    public List<HorarioResponse> getHorariosHoje() {
        return this.horariosHoje;
    }

    public void setHorariosHoje(List<HorarioResponse> horariosHoje) {
        this.horariosHoje = horariosHoje;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

}
