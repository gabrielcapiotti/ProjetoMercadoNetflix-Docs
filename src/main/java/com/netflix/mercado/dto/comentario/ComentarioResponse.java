package com.netflix.mercado.dto.comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Resposta com informações de comentário")
public class ComentarioResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "ID do usuário que comentou")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Username de quem comentou", example = "joao.silva")
    private String usuarioUsername;

    @Schema(description = "Total de curtidas", example = "5")
    private Integer curtidas;

    @Schema(description = "ID do comentário pai (para respostas)", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
