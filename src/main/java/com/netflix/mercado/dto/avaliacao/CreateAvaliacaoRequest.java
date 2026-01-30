package com.netflix.mercado.dto.avaliacao;

import jakarta.validation.constraints.*;
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
@Schema(description = "Requisição para criar uma avaliação de mercado")
public class CreateAvaliacaoRequest {

    @NotNull(message = "ID do mercado não pode ser nulo")
    @Schema(description = "ID do mercado a ser avaliado", example = "1")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @NotNull(message = "Número de estrelas não pode ser nulo")
    @Min(value = 1, message = "Avaliação deve ser no mínimo 1 estrela")
    @Max(value = 5, message = "Avaliação deve ser no máximo 5 estrelas")
    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Size(min = 10, max = 1000, message = "Comentário deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Comentário sobre o mercado (opcional)", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;
}
