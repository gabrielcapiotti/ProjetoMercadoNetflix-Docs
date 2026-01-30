package com.netflix.mercado.dto.horario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
