package com.netflix.mercado.dto.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualizar uma avaliação")
public class UpdateAvaliacaoRequest {

    @Min(value = 1, message = "Avaliação deve ser no mínimo 1 estrela")
    @Max(value = 5, message = "Avaliação deve ser no máximo 5 estrelas")
    @Schema(description = "Número de estrelas (1-5)", example = "5")
    private Integer estrelas;

    @Size(min = 10, max = 1000, message = "Comentário deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Comentário sobre o mercado", example = "Experiência excelente!")
    private String comentario;
}
