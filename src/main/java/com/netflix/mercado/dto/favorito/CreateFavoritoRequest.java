package com.netflix.mercado.dto.favorito;

import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Requisição para adicionar mercado aos favoritos")
public class CreateFavoritoRequest {

    @NotNull(message = "ID do mercado não pode ser nulo")
    @Schema(description = "ID do mercado a ser favoritado", example = "1")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o mercado (opcional)", example = "Compro aqui toda semana")
    private String observacoes;

    @Schema(description = "Prioridade do favorito (1-5, sendo 1 menor)", example = "3")
    private Integer prioridade;
}
