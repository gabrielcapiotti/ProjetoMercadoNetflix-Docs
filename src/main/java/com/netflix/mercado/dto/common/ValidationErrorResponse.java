package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Resposta de erro de validação com detalhes de campos")
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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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
    }
}
