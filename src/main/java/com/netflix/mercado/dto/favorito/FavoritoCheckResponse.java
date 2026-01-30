package com.netflix.mercado.dto.favorito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta para verificar se mercado está nos favoritos")
public class FavoritoCheckResponse {

    @Schema(description = "Indica se o mercado está nos favoritos do usuário", example = "true")
    private Boolean existe;

    @Schema(description = "ID do favorito, se existir", example = "1")
    private Long favoritoId;
}
