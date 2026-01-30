package com.netflix.mercado.dto.mercado;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualização de mercado")
public class UpdateMercadoRequest {

    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Telefone deve estar em formato válido")
    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @Size(min = 5, max = 150, message = "Endereço deve ter entre 5 e 150 caracteres")
    @Schema(description = "Rua e número", example = "Rua das Flores")
    private String endereco;

    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Schema(description = "Complemento do endereço", example = "Apto 501")
    private String complemento;

    @Size(min = 3, max = 80, message = "Bairro deve ter entre 3 e 80 caracteres")
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Size(min = 3, max = 80, message = "Cidade deve ter entre 3 e 80 caracteres")
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ser a sigla em maiúsculas")
    @Schema(description = "UF", example = "SP")
    private String estado;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar em formato válido")
    @Schema(description = "CEP", example = "01310-100")
    private String cep;

    @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
    @Schema(description = "Latitude do mercado", example = "-23.5505")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
    @Schema(description = "Longitude do mercado", example = "-46.6333")
    private BigDecimal longitude;

    @URL(message = "URL da foto deve ser válida")
    @Schema(description = "URL da foto principal do mercado", example = "https://example.com/mercado.jpg")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;
}
