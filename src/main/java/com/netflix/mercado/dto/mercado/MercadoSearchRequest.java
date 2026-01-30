package com.netflix.mercado.dto.mercado;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
