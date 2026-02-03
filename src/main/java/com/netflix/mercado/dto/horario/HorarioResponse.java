package com.netflix.mercado.dto.horario;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta com informações de horário de funcionamento")
public class HorarioResponse {

    @Schema(description = "ID do horário", example = "1")
    private Long id;

    @Schema(description = "Dia da semana", example = "SEGUNDA")
    @JsonProperty("diaSemana")
    private String diaSemana;

    @Schema(description = "Hora de abertura", example = "08:00")
    @JsonProperty("horaAbertura")
    private String horaAbertura;

    @Schema(description = "Hora de fechamento", example = "20:00")
    @JsonProperty("horaFechamento")
    private String horaFechamento;

    @Schema(description = "Indica se o mercado funciona neste dia", example = "true")
    private Boolean aberto;

    @Schema(description = "Observações", example = "Encerra mais cedo nos finais de semana")
    private String observacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public HorarioResponse() {
    }

    public HorarioResponse(Long id, String diaSemana, String horaAbertura, String horaFechamento, Boolean aberto, String observacoes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.aberto = aberto;
        this.observacoes = observacoes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiaSemana() {
        return this.diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Método factory para criar HorarioResponse a partir de HorarioFuncionamento
     */
    public static HorarioResponse from(com.netflix.mercado.entity.HorarioFuncionamento horario) {
        return new HorarioResponse(
            horario.getId(),
            horario.getDiaSemana() != null ? horario.getDiaSemana().name() : null,
            horario.getHoraAbertura() != null ? horario.getHoraAbertura().toString() : null,
            horario.getHoraFechamento() != null ? horario.getHoraFechamento().toString() : null,
            horario.getAberto(),
            horario.getObservacoes(),
            horario.getCreatedAt(),
            horario.getUpdatedAt()
        );
    }

}
