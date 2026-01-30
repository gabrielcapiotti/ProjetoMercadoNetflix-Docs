package com.netflix.mercado.dto.notificacao;

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
@Schema(description = "Resposta com informações de notificação")
public class NotificacaoResponse {

    @Schema(description = "ID da notificação", example = "1")
    private Long id;

    @Schema(description = "Título da notificação", example = "Nova promoção disponível")
    private String titulo;

    @Schema(description = "Conteúdo da notificação", example = "Confira a nova promoção no Mercado Central")
    private String conteudo;

    @Schema(description = "Tipo de notificação", example = "PROMOCAO")
    private String tipo;

    @Schema(description = "Indica se a notificação foi lida", example = "false")
    private Boolean lida;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de leitura")
    @JsonProperty("readAt")
    private LocalDateTime readAt;
}
