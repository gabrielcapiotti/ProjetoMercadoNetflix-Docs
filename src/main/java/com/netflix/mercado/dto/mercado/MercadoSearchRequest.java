package com.netflix.mercado.dto.mercado;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Requisição para buscar mercados com filtros")
public class MercadoSearchRequest {

    @Size(min = 1, max = 100, message = "Nome deve ter entre 1 e 100 caracteres")
    @Schema(description = "Nome ou parte do nome do mercado (busca parcial)", example = "Mercado")
    private String nome;

    @Size(min = 3, max = 80, message = "Cidade deve ter entre 3 e 80 caracteres")
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ser uma sigla")
    @Schema(description = "UF", example = "SP")
    private String estado;

    @DecimalMin(value = "0.0", message = "Avaliação mínima não pode ser negativa")
    @DecimalMax(value = "5.0", message = "Avaliação mínima não pode ser maior que 5")
    @Schema(description = "Avaliação mínima para filtro (0-5)", example = "3.0")
    @Size(min = 0)
    private BigDecimal minAvaliacao;

    @Min(value = 0, message = "Página deve ser no mínimo 0")
    @Schema(description = "Número da página (0-indexed)", example = "0")
    private Integer page;

    @Min(value = 1, message = "Tamanho deve ser no mínimo 1")
    @Schema(description = "Quantidade de resultados por página", example = "20")
    private Integer size;
    public MercadoSearchRequest() {
    }

    public MercadoSearchRequest(String nome, String cidade, String estado, BigDecimal minAvaliacao, Integer page, Integer size) {
        this.nome = nome;
        this.cidade = cidade;
        this.estado = estado;
        this.minAvaliacao = minAvaliacao;
        this.page = page;
        this.size = size;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getMinAvaliacao() {
        return this.minAvaliacao;
    }

    public void setMinAvaliacao(BigDecimal minAvaliacao) {
        this.minAvaliacao = minAvaliacao;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
