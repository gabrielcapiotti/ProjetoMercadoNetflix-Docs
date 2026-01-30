# DTOs do Projeto Netflix Mercados

> Todos os DTOs em Java 21, com Jakarta Validation, Lombok, Swagger Schema e JSON Property.

## DTOs de Autenticação

### RegisterRequest

```java
package com.mercadonetflix.dtos.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para registro de usuário")
public class RegisterRequest {

    @JsonProperty("nome")
    @Schema(description = "Nome completo do usuário", example = "Ana Souza")
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
    private String nome;

    @JsonProperty("email")
    @Schema(description = "Email do usuário", example = "ana@exemplo.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @JsonProperty("senha")
    @Schema(description = "Senha do usuário", example = "Senha@123")
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$",
        message = "Senha deve conter letras maiúsculas, minúsculas, número e caractere especial"
    )
    private String senha;
}
```

### LoginRequest

```java
package com.mercadonetflix.dtos.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição de autenticação")
public class LoginRequest {

    @JsonProperty("email")
    @Schema(description = "Email do usuário", example = "ana@exemplo.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @JsonProperty("senha")
    @Schema(description = "Senha do usuário", example = "Senha@123")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
```

### JwtAuthenticationResponse

```java
package com.mercadonetflix.dtos.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de autenticação com tokens JWT")
public class JwtAuthenticationResponse {

    @JsonProperty("accessToken")
    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @JsonProperty("refreshToken")
    @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @JsonProperty("tokenType")
    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType;

    @JsonProperty("expiresIn")
    @Schema(description = "Tempo de expiração do token em segundos", example = "3600")
    private Long expiresIn;

    @JsonProperty("user")
    @Schema(description = "Dados do usuário autenticado")
    private UserResponse user;
}
```

### RefreshTokenRequest

```java
package com.mercadonetflix.dtos.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para renovar token")
public class RefreshTokenRequest {

    @JsonProperty("refreshToken")
    @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "Refresh token é obrigatório")
    private String refreshToken;
}
```

### UserResponse

```java
package com.mercadonetflix.dtos.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados públicos do usuário")
public class UserResponse {

    @JsonProperty("id")
    @Schema(description = "Identificador do usuário", example = "10")
    private Long id;

    @JsonProperty("nome")
    @Schema(description = "Nome do usuário", example = "Ana Souza")
    private String nome;

    @JsonProperty("email")
    @Schema(description = "Email do usuário", example = "ana@exemplo.com")
    private String email;

    @JsonProperty("role")
    @Schema(description = "Perfil do usuário", example = "CLIENTE")
    private String role;

    @JsonProperty("ativo")
    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean ativo;

    @JsonProperty("criadoEm")
    @Schema(description = "Data de criação", example = "2026-01-30T10:15:30")
    private LocalDateTime criadoEm;
}
```

### ChangePasswordRequest

```java
package com.mercadonetflix.dtos.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para alteração de senha")
public class ChangePasswordRequest {

    @JsonProperty("senhaAtual")
    @Schema(description = "Senha atual do usuário", example = "Senha@123")
    @NotBlank(message = "Senha atual é obrigatória")
    private String senhaAtual;

    @JsonProperty("novaSenha")
    @Schema(description = "Nova senha do usuário", example = "NovaSenha@123")
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, max = 100, message = "Nova senha deve ter entre 8 e 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$",
        message = "Nova senha deve conter letras maiúsculas, minúsculas, número e caractere especial"
    )
    private String novaSenha;
}
```

## DTOs de Mercado

### CreateMercadoRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de mercado")
public class CreateMercadoRequest {

    @JsonProperty("nome")
    @Schema(description = "Nome do mercado", example = "Mercado Central")
    @NotBlank(message = "Nome do mercado é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @JsonProperty("descricao")
    @Schema(description = "Descrição do mercado", example = "Mercado com variedade de produtos")
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @JsonProperty("telefone")
    @Schema(description = "Telefone do mercado", example = "11999998888")
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone inválido")
    private String telefone;

    @JsonProperty("email")
    @Schema(description = "Email do mercado", example = "contato@mercado.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @JsonProperty("cep")
    @Schema(description = "CEP do mercado", example = "01001-000")
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;

    @JsonProperty("rua")
    @Schema(description = "Rua do mercado", example = "Rua das Flores")
    @NotBlank(message = "Rua é obrigatória")
    private String rua;

    @JsonProperty("numero")
    @Schema(description = "Número do endereço", example = "100")
    @NotNull(message = "Número é obrigatório")
    @Positive(message = "Número deve ser positivo")
    private Integer numero;

    @JsonProperty("cidade")
    @Schema(description = "Cidade do mercado", example = "São Paulo")
    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @JsonProperty("estado")
    @Schema(description = "Estado do mercado", example = "SP")
    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @JsonProperty("complemento")
    @Schema(description = "Complemento do endereço", example = "Bloco B")
    private String complemento;

    @JsonProperty("latitude")
    @Schema(description = "Latitude do mercado", example = "-23.5505")
    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @JsonProperty("longitude")
    @Schema(description = "Longitude do mercado", example = "-46.6333")
    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @JsonProperty("cnpj")
    @Schema(description = "CNPJ do mercado", example = "12345678000199")
    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ inválido")
    private String cnpj;

    @JsonProperty("tipoMercado")
    @Schema(description = "Tipo do mercado", example = "SUPERMERCADO")
    @NotBlank(message = "Tipo de mercado é obrigatório")
    private String tipoMercado;

    @JsonProperty("logo")
    @Schema(description = "URL do logo", example = "https://cdn.exemplo.com/logo.png")
    private String logo;

    @JsonProperty("siteUrl")
    @Schema(description = "Site do mercado", example = "https://mercado.com")
    private String siteUrl;
}
```

### UpdateMercadoRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para atualização de mercado")
public class UpdateMercadoRequest {

    @JsonProperty("nome")
    @Schema(description = "Nome do mercado", example = "Mercado Central")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @JsonProperty("descricao")
    @Schema(description = "Descrição do mercado")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @JsonProperty("telefone")
    @Schema(description = "Telefone do mercado")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone inválido")
    private String telefone;

    @JsonProperty("email")
    @Schema(description = "Email do mercado")
    @Email(message = "Email inválido")
    private String email;

    @JsonProperty("cep")
    @Schema(description = "CEP do mercado")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;

    @JsonProperty("rua")
    @Schema(description = "Rua do mercado")
    private String rua;

    @JsonProperty("numero")
    @Schema(description = "Número do endereço")
    @Positive(message = "Número deve ser positivo")
    private Integer numero;

    @JsonProperty("cidade")
    @Schema(description = "Cidade do mercado")
    private String cidade;

    @JsonProperty("estado")
    @Schema(description = "Estado do mercado")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @JsonProperty("complemento")
    @Schema(description = "Complemento do endereço")
    private String complemento;

    @JsonProperty("latitude")
    @Schema(description = "Latitude do mercado")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @JsonProperty("longitude")
    @Schema(description = "Longitude do mercado")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @JsonProperty("logo")
    @Schema(description = "URL do logo")
    private String logo;

    @JsonProperty("siteUrl")
    @Schema(description = "Site do mercado")
    private String siteUrl;
}
```

### MercadoResponse

```java
package com.mercadonetflix.dtos.mercado.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta resumida de mercado")
public class MercadoResponse {

    @JsonProperty("id")
    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @JsonProperty("nome")
    @Schema(description = "Nome do mercado")
    private String nome;

    @JsonProperty("descricao")
    @Schema(description = "Descrição do mercado")
    private String descricao;

    @JsonProperty("cidade")
    @Schema(description = "Cidade do mercado")
    private String cidade;

    @JsonProperty("estado")
    @Schema(description = "Estado do mercado")
    private String estado;

    @JsonProperty("tipoMercado")
    @Schema(description = "Tipo do mercado", example = "SUPERMERCADO")
    private String tipoMercado;

    @JsonProperty("avaliacaoMedia")
    @Schema(description = "Média de avaliações", example = "4.6")
    private BigDecimal avaliacaoMedia;

    @JsonProperty("totalAvaliacoes")
    @Schema(description = "Total de avaliações", example = "120")
    private Long totalAvaliacoes;

    @JsonProperty("ativo")
    @Schema(description = "Indica se o mercado está ativo")
    private Boolean ativo;

    @JsonProperty("aprovado")
    @Schema(description = "Indica se o mercado foi aprovado")
    private Boolean aprovado;

    @JsonProperty("logo")
    @Schema(description = "URL do logo")
    private String logo;

    @JsonProperty("dataCriacao")
    @Schema(description = "Data de criação")
    private LocalDateTime dataCriacao;
}
```

### MercadoDetailResponse

```java
package com.mercadonetflix.dtos.mercado.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercadonetflix.dtos.horario.response.HorarioResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta detalhada de mercado")
public class MercadoDetailResponse {

    @JsonProperty("id")
    @Schema(description = "ID do mercado")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("telefone")
    private String telefone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("rua")
    private String rua;

    @JsonProperty("numero")
    private Integer numero;

    @JsonProperty("complemento")
    private String complemento;

    @JsonProperty("cidade")
    private String cidade;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("cnpj")
    private String cnpj;

    @JsonProperty("tipoMercado")
    private String tipoMercado;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("siteUrl")
    private String siteUrl;

    @JsonProperty("avaliacaoMedia")
    private BigDecimal avaliacaoMedia;

    @JsonProperty("totalAvaliacoes")
    private Long totalAvaliacoes;

    @JsonProperty("tempoFuncionamento")
    private Integer tempoFuncionamento;

    @JsonProperty("ativo")
    private Boolean ativo;

    @JsonProperty("aprovado")
    private Boolean aprovado;

    @JsonProperty("dataCriacao")
    private LocalDateTime dataCriacao;

    @JsonProperty("dataAtualizacao")
    private LocalDateTime dataAtualizacao;

    @JsonProperty("dataAprovacao")
    private LocalDateTime dataAprovacao;

    @JsonProperty("horarios")
    private List<HorarioResponse> horarios;

    @JsonProperty("distanciaKm")
    private Integer distanciaKm;
}
```

### MercadoSearchFilterRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filtros de busca de mercado")
public class MercadoSearchFilterRequest {

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("cidade")
    private String cidade;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("tipoMercado")
    private String tipoMercado;

    @JsonProperty("ativo")
    private Boolean ativo;

    @JsonProperty("aprovado")
    private Boolean aprovado;

    @JsonProperty("avaliacaoMinima")
    @Schema(description = "Média de avaliação mínima", example = "4.0")
    private Double avaliacaoMinima;

    @JsonProperty("totalAvaliacaoMinima")
    @Schema(description = "Total mínimo de avaliações", example = "10")
    private Long totalAvaliacaoMinima;

    @JsonProperty("page")
    @Schema(description = "Página", example = "0")
    @Min(0)
    private Integer page;

    @JsonProperty("size")
    @Schema(description = "Tamanho da página", example = "20")
    @Min(1)
    @Max(100)
    private Integer size;

    @JsonProperty("sortBy")
    @Schema(description = "Campo de ordenação", example = "dataCriacao")
    private String sortBy;

    @JsonProperty("direction")
    @Schema(description = "Direção da ordenação", example = "DESC")
    private String direction;
}
```

### MercadoNearbyRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para busca de mercados próximos")
public class MercadoNearbyRequest {

    @JsonProperty("latitude")
    @Schema(description = "Latitude", example = "-23.5505")
    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @JsonProperty("longitude")
    @Schema(description = "Longitude", example = "-46.6333")
    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @JsonProperty("raioKm")
    @Schema(description = "Raio em KM", example = "5")
    @NotNull(message = "Raio é obrigatório")
    @Positive(message = "Raio deve ser positivo")
    private Double raioKm;

    @JsonProperty("page")
    @Min(0)
    private Integer page;

    @JsonProperty("size")
    @Min(1)
    @Max(100)
    private Integer size;
}
```

### MercadoStatusResponse

```java
package com.mercadonetflix.dtos.mercado.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Status do mercado")
public class MercadoStatusResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("ativo")
    private Boolean ativo;

    @JsonProperty("aprovado")
    private Boolean aprovado;

    @JsonProperty("motivoReprovacao")
    @Schema(description = "Motivo de reprovação, se existir")
    private String motivoReprovacao;
}
```

## DTOs de Avaliação

### CreateAvaliacaoRequest

```java
package com.mercadonetflix.dtos.avaliacao.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de avaliação")
public class CreateAvaliacaoRequest {

    @JsonProperty("mercadoId")
    @Schema(description = "ID do mercado", example = "1")
    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser positivo")
    private Long mercadoId;

    @JsonProperty("estrelas")
    @Schema(description = "Número de estrelas", example = "5")
    @NotNull(message = "Número de estrelas é obrigatório")
    @Min(value = 1, message = "Mínimo 1 estrela")
    @Max(value = 5, message = "Máximo 5 estrelas")
    private Integer estrelas;

    @JsonProperty("comentario")
    @Schema(description = "Comentário da avaliação")
    @NotBlank(message = "Comentário é obrigatório")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    private String comentario;

    @JsonProperty("imagensUrls")
    @Schema(description = "URLs de imagens")
    @Size(max = 5, message = "Máximo 5 imagens")
    private List<String> imagensUrls;

    @JsonProperty("anonimo")
    @Schema(description = "Avaliação anônima", example = "false")
    private Boolean anonimo;
}
```

### UpdateAvaliacaoRequest

```java
package com.mercadonetflix.dtos.avaliacao.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para atualização de avaliação")
public class UpdateAvaliacaoRequest {

    @JsonProperty("estrelas")
    @Schema(description = "Número de estrelas", example = "4")
    @Min(value = 1, message = "Mínimo 1 estrela")
    @Max(value = 5, message = "Máximo 5 estrelas")
    private Integer estrelas;

    @JsonProperty("comentario")
    @Schema(description = "Comentário da avaliação")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    private String comentario;

    @JsonProperty("imagensUrls")
    @Schema(description = "URLs de imagens")
    @Size(max = 5, message = "Máximo 5 imagens")
    private List<String> imagensUrls;
}
```

### AvaliacaoResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta resumida de avaliação")
public class AvaliacaoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("estrelas")
    private Integer estrelas;

    @JsonProperty("comentario")
    private String comentario;

    @JsonProperty("imagensUrls")
    private List<String> imagensUrls;

    @JsonProperty("anonimo")
    private Boolean anonimo;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;
}
```

### AvaliacaoDetailResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta detalhada de avaliação")
public class AvaliacaoDetailResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("nomeUsuario")
    private String nomeUsuario;

    @JsonProperty("estrelas")
    private Integer estrelas;

    @JsonProperty("comentario")
    private String comentario;

    @JsonProperty("imagensUrls")
    private List<String> imagensUrls;

    @JsonProperty("anonimo")
    private Boolean anonimo;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;

    @JsonProperty("atualizadoEm")
    private LocalDateTime atualizadoEm;
}
```

### RatingStatsResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas de avaliações")
public class RatingStatsResponse {

    @JsonProperty("media")
    @Schema(description = "Média das avaliações", example = "4.5")
    private BigDecimal media;

    @JsonProperty("totalAvaliacoes")
    @Schema(description = "Total de avaliações", example = "120")
    private Long totalAvaliacoes;

    @JsonProperty("totalComentarios")
    @Schema(description = "Total de comentários", example = "80")
    private Long totalComentarios;
}
```

### RatingDistributionResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Distribuição de estrelas")
public class RatingDistributionResponse {

    @JsonProperty("estrelas1")
    @Schema(description = "Total de avaliações com 1 estrela", example = "3")
    private Long estrelas1;

    @JsonProperty("estrelas2")
    @Schema(description = "Total de avaliações com 2 estrelas", example = "5")
    private Long estrelas2;

    @JsonProperty("estrelas3")
    @Schema(description = "Total de avaliações com 3 estrelas", example = "12")
    private Long estrelas3;

    @JsonProperty("estrelas4")
    @Schema(description = "Total de avaliações com 4 estrelas", example = "40")
    private Long estrelas4;

    @JsonProperty("estrelas5")
    @Schema(description = "Total de avaliações com 5 estrelas", example = "60")
    private Long estrelas5;
}
```

## DTOs de Comentário

### CreateComentarioRequest

```java
package com.mercadonetflix.dtos.comentario.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de comentário")
public class CreateComentarioRequest {

    @JsonProperty("mercadoId")
    @Schema(description = "ID do mercado", example = "1")
    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser positivo")
    private Long mercadoId;

    @JsonProperty("texto")
    @Schema(description = "Texto do comentário")
    @NotBlank(message = "Texto é obrigatório")
    @Size(min = 3, max = 500, message = "Comentário deve ter entre 3 e 500 caracteres")
    private String texto;

    @JsonProperty("anonimo")
    @Schema(description = "Comentário anônimo", example = "false")
    private Boolean anonimo;
}
```

### UpdateComentarioRequest

```java
package com.mercadonetflix.dtos.comentario.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para atualização de comentário")
public class UpdateComentarioRequest {

    @JsonProperty("texto")
    @Schema(description = "Texto do comentário")
    @Size(min = 3, max = 500, message = "Comentário deve ter entre 3 e 500 caracteres")
    private String texto;
}
```

### ComentarioResponse

```java
package com.mercadonetflix.dtos.comentario.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta resumida de comentário")
public class ComentarioResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("nomeUsuario")
    private String nomeUsuario;

    @JsonProperty("texto")
    private String texto;

    @JsonProperty("anonimo")
    private Boolean anonimo;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;
}
```

### ComentarioDetailResponse

```java
package com.mercadonetflix.dtos.comentario.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta detalhada de comentário")
public class ComentarioDetailResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("nomeUsuario")
    private String nomeUsuario;

    @JsonProperty("texto")
    private String texto;

    @JsonProperty("anonimo")
    private Boolean anonimo;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;

    @JsonProperty("atualizadoEm")
    private LocalDateTime atualizadoEm;
}
```

## DTOs de Favorito

### FavoritoRequest

```java
package com.mercadonetflix.dtos.favorito.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para favoritar mercado")
public class FavoritoRequest {

    @JsonProperty("mercadoId")
    @Schema(description = "ID do mercado", example = "1")
    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser positivo")
    private Long mercadoId;
}
```

### FavoritoResponse

```java
package com.mercadonetflix.dtos.favorito.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de favorito")
public class FavoritoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;
}
```

### FavoritoCheckResponse

```java
package com.mercadonetflix.dtos.favorito.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Verificação de favorito")
public class FavoritoCheckResponse {

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("favoritado")
    private Boolean favoritado;
}
```

## DTOs de Notificação

### CreateNotificacaoRequest

```java
package com.mercadonetflix.dtos.notificacao.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de notificação")
public class CreateNotificacaoRequest {

    @JsonProperty("titulo")
    @Schema(description = "Título da notificação")
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 120, message = "Título deve ter entre 3 e 120 caracteres")
    private String titulo;

    @JsonProperty("mensagem")
    @Schema(description = "Mensagem da notificação")
    @NotBlank(message = "Mensagem é obrigatória")
    @Size(min = 5, max = 500, message = "Mensagem deve ter entre 5 e 500 caracteres")
    private String mensagem;

    @JsonProperty("tipo")
    @Schema(description = "Tipo da notificação", example = "PROMOCAO")
    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;

    @JsonProperty("usuarioId")
    @Schema(description = "ID do usuário", example = "10")
    @NotNull(message = "Usuário é obrigatório")
    private Long usuarioId;

    @JsonProperty("dataEnvio")
    @Schema(description = "Data de envio", example = "2026-01-30T10:15:30")
    private LocalDateTime dataEnvio;
}
```

### NotificacaoResponse

```java
package com.mercadonetflix.dtos.notificacao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de notificação")
public class NotificacaoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("mensagem")
    private String mensagem;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("lida")
    private Boolean lida;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;
}
```

### NotificacaoStatsResponse

```java
package com.mercadonetflix.dtos.notificacao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas de notificações")
public class NotificacaoStatsResponse {

    @JsonProperty("total")
    @Schema(description = "Total de notificações", example = "20")
    private Long total;

    @JsonProperty("lidas")
    @Schema(description = "Total de notificações lidas", example = "12")
    private Long lidas;

    @JsonProperty("naoLidas")
    @Schema(description = "Total de notificações não lidas", example = "8")
    private Long naoLidas;
}
```

## DTOs de Promoção

### CreatePromocaoRequest

```java
package com.mercadonetflix.dtos.promocao.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de promoção")
public class CreatePromocaoRequest {

    @JsonProperty("mercadoId")
    @Schema(description = "ID do mercado", example = "1")
    @NotNull(message = "ID do mercado é obrigatório")
    private Long mercadoId;

    @JsonProperty("titulo")
    @Schema(description = "Título da promoção")
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 120, message = "Título deve ter entre 3 e 120 caracteres")
    private String titulo;

    @JsonProperty("descricao")
    @Schema(description = "Descrição da promoção")
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 5, max = 500, message = "Descrição deve ter entre 5 e 500 caracteres")
    private String descricao;

    @JsonProperty("codigo")
    @Schema(description = "Código da promoção", example = "MERCADO10")
    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 30, message = "Código deve ter entre 3 e 30 caracteres")
    private String codigo;

    @JsonProperty("descontoPercentual")
    @Schema(description = "Percentual de desconto", example = "10")
    @NotNull(message = "Desconto é obrigatório")
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double descontoPercentual;

    @JsonProperty("dataInicio")
    @Schema(description = "Data de início", example = "2026-02-01")
    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;

    @JsonProperty("dataFim")
    @Schema(description = "Data de término", example = "2026-02-28")
    @NotNull(message = "Data de término é obrigatória")
    private LocalDate dataFim;

    @JsonProperty("ativo")
    @Schema(description = "Promoção ativa", example = "true")
    private Boolean ativo;
}
```

### UpdatePromocaoRequest

```java
package com.mercadonetflix.dtos.promocao.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para atualização de promoção")
public class UpdatePromocaoRequest {

    @JsonProperty("titulo")
    @Schema(description = "Título da promoção")
    @Size(min = 3, max = 120, message = "Título deve ter entre 3 e 120 caracteres")
    private String titulo;

    @JsonProperty("descricao")
    @Schema(description = "Descrição da promoção")
    @Size(min = 5, max = 500, message = "Descrição deve ter entre 5 e 500 caracteres")
    private String descricao;

    @JsonProperty("codigo")
    @Schema(description = "Código da promoção", example = "MERCADO10")
    @Size(min = 3, max = 30, message = "Código deve ter entre 3 e 30 caracteres")
    private String codigo;

    @JsonProperty("descontoPercentual")
    @Schema(description = "Percentual de desconto", example = "10")
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double descontoPercentual;

    @JsonProperty("dataInicio")
    @Schema(description = "Data de início", example = "2026-02-01")
    private LocalDate dataInicio;

    @JsonProperty("dataFim")
    @Schema(description = "Data de término", example = "2026-02-28")
    private LocalDate dataFim;

    @JsonProperty("ativo")
    @Schema(description = "Promoção ativa", example = "true")
    private Boolean ativo;
}
```

### PromocaoResponse

```java
package com.mercadonetflix.dtos.promocao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de promoção")
public class PromocaoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("codigo")
    private String codigo;

    @JsonProperty("descontoPercentual")
    private Double descontoPercentual;

    @JsonProperty("dataInicio")
    private LocalDate dataInicio;

    @JsonProperty("dataFim")
    private LocalDate dataFim;

    @JsonProperty("ativo")
    private Boolean ativo;

    @JsonProperty("criadoEm")
    private LocalDateTime criadoEm;
}
```

### ValidatePromocaoResponse

```java
package com.mercadonetflix.dtos.promocao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validação de promoção")
public class ValidatePromocaoResponse {

    @JsonProperty("codigoValido")
    @Schema(description = "Indica se o código é válido", example = "true")
    private Boolean codigoValido;

    @JsonProperty("mensagem")
    @Schema(description = "Mensagem de validação")
    private String mensagem;

    @JsonProperty("descontoPercentual")
    @Schema(description = "Percentual de desconto", example = "10")
    private Double descontoPercentual;
}
```

### ApplyPromocaoRequest

```java
package com.mercadonetflix.dtos.promocao.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para aplicar promoção")
public class ApplyPromocaoRequest {

    @JsonProperty("codigo")
    @Schema(description = "Código da promoção", example = "MERCADO10")
    @NotBlank(message = "Código é obrigatório")
    private String codigo;

    @JsonProperty("mercadoId")
    @Schema(description = "ID do mercado", example = "1")
    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser positivo")
    private Long mercadoId;

    @JsonProperty("valorOriginal")
    @Schema(description = "Valor original", example = "100.00")
    @NotNull(message = "Valor original é obrigatório")
    @Positive(message = "Valor original deve ser positivo")
    private BigDecimal valorOriginal;
}
```

### ApplyPromocaoResponse

```java
package com.mercadonetflix.dtos.promocao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de aplicação de promoção")
public class ApplyPromocaoResponse {

    @JsonProperty("valorOriginal")
    private BigDecimal valorOriginal;

    @JsonProperty("descontoAplicado")
    private BigDecimal descontoAplicado;

    @JsonProperty("valorFinal")
    private BigDecimal valorFinal;
}
```

## DTOs de Horário

### CreateHorarioRequest

```java
package com.mercadonetflix.dtos.horario.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de horário")
public class CreateHorarioRequest {

    @JsonProperty("mercadoId")
    @Schema(description = "ID do mercado", example = "1")
    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser positivo")
    private Long mercadoId;

    @JsonProperty("diaSemana")
    @Schema(description = "Dia da semana", example = "SEGUNDA")
    @NotBlank(message = "Dia da semana é obrigatório")
    private String diaSemana;

    @JsonProperty("abre")
    @Schema(description = "Horário de abertura", example = "08:00")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Horário inválido")
    private String abre;

    @JsonProperty("fecha")
    @Schema(description = "Horário de fechamento", example = "18:00")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Horário inválido")
    private String fecha;

    @JsonProperty("fechado")
    @Schema(description = "Indica se está fechado", example = "false")
    private Boolean fechado;
}
```

### UpdateHorarioRequest

```java
package com.mercadonetflix.dtos.horario.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para atualização de horário")
public class UpdateHorarioRequest {

    @JsonProperty("diaSemana")
    private String diaSemana;

    @JsonProperty("abre")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Horário inválido")
    private String abre;

    @JsonProperty("fecha")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Horário inválido")
    private String fecha;

    @JsonProperty("fechado")
    private Boolean fechado;
}
```

### HorarioResponse

```java
package com.mercadonetflix.dtos.horario.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de horário")
public class HorarioResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("mercadoId")
    private Long mercadoId;

    @JsonProperty("diaSemana")
    private String diaSemana;

    @JsonProperty("abre")
    private String abre;

    @JsonProperty("fecha")
    private String fecha;

    @JsonProperty("fechado")
    private Boolean fechado;
}
```

## DTOs Genéricos

### ApiResponse<T>

```java
package com.mercadonetflix.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta padrão da API")
public class ApiResponse<T> {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("timestamp")
    private String timestamp;
}
```

### ErrorResponse

```java
package com.mercadonetflix.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta padrão de erro")
public class ErrorResponse {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("path")
    private String path;

    @JsonProperty("errors")
    private List<String> errors;
}
```

### PageResponse<T>

```java
package com.mercadonetflix.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada")
public class PageResponse<T> {

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("pagination")
    private PaginationInfo pagination;

    @JsonProperty("sort")
    private SortInfo sort;
}
```

### PaginationInfo

```java
package com.mercadonetflix.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de paginação")
public class PaginationInfo {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("totalElements")
    private Long totalElements;

    @JsonProperty("totalPages")
    private Integer totalPages;
}
```

### SortInfo

```java
package com.mercadonetflix.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de ordenação")
public class SortInfo {

    @JsonProperty("sorted")
    private Boolean sorted;

    @JsonProperty("unsorted")
    private Boolean unsorted;

    @JsonProperty("empty")
    private Boolean empty;

    @JsonProperty("sortBy")
    private String sortBy;

    @JsonProperty("direction")
    private String direction;
}
```
