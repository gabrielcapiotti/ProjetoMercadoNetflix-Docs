package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

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
    public PageResponse() {
    }

    public PageResponse(List<T> conteudo, Integer paginaAtual, Integer totalPaginas, Long totalElementos, Integer quantidadeElementos, Boolean temProxima, Boolean temAnterior, Boolean primeiraPage, Boolean ultimaPage) {
        this.conteudo = conteudo;
        this.paginaAtual = paginaAtual;
        this.totalPaginas = totalPaginas;
        this.totalElementos = totalElementos;
        this.quantidadeElementos = quantidadeElementos;
        this.temProxima = temProxima;
        this.temAnterior = temAnterior;
        this.primeiraPage = primeiraPage;
        this.ultimaPage = ultimaPage;
    }

    public List<T> getConteudo() {
        return this.conteudo;
    }

    public void setConteudo(List<T> conteudo) {
        this.conteudo = conteudo;
    }

    public Integer getPaginaAtual() {
        return this.paginaAtual;
    }

    public void setPaginaAtual(Integer paginaAtual) {
        this.paginaAtual = paginaAtual;
    }

    public Integer getTotalPaginas() {
        return this.totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public Long getTotalElementos() {
        return this.totalElementos;
    }

    public void setTotalElementos(Long totalElementos) {
        this.totalElementos = totalElementos;
    }

    public Integer getQuantidadeElementos() {
        return this.quantidadeElementos;
    }

    public void setQuantidadeElementos(Integer quantidadeElementos) {
        this.quantidadeElementos = quantidadeElementos;
    }

    public Boolean getTemProxima() {
        return this.temProxima;
    }

    public void setTemProxima(Boolean temProxima) {
        this.temProxima = temProxima;
    }

    public Boolean getTemAnterior() {
        return this.temAnterior;
    }

    public void setTemAnterior(Boolean temAnterior) {
        this.temAnterior = temAnterior;
    }

    public Boolean getPrimeiraPage() {
        return this.primeiraPage;
    }

    public void setPrimeiraPage(Boolean primeiraPage) {
        this.primeiraPage = primeiraPage;
    }

    public Boolean getUltimaPage() {
        return this.ultimaPage;
    }

    public void setUltimaPage(Boolean ultimaPage) {
        this.ultimaPage = ultimaPage;
    }

}
