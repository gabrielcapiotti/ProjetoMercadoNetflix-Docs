package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

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
    public ErrorResponse() {
    }

    public ErrorResponse(String codigo, String mensagem, String detalhes, String path, Integer status, LocalDateTime timestamp) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.detalhes = detalhes;
        this.path = path;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDetalhes() {
        return this.detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
