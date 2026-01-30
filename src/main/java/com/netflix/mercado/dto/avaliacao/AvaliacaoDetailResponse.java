package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.auth.UserResponse;
import com.netflix.mercado.dto.comentario.ComentarioDetailResponse;
import com.netflix.mercado.dto.mercado.MercadoResponse;
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
@Schema(description = "Resposta detalhada com informações completas de avaliação")
public class AvaliacaoDetailResponse {

    @Schema(description = "ID da avaliação", example = "1")
    private Long id;

    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Schema(description = "Comentário da avaliação", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;

    @Schema(description = "Total de curtidas", example = "12")
    private Integer curtidas;

    @Schema(description = "Informações do usuário que avaliou")
    private UserResponse usuario;

    @Schema(description = "Informações básicas do mercado")
    private MercadoResponse mercado;

    @Schema(description = "Lista de comentários sobre a avaliação")
    private List<ComentarioDetailResponse> comentarios;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
