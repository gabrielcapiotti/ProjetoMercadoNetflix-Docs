package com.netflix.mercado.dto.mercado;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações básicas do mercado")
public class MercadoResponse {

    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Rua e número", example = "Rua das Flores, 123")
    private String endereco;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "UF", example = "SP")
    private String estado;

    @Schema(description = "CEP", example = "01310-100")
    private String cep;

    @Schema(description = "Latitude", example = "-23.5505")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private BigDecimal longitude;

    @Schema(description = "URL da foto principal", example = "https://example.com/mercado.jpg")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;

    @Schema(description = "Avaliação média (0-5)", example = "4.5")
    @JsonProperty("avaliacaoMedia")
    private BigDecimal avaliacaoMedia;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
