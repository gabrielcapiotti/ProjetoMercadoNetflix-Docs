package com.netflix.mercado.dto.horario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar horário de funcionamento")
public class CreateHorarioRequest {

    @NotBlank(message = "Dia da semana não pode estar em branco")
    @Pattern(regexp = "^(SEGUNDA|TERCA|QUARTA|QUINTA|SEXTA|SABADO|DOMINGO)$",
             message = "Dia da semana inválido")
    @Schema(description = "Dia da semana (SEGUNDA a DOMINGO)", example = "SEGUNDA")
    @JsonProperty("diaSemana")
    private String diaSemana;

    @NotBlank(message = "Hora de abertura não pode estar em branco")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", 
             message = "Hora de abertura deve estar no formato HH:mm")
    @Schema(description = "Hora de abertura", example = "08:00")
    @JsonProperty("horaAbertura")
    private String horaAbertura;

    @NotBlank(message = "Hora de fechamento não pode estar em branco")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "Hora de fechamento deve estar no formato HH:mm")
    @Schema(description = "Hora de fechamento", example = "20:00")
    @JsonProperty("horaFechamento")
    private String horaFechamento;

    @NotNull(message = "Status de abertura não pode ser nulo")
    @Schema(description = "Indica se o mercado funciona neste dia", example = "true")
    private Boolean aberto;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o horário (opcional)", example = "Encerra mais cedo nos finais de semana")
    private String observacoes;
}
