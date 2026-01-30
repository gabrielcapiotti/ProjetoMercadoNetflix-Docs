package com.netflix.mercado.dto.horario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
