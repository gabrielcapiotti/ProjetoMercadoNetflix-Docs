package com.netflix.mercado.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(description = "Requisição para criar comentário em avaliação")
public class CreateComentarioRequest {

    @NotNull(message = "ID da avaliação não pode ser nulo")
    @Schema(description = "ID da avaliação", example = "1")
    @JsonProperty("avaliacaoId")
    private Long avaliacaoId;

    @NotBlank(message = "Conteúdo não pode estar em branco")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "ID do comentário pai para respostas aninhadas", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;
    public CreateComentarioRequest() {
    }

    public CreateComentarioRequest(Long avaliacaoId, String conteudo, Long comentarioPaiId) {
        this.avaliacaoId = avaliacaoId;
        this.conteudo = conteudo;
        this.comentarioPaiId = comentarioPaiId;
    }

    public Long getAvaliacaoId() {
        return this.avaliacaoId;
    }

    public void setAvaliacaoId(Long avaliacaoId) {
        this.avaliacaoId = avaliacaoId;
    }

    public String getConteudo() {
        return this.conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Long getComentarioPaiId() {
        return this.comentarioPaiId;
    }

    public void setComentarioPaiId(Long comentarioPaiId) {
        this.comentarioPaiId = comentarioPaiId;
    }

}
