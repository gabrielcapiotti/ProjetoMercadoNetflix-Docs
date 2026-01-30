package com.netflix.mercado.dto.favorito;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.mercado.MercadoResponse;
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
@Schema(description = "Resposta com informações do favorito")
public class FavoritoResponse {

    @Schema(description = "ID do favorito", example = "1")
    private Long id;

    @Schema(description = "Informações do mercado favoritado")
    private MercadoResponse mercado;

    @Schema(description = "ID do usuário proprietário do favorito")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Observações sobre o mercado", example = "Compro aqui toda semana")
    private String observacoes;

    @Schema(description = "Prioridade do favorito (1-5)", example = "3")
    private Integer prioridade;

    @Schema(description = "Data de adição aos favoritos")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de última atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
