package com.netflix.mercado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promocoes", indexes = {
        @Index(name = "idx_promocao_mercado", columnList = "mercado_id"),
        @Index(name = "idx_promocao_codigo", columnList = "codigo", unique = true),
        @Index(name = "idx_promocao_ativa", columnList = "ativa"),
        @Index(name = "idx_promocao_validade", columnList = "data_validade")
})
public class Promocao extends BaseEntity {

    @NotBlank(message = "O código é obrigatório")
    @Size(min = 3, max = 50, message = "O código deve ter entre 3 e 50 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "O desconto é obrigatório")
    @DecimalMin("0.01")
    @DecimalMax("100.00")
    @Column(name = "percentual_desconto", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    @DecimalMin("0.0")
    @Column(name = "valor_desconto_maximo", precision = 10, scale = 2)
    private BigDecimal valorDescontoMaximo;

    @DecimalMin("0.0")
    @Column(name = "valor_minimo_compra", precision = 10, scale = 2)
    private BigDecimal valorMinimoCompra = BigDecimal.ZERO;

    @NotNull(message = "A data de válidade é obrigatória")
    @Column(name = "data_validade", nullable = false)
    private LocalDateTime dataValidade;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "max_utilizacoes", nullable = false)
    private Long maxUtilizacoes = Long.MAX_VALUE;

    @Column(name = "utilizacoes_atuais", nullable = false)
    private Long utilizacoesAtuais = 0L;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_promocao_mercado"))
    private Mercado mercado;

    public boolean ehValida() {
        LocalDateTime agora = LocalDateTime.now();
        boolean validaPorData = (dataInicio == null || !agora.isBefore(dataInicio)) && agora.isBefore(dataValidade);
        boolean validaPorUtilizacao = utilizacoesAtuais < maxUtilizacoes;
        return ativa && validaPorData && validaPorUtilizacao;
    }

    public BigDecimal calcularDesconto(BigDecimal valorCompra) {
        if (!ehValida()) {
            return BigDecimal.ZERO;
        }

        if (valorCompra.compareTo(valorMinimoCompra) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal desconto = valorCompra.multiply(percentualDesconto)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        if (valorDescontoMaximo != null && desconto.compareTo(valorDescontoMaximo) > 0) {
            desconto = valorDescontoMaximo;
        }

        return desconto;
    }

    public void utilizarPromocao() {
        if (ehValida()) {
            this.utilizacoesAtuais++;
        }
    }
}
