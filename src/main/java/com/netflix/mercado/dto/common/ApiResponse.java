package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
    public ApiResponse() {
    }

    public ApiResponse(Boolean sucesso, String mensagem, T dados, LocalDateTime timestamp) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dados = dados;
        this.timestamp = timestamp;
    }

    public Boolean getSucesso() {
        return this.sucesso;
    }

    public void setSucesso(Boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public T getDados() {
        return this.dados;
    }

    public void setDados(T dados) {
        this.dados = dados;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
