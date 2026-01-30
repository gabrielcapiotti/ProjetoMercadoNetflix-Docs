package com.netflix.mercado.dto.comentario;

import jakarta.validation.constraints.NotBlank;
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
}
