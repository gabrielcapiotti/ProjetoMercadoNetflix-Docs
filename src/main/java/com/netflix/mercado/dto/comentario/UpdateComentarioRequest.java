package com.netflix.mercado.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para atualizar comentário")
public class UpdateComentarioRequest {

    @NotBlank(message = "Conteúdo não pode estar em branco")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    @Schema(description = "Novo conteúdo do comentário", example = "Na verdade, mudei de ideia!")
    private String conteudo;
    public UpdateComentarioRequest() {
    }

    public UpdateComentarioRequest(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return this.conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

}
