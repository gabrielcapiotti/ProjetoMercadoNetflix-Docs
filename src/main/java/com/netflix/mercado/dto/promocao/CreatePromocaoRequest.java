package com.netflix.mercado.dto.promocao;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Requisição para criar promoção")
public class CreatePromocaoRequest {

    @NotBlank(message = "Código da promoção não pode estar em branco")
    @Size(min = 3, max = 20, message = "Código deve ter entre 3 e 20 caracteres")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Código deve conter apenas letras maiúsculas, números, hífen e underscore")
    @Schema(description = "Código único da promoção", example = "PROMO2024")
    private String codigo;

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    @Schema(description = "Descrição da promoção", example = "Desconto especial em produtos selecionados")
    private String descricao;

    @NotNull(message = "Percentual de desconto não pode ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "Desconto deve ser maior que 0")
    @DecimalMax(value = "100.0", message = "Desconto não pode ser maior que 100%")
    @Schema(description = "Percentual de desconto (0-100)", example = "15.50")
    @JsonProperty("percentualDesconto")
    private BigDecimal percentualDesconto;

    @DecimalMin(value = "0.0", message = "Valor máximo de desconto não pode ser negativo")
    @Schema(description = "Valor máximo de desconto permitido", example = "50.00")
    @JsonProperty("valorDescontoMaximo")
    private BigDecimal valorDescontoMaximo;

    @DecimalMin(value = "0.0", message = "Valor mínimo de compra não pode ser negativo")
    @Schema(description = "Valor mínimo de compra para usar promoção", example = "100.00")
    @JsonProperty("valorMinimoCompra")
    private BigDecimal valorMinimoCompra;

    @NotNull(message = "Data de validade não pode ser nula")
    @Future(message = "Data de validade deve ser no futuro")
    @Schema(description = "Data de validade da promoção", example = "2025-12-31T23:59:59")
    @JsonProperty("dataValidade")
    private LocalDateTime dataValidade;

    @NotNull(message = "Data de início não pode ser nula")
    @PastOrPresent(message = "Data de início deve ser no passado ou presente")
    @Schema(description = "Data de início da promoção", example = "2025-01-30T00:00:00")
    @JsonProperty("dataInicio")
    private LocalDateTime dataInicio;

    @Min(value = 1, message = "Máximo de utilizações deve ser no mínimo 1")
    @Schema(description = "Máximo de vezes que a promoção pode ser utilizada", example = "100")
    @JsonProperty("maxUtilizacoes")
    private Integer maxUtilizacoes;
    public CreatePromocaoRequest() {
    }

    public CreatePromocaoRequest(String codigo, String descricao, BigDecimal percentualDesconto, BigDecimal valorDescontoMaximo, BigDecimal valorMinimoCompra, LocalDateTime dataValidade, LocalDateTime dataInicio, Integer maxUtilizacoes) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.percentualDesconto = percentualDesconto;
        this.valorDescontoMaximo = valorDescontoMaximo;
        this.valorMinimoCompra = valorMinimoCompra;
        this.dataValidade = dataValidade;
        this.dataInicio = dataInicio;
        this.maxUtilizacoes = maxUtilizacoes;
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

    public LocalDateTime getDataValidade() {
        return this.dataValidade;
    }

    public void setDataValidade(LocalDateTime dataValidade) {
        this.dataValidade = dataValidade;
    }

    public LocalDateTime getDataInicio() {
        return this.dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Integer getMaxUtilizacoes() {
        return this.maxUtilizacoes;
    }

    public void setMaxUtilizacoes(Integer maxUtilizacoes) {
        this.maxUtilizacoes = maxUtilizacoes;
    }

}
