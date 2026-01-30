package com.netflix.mercado.dto.horario;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(description = "Requisição para atualizar horário de funcionamento")
public class UpdateHorarioRequest {

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "Hora de abertura deve estar no formato HH:mm")
    @Schema(description = "Hora de abertura", example = "09:00")
    @JsonProperty("horaAbertura")
    private String horaAbertura;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "Hora de fechamento deve estar no formato HH:mm")
    @Schema(description = "Hora de fechamento", example = "21:00")
    @JsonProperty("horaFechamento")
    private String horaFechamento;

    @Schema(description = "Indica se o mercado funciona neste dia", example = "true")
    private Boolean aberto;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o horário", example = "Encerra mais cedo nos finais de semana")
    private String observacoes;
    public UpdateHorarioRequest() {
    }

    public UpdateHorarioRequest(String horaAbertura, String horaFechamento, Boolean aberto, String observacoes) {
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.aberto = aberto;
        this.observacoes = observacoes;
    }

    public String getHoraAbertura() {
        return this.horaAbertura;
    }

    public void setHoraAbertura(String horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public String getHoraFechamento() {
        return this.horaFechamento;
    }

    public void setHoraFechamento(String horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    public Boolean getAberto() {
        return this.aberto;
    }

    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }

    public String getObservacoes() {
        return this.observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}
