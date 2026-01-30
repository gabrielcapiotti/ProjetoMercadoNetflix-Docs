package com.netflix.mercado.dto.avaliacao;

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
@Schema(description = "Resposta com informações de avaliação")
public class AvaliacaoResponse {

    @Schema(description = "ID da avaliação", example = "1")
    private Long id;

    @Schema(description = "ID do usuário que avaliou")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Username de quem avaliou", example = "joao.silva")
    private String usuarioUsername;

    @Schema(description = "ID do mercado avaliado")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @Schema(description = "Nome do mercado avaliado", example = "Mercado Central")
    private String mercadoNome;

    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Schema(description = "Comentário da avaliação", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;

    @Schema(description = "Total de curtidas", example = "12")
    private Integer curtidas;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
