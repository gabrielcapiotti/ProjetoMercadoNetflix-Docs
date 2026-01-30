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
@Schema(description = "Requisição para criação de novo mercado")
public class CreateMercadoRequest {

    @NotBlank(message = "Nome do mercado não pode estar em branco")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @NotBlank(message = "CNPJ não pode estar em branco")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", 
             message = "CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX")
    @Schema(description = "CNPJ do mercado", example = "12.345.678/0001-90")
    private String cnpj;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @NotBlank(message = "Telefone não pode estar em branco")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Telefone deve estar em formato válido")
    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @NotBlank(message = "Endereço não pode estar em branco")
    @Size(min = 5, max = 150, message = "Endereço deve ter entre 5 e 150 caracteres")
    @Schema(description = "Rua e número", example = "Rua das Flores")
    private String endereco;

    @NotBlank(message = "Número não pode estar em branco")
    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Schema(description = "Complemento do endereço (opcional)", example = "Apto 501")
    private String complemento;

    @NotBlank(message = "Bairro não pode estar em branco")
    @Size(min = 3, max = 80, message = "Bairro deve ter entre 3 e 80 caracteres")
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @NotBlank(message = "Cidade não pode estar em branco")
    @Size(min = 3, max = 80, message = "Cidade deve ter entre 3 e 80 caracteres")
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @NotBlank(message = "Estado não pode estar em branco")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ser a sigla em maiúsculas (ex: SP)")
    @Schema(description = "UF", example = "SP")
    private String estado;

    @NotBlank(message = "CEP não pode estar em branco")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar em formato válido (XXXXX-XXX)")
    @Schema(description = "CEP", example = "01310-100")
    private String cep;

    @NotNull(message = "Latitude não pode ser nula")
    @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
    @Schema(description = "Latitude do mercado", example = "-23.5505")
    private BigDecimal latitude;

    @NotNull(message = "Longitude não pode ser nula")
    @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
    @Schema(description = "Longitude do mercado", example = "-46.6333")
    private BigDecimal longitude;

    @URL(message = "URL da foto deve ser válida")
    @Schema(description = "URL da foto principal do mercado", example = "https://example.com/mercado.jpg")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;
}
