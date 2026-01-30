package com.netflix.mercado.dto.favorito;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para verificar se mercado está nos favoritos")
public class FavoritoCheckResponse {

    @Schema(description = "Indica se o mercado está nos favoritos do usuário", example = "true")
    private Boolean existe;

    @Schema(description = "ID do favorito, se existir", example = "1")
    private Long favoritoId;
    public FavoritoCheckResponse() {
    }

    public FavoritoCheckResponse(Boolean existe, Long favoritoId) {
        this.existe = existe;
        this.favoritoId = favoritoId;
    }

    public Boolean getExiste() {
        return this.existe;
    }

    public void setExiste(Boolean existe) {
        this.existe = existe;
    }

    public Long getFavoritoId() {
        return this.favoritoId;
    }

    public void setFavoritoId(Long favoritoId) {
        this.favoritoId = favoritoId;
    }

}
