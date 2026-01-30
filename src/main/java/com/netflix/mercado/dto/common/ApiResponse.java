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
@Schema(description = "Resposta genérica da API")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private Boolean sucesso;

    @Schema(description = "Mensagem descritiva da resposta", example = "Operação realizada com sucesso")
    private String mensagem;

    @Schema(description = "Dados retornados pela operação")
    private T dados;

    @Schema(description = "Timestamp da resposta")
    private LocalDateTime timestamp;

    /**
     * Construtor para sucesso com dados
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .sucesso(true)
                .mensagem(message)
                .dados(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Construtor para sucesso sem dados
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .sucesso(true)
                .mensagem(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Construtor para erro
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .sucesso(false)
                .mensagem(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
