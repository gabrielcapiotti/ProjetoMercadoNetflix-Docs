package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resposta com informações de promoção")
public class PromocaoResponse {

    @Schema(description = "ID da promoção", example = "1")
    private Long id;

    @Schema(description = "Código da promoção", example = "PROMO2024")
    private String codigo;

    @Schema(description = "Descrição da promoção", example = "Desconto especial em produtos selecionados")
    private String descricao;

    @Schema(description = "Percentual de desconto", example = "15.50")
    @JsonProperty("percentualDesconto")
    private BigDecimal percentualDesconto;

    @Schema(description = "Valor máximo de desconto", example = "50.00")
    @JsonProperty("valorDescontoMaximo")
    private BigDecimal valorDescontoMaximo;

    @Schema(description = "Valor mínimo de compra", example = "100.00")
    @JsonProperty("valorMinimoCompra")
    private BigDecimal valorMinimoCompra;

    @Schema(description = "Indica se a promoção está ativa", example = "true")
    private Boolean ativa;

    @Schema(description = "Total de utilizações", example = "45")
    @JsonProperty("utilizacoes")
    private Integer utilizacoes;

    @Schema(description = "Máximo de utilizações", example = "100")
    @JsonProperty("maxUtilizacoes")
    private Integer maxUtilizacoes;

    @Schema(description = "Data de início", example = "2025-01-30T00:00:00")
    @JsonProperty("dataInicio")
    private LocalDateTime dataInicio;

    @Schema(description = "Data de validade", example = "2025-12-31T23:59:59")
    @JsonProperty("dataValidade")
    private LocalDateTime dataValidade;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public PromocaoResponse() {
    }

    public PromocaoResponse(Long id, String codigo, String descricao, BigDecimal percentualDesconto, BigDecimal valorDescontoMaximo, BigDecimal valorMinimoCompra, Boolean ativa, Integer utilizacoes, Integer maxUtilizacoes, LocalDateTime dataInicio, LocalDateTime dataValidade, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.percentualDesconto = percentualDesconto;
        this.valorDescontoMaximo = valorDescontoMaximo;
        this.valorMinimoCompra = valorMinimoCompra;
        this.ativa = ativa;
        this.utilizacoes = utilizacoes;
        this.maxUtilizacoes = maxUtilizacoes;
        this.dataInicio = dataInicio;
        this.dataValidade = dataValidade;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPercentualDesconto() {
        return this.percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public BigDecimal getValorDescontoMaximo() {
        return this.valorDescontoMaximo;
    }

    public void setValorDescontoMaximo(BigDecimal valorDescontoMaximo) {
        this.valorDescontoMaximo = valorDescontoMaximo;
    }

    public BigDecimal getValorMinimoCompra() {
        return this.valorMinimoCompra;
    }

    public void setValorMinimoCompra(BigDecimal valorMinimoCompra) {
        this.valorMinimoCompra = valorMinimoCompra;
    }

    public Boolean getAtiva() {
        return this.ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public Integer getUtilizacoes() {
        return this.utilizacoes;
    }

    public void setUtilizacoes(Integer utilizacoes) {
        this.utilizacoes = utilizacoes;
    }

    public Integer getMaxUtilizacoes() {
        return this.maxUtilizacoes;
    }

    public void setMaxUtilizacoes(Integer maxUtilizacoes) {
        this.maxUtilizacoes = maxUtilizacoes;
    }

    public LocalDateTime getDataInicio() {
        return this.dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataValidade() {
        return this.dataValidade;
    }

    public void setDataValidade(LocalDateTime dataValidade) {
        this.dataValidade = dataValidade;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
