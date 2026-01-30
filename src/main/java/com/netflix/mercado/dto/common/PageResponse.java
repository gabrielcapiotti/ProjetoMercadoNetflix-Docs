package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta paginada genérica")
public class PageResponse<T> {

    @Schema(description = "Lista de elementos na página atual")
    private List<T> conteudo;

    @Schema(description = "Número da página atual (0-indexed)", example = "0")
    @JsonProperty("paginaAtual")
    private Integer paginaAtual;

    @Schema(description = "Total de páginas", example = "5")
    @JsonProperty("totalPaginas")
    private Integer totalPaginas;

    @Schema(description = "Total de elementos", example = "100")
    @JsonProperty("totalElementos")
    private Long totalElementos;

    @Schema(description = "Quantidade de elementos nesta página", example = "20")
    @JsonProperty("quantidadeElementos")
    private Integer quantidadeElementos;

    @Schema(description = "Indica se há próxima página", example = "true")
    @JsonProperty("temProxima")
    private Boolean temProxima;

    @Schema(description = "Indica se há página anterior", example = "false")
    @JsonProperty("temAnterior")
    private Boolean temAnterior;

    @Schema(description = "Indica se está na primeira página", example = "true")
    @JsonProperty("primeiraPage")
    private Boolean primeiraPage;

    @Schema(description = "Indica se está na última página", example = "false")
    @JsonProperty("ultimaPage")
    private Boolean ultimaPage;
}
