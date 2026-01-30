package com.netflix.mercado.dto.notificacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar notificação")
public class CreateNotificacaoRequest {

    @NotNull(message = "ID do usuário não pode ser nulo")
    @Schema(description = "ID do usuário destinatário", example = "1")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @NotBlank(message = "Título não pode estar em branco")
    @Size(min = 3, max = 150, message = "Título deve ter entre 3 e 150 caracteres")
    @Schema(description = "Título da notificação", example = "Nova promoção disponível")
    private String titulo;

    @NotBlank(message = "Conteúdo não pode estar em branco")
    @Size(min = 5, max = 1000, message = "Conteúdo deve ter entre 5 e 1000 caracteres")
    @Schema(description = "Conteúdo da notificação", example = "Confira a nova promoção no Mercado Central")
    private String conteudo;

    @NotNull(message = "Tipo não pode ser nulo")
    @Schema(description = "Tipo de notificação (INFO, PROMOCAO, ALERTA, AVISO)", example = "PROMOCAO")
    private String tipo;
}
