package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Resposta da validação de promoção")
public class ValidatePromocaoResponse {

    @Schema(description = "Indica se a promoção é válida", example = "true")
    private Boolean valida;

    @Schema(description = "Valor do desconto a ser aplicado", example = "15.00")
    private BigDecimal desconto;

    @Schema(description = "Mensagem sobre a validação", example = "Promoção válida e disponível")
    private String mensagem;

    @Schema(description = "Motivo se a promoção for inválida", example = "Promoção expirada")
    private String motivo;

    @Schema(description = "Utilizações restantes", example = "55")
    @JsonProperty("utilizacoesRestantes")
    private Integer utilizacoesRestantes;
    public ValidatePromocaoResponse() {
    }

    public ValidatePromocaoResponse(Boolean valida, BigDecimal desconto, String mensagem, String motivo, Integer utilizacoesRestantes) {
        this.valida = valida;
        this.desconto = desconto;
        this.mensagem = mensagem;
        this.motivo = motivo;
        this.utilizacoesRestantes = utilizacoesRestantes;
    }

    public Boolean getValida() {
        return this.valida;
    }

    public void setValida(Boolean valida) {
        this.valida = valida;
    }

    public BigDecimal getDesconto() {
        return this.desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Integer getUtilizacoesRestantes() {
        return this.utilizacoesRestantes;
    }

    public void setUtilizacoesRestantes(Integer utilizacoesRestantes) {
        this.utilizacoesRestantes = utilizacoesRestantes;
    }

}
