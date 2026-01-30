package com.netflix.mercado.dto.comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.auth.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta detalhada com informações completas de comentário")
public class ComentarioDetailResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "Total de curtidas", example = "5")
    private Integer curtidas;

    @Schema(description = "Informações do usuário que comentou")
    private UserResponse usuario;

    @Schema(description = "ID do comentário pai (para respostas)", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;

    @Schema(description = "Lista de respostas aninhadas")
    private List<ComentarioDetailResponse> respostas;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
