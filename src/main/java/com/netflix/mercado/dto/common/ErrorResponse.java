package com.netflix.mercado.dto.common;

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
@Schema(description = "Resposta de erro padronizada")
public class ErrorResponse {

    @Schema(description = "Código do erro", example = "RECURSO_NAO_ENCONTRADO")
    private String codigo;

    @Schema(description = "Mensagem de erro", example = "Mercado não encontrado")
    private String mensagem;

    @Schema(description = "Detalhes adicionais sobre o erro", example = "Nenhum mercado encontrado com o ID 999")
    private String detalhes;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/mercados/999")
    private String path;

    @Schema(description = "Status HTTP da resposta", example = "404")
    private Integer status;

    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;
}
