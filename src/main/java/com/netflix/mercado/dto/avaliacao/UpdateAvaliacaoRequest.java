package com.netflix.mercado.dto.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para atualizar uma avaliação")
public class UpdateAvaliacaoRequest {

    @Min(value = 1, message = "Avaliação deve ser no mínimo 1 estrela")
    @Max(value = 5, message = "Avaliação deve ser no máximo 5 estrelas")
    @Schema(description = "Número de estrelas (1-5)", example = "5")
    private Integer estrelas;

    @Size(min = 10, max = 1000, message = "Comentário deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Comentário sobre o mercado", example = "Experiência excelente!")
    private String comentario;
    public UpdateAvaliacaoRequest() {
    }

    public UpdateAvaliacaoRequest(Integer estrelas, String comentario) {
        this.estrelas = estrelas;
        this.comentario = comentario;
    }

    public Integer getEstrelas() {
        return this.estrelas;
    }

    public void setEstrelas(Integer estrelas) {
        this.estrelas = estrelas;
    }

    public String getComentario() {
        return this.comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}
