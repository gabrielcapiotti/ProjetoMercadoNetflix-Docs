package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta de erro de validação com detalhes de campos")
@Builder
public class ValidationErrorResponse {

    @Schema(description = "Mensagem geral de erro", example = "Erro de validação")
    private String mensagem;

    @Schema(description = "Código do erro", example = "VALIDATION_ERROR")
    private String codigo;

    @Schema(description = "Status HTTP", example = "400")
    private Integer status;

    @Schema(description = "Lista de erros por campo")
    private List<FieldError> erros;

    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;

    /**
     * Classe interna para representar erro de campo
     */
    @Schema(description = "Erro de validação de um campo específico")
    public static class FieldError {

        @Schema(description = "Nome do campo com erro", example = "email")
        private String field;

        @Schema(description = "Valor rejeitado", example = "usuario@invalido")
        @JsonProperty("rejectedValue")
        private Object rejectedValue;

        @Schema(description = "Mensagem de erro", example = "Email deve ser válido")
        private String mensagem;

        @Schema(description = "Código do erro de validação", example = "Email.invalid")
        private String codigo;

        public FieldError() {
        }

        public FieldError(String field, Object rejectedValue, String mensagem, String codigo) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.mensagem = mensagem;
            this.codigo = codigo;
        }

        public String getField() {
            return this.field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getRejectedValue() {
            return this.rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getMensagem() {
            return this.mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getCodigo() {
            return this.codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(String mensagem, String codigo, Integer status, List<FieldError> erros, LocalDateTime timestamp) {
        this.mensagem = mensagem;
        this.codigo = codigo;
        this.status = status;
        this.erros = erros;
        this.timestamp = timestamp;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<FieldError> getErros() {
        return this.erros;
    }

    public void setErros(List<FieldError> erros) {
        this.erros = erros;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
