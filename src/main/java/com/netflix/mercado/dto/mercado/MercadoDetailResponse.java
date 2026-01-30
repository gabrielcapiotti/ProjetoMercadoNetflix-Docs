package com.netflix.mercado.dto.mercado;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.avaliacao.AvaliacaoResponse;
import com.netflix.mercado.dto.horario.HorarioResponse;
import com.netflix.mercado.dto.promocao.PromocaoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta detalhada com informações completas do mercado")
public class MercadoDetailResponse {

    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @Schema(description = "CNPJ do mercado", example = "12.345.678/0001-90")
    private String cnpj;

    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Endereço completo", example = "Rua das Flores, 123, Centro")
    private String enderecoCompleto;

    @Schema(description = "Latitude", example = "-23.5505")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private BigDecimal longitude;

    @Schema(description = "URL da foto principal")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;

    @Schema(description = "Avaliação média (0-5)", example = "4.5")
    @JsonProperty("avaliacaoMedia")
    private BigDecimal avaliacaoMedia;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Lista de horários de funcionamento")
    private List<HorarioResponse> horarios;

    @Schema(description = "Lista de promoções ativas")
    private List<PromocaoResponse> promocoes;

    @Schema(description = "Lista de avaliações mais recentes")
    private List<AvaliacaoResponse> avaliacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
