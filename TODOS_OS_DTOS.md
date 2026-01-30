# DTOs (Data Transfer Objects) - Netflix Mercados

Código Java completo pronto para produção, organizado por categorias.

---

## CATEGORIA 1: AUTH DTOS

### 1.1 RegisterRequest
```java
package com.netflix.mercado.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para registro de novo usuário")
public class RegisterRequest {

    @NotBlank(message = "Username não pode estar em branco")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Schema(description = "Nome de usuário único", example = "joao.silva")
    private String username;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Schema(description = "Senha com letras, números e caracteres especiais", example = "Senha@123")
    private String password;

    @NotBlank(message = "Confirmação de senha não pode estar em branco")
    @Schema(description = "Confirmação da senha", example = "Senha@123")
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @NotBlank(message = "Nome completo não pode estar em branco")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva Santos")
    private String fullName;
}
```

### 1.2 LoginRequest
```java
package com.netflix.mercado.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para autenticação de usuário")
public class LoginRequest {

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Schema(description = "Senha do usuário", example = "Senha@123")
    private String password;
}
```

### 1.3 JwtAuthenticationResponse
```java
package com.netflix.mercado.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com tokens JWT de autenticação")
public class JwtAuthenticationResponse {

    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("accessToken")
    private String accessToken;

    @Schema(description = "Token de refresh JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("refreshToken")
    private String refreshToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    @JsonProperty("tokenType")
    private String tokenType;

    @Schema(description = "Tempo de expiração em segundos", example = "3600")
    @JsonProperty("expiresIn")
    private Long expiresIn;

    @Schema(description = "Dados do usuário autenticado")
    private UserResponse user;
}
```

### 1.4 RefreshTokenRequest
```java
package com.netflix.mercado.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para refresh do token JWT")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token não pode estar em branco")
    @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}
```

### 1.5 UserResponse
```java
package com.netflix.mercado.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações do usuário")
public class UserResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @Schema(description = "Username", example = "joao.silva")
    private String username;

    @Schema(description = "Nome completo", example = "João Silva Santos")
    @JsonProperty("fullName")
    private String fullName;

    @Schema(description = "Funções/roles do usuário", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean active;

    @Schema(description = "Data de criação do usuário")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de última atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 1.6 ChangePasswordRequest
```java
package com.netflix.mercado.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para alterar senha do usuário")
public class ChangePasswordRequest {

    @NotBlank(message = "Senha atual não pode estar em branco")
    @Schema(description = "Senha atual do usuário", example = "SenhaAntiga@123")
    @JsonProperty("oldPassword")
    private String oldPassword;

    @NotBlank(message = "Nova senha não pode estar em branco")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Schema(description = "Nova senha", example = "NovaSenh@456")
    @JsonProperty("newPassword")
    private String newPassword;

    @NotBlank(message = "Confirmação de senha não pode estar em branco")
    @Schema(description = "Confirmação da nova senha", example = "NovaSenh@456")
    @JsonProperty("confirmPassword")
    private String confirmPassword;
}
```

---

## CATEGORIA 2: MERCADO DTOS

### 2.1 CreateMercadoRequest
```java
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
```

### 2.2 UpdateMercadoRequest
```java
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
```

### 2.3 MercadoResponse
```java
package com.netflix.mercado.dto.mercado;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações básicas do mercado")
public class MercadoResponse {

    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Rua e número", example = "Rua das Flores, 123")
    private String endereco;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "UF", example = "SP")
    private String estado;

    @Schema(description = "CEP", example = "01310-100")
    private String cep;

    @Schema(description = "Latitude", example = "-23.5505")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private BigDecimal longitude;

    @Schema(description = "URL da foto principal", example = "https://example.com/mercado.jpg")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;

    @Schema(description = "Avaliação média (0-5)", example = "4.5")
    @JsonProperty("avaliacaoMedia")
    private BigDecimal avaliacaoMedia;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 2.4 MercadoDetailResponse
```java
package com.netflix.mercado.dto.mercado;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.avaliacao.AvaliacaoResponse;
import com.netflix.mercado.dto.horario.HorarioResponse;
import com.netflix.mercado.dto.promocao.PromocaoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta detalhada com informações completas do mercado")
public class MercadoDetailResponse {

    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @Schema(description = "CNPJ do mercado", example = "12.345.678/0001-90")
    private String cnpj;

    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Endereço completo", example = "Rua das Flores, 123, Centro")
    private String enderecoCompleto;

    @Schema(description = "Latitude", example = "-23.5505")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private BigDecimal longitude;

    @Schema(description = "URL da foto principal")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;

    @Schema(description = "Avaliação média (0-5)", example = "4.5")
    @JsonProperty("avaliacaoMedia")
    private BigDecimal avaliacaoMedia;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Lista de horários de funcionamento")
    private List<HorarioResponse> horarios;

    @Schema(description = "Lista de promoções ativas")
    private List<PromocaoResponse> promocoes;

    @Schema(description = "Lista de avaliações mais recentes")
    private List<AvaliacaoResponse> avaliacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 2.5 MercadoNearbyRequest
```java
package com.netflix.mercado.dto.mercado;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Requisição para buscar mercados próximos por localização")
public class MercadoNearbyRequest {

    @NotNull(message = "Latitude não pode ser nula")
    @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
    @Schema(description = "Latitude da localização", example = "-23.5505")
    private BigDecimal latitude;

    @NotNull(message = "Longitude não pode ser nula")
    @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
    @Schema(description = "Longitude da localização", example = "-46.6333")
    private BigDecimal longitude;

    @NotNull(message = "Raio não pode ser nulo")
    @Min(value = 1, message = "Raio deve ser no mínimo 1 km")
    @Schema(description = "Raio de busca em quilômetros", example = "5")
    private BigDecimal raio;

    @Min(value = 0, message = "Página deve ser no mínimo 0")
    @Schema(description = "Número da página (0-indexed)", example = "0")
    private Integer page;

    @Min(value = 1, message = "Tamanho deve ser no mínimo 1")
    @Schema(description = "Quantidade de resultados por página", example = "20")
    private Integer size;
}
```

### 2.6 MercadoSearchRequest
```java
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
```

---

## CATEGORIA 3: AVALIACAO DTOS

### 3.1 CreateAvaliacaoRequest
```java
package com.netflix.mercado.dto.avaliacao;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar uma avaliação de mercado")
public class CreateAvaliacaoRequest {

    @NotNull(message = "ID do mercado não pode ser nulo")
    @Schema(description = "ID do mercado a ser avaliado", example = "1")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @NotNull(message = "Número de estrelas não pode ser nulo")
    @Min(value = 1, message = "Avaliação deve ser no mínimo 1 estrela")
    @Max(value = 5, message = "Avaliação deve ser no máximo 5 estrelas")
    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Size(min = 10, max = 1000, message = "Comentário deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Comentário sobre o mercado (opcional)", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;
}
```

### 3.2 UpdateAvaliacaoRequest
```java
package com.netflix.mercado.dto.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualizar uma avaliação")
public class UpdateAvaliacaoRequest {

    @Min(value = 1, message = "Avaliação deve ser no mínimo 1 estrela")
    @Max(value = 5, message = "Avaliação deve ser no máximo 5 estrelas")
    @Schema(description = "Número de estrelas (1-5)", example = "5")
    private Integer estrelas;

    @Size(min = 10, max = 1000, message = "Comentário deve ter entre 10 e 1000 caracteres")
    @Schema(description = "Comentário sobre o mercado", example = "Experiência excelente!")
    private String comentario;
}
```

### 3.3 AvaliacaoResponse
```java
package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações de avaliação")
public class AvaliacaoResponse {

    @Schema(description = "ID da avaliação", example = "1")
    private Long id;

    @Schema(description = "ID do usuário que avaliou")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Username de quem avaliou", example = "joao.silva")
    private String usuarioUsername;

    @Schema(description = "ID do mercado avaliado")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @Schema(description = "Nome do mercado avaliado", example = "Mercado Central")
    private String mercadoNome;

    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Schema(description = "Comentário da avaliação", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;

    @Schema(description = "Total de curtidas", example = "12")
    private Integer curtidas;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 3.4 AvaliacaoDetailResponse
```java
package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.auth.UserResponse;
import com.netflix.mercado.dto.comentario.ComentarioDetailResponse;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta detalhada com informações completas de avaliação")
public class AvaliacaoDetailResponse {

    @Schema(description = "ID da avaliação", example = "1")
    private Long id;

    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Schema(description = "Comentário da avaliação", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;

    @Schema(description = "Total de curtidas", example = "12")
    private Integer curtidas;

    @Schema(description = "Informações do usuário que avaliou")
    private UserResponse usuario;

    @Schema(description = "Informações básicas do mercado")
    private MercadoResponse mercado;

    @Schema(description = "Lista de comentários sobre a avaliação")
    private List<ComentarioDetailResponse> comentarios;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 3.5 RatingStatsResponse
```java
package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com estatísticas de avaliações")
public class RatingStatsResponse {

    @Schema(description = "Média de estrelas (0-5)", example = "4.35")
    @JsonProperty("mediaEstrelas")
    private BigDecimal mediaEstrelas;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Distribuição de avaliações por estrela")
    @JsonProperty("distribuicaoPorEstrela")
    private Map<Integer, Integer> distribuicaoPorEstrela;

    @Schema(description = "Percentual de avaliações com 5 estrelas", example = "45.24")
    @JsonProperty("percentualCincoEstrelas")
    private BigDecimal percentualCincoEstrelas;

    @Schema(description = "Percentual de avaliações com 4 estrelas", example = "28.57")
    @JsonProperty("percentualQuatroEstrelas")
    private BigDecimal percentualQuatroEstrelas;

    @Schema(description = "Percentual de avaliações com 3 estrelas", example = "14.29")
    @JsonProperty("percentualTresEstrelas")
    private BigDecimal percentualTresEstrelas;

    @Schema(description = "Percentual de avaliações com 2 estrelas", example = "7.14")
    @JsonProperty("percentualDoisEstrelas")
    private BigDecimal percentualDoisEstrelas;

    @Schema(description = "Percentual de avaliações com 1 estrela", example = "4.76")
    @JsonProperty("percentualUmaEstrela")
    private BigDecimal percentualUmaEstrela;
}
```

---

## CATEGORIA 4: COMENTARIO DTOS

### 4.1 CreateComentarioRequest
```java
package com.netflix.mercado.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar comentário em avaliação")
public class CreateComentarioRequest {

    @NotNull(message = "ID da avaliação não pode ser nulo")
    @Schema(description = "ID da avaliação", example = "1")
    @JsonProperty("avaliacaoId")
    private Long avaliacaoId;

    @NotBlank(message = "Conteúdo não pode estar em branco")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "ID do comentário pai para respostas aninhadas", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;
}
```

### 4.2 UpdateComentarioRequest
```java
package com.netflix.mercado.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualizar comentário")
public class UpdateComentarioRequest {

    @NotBlank(message = "Conteúdo não pode estar em branco")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    @Schema(description = "Novo conteúdo do comentário", example = "Na verdade, mudei de ideia!")
    private String conteudo;
}
```

### 4.3 ComentarioResponse
```java
package com.netflix.mercado.dto.comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações de comentário")
public class ComentarioResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "ID do usuário que comentou")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Username de quem comentou", example = "joao.silva")
    private String usuarioUsername;

    @Schema(description = "Total de curtidas", example = "5")
    private Integer curtidas;

    @Schema(description = "ID do comentário pai (para respostas)", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 4.4 ComentarioDetailResponse
```java
package com.netflix.mercado.dto.comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.auth.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta detalhada com informações completas de comentário")
public class ComentarioDetailResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "Total de curtidas", example = "5")
    private Integer curtidas;

    @Schema(description = "Informações do usuário que comentou")
    private UserResponse usuario;

    @Schema(description = "ID do comentário pai (para respostas)", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;

    @Schema(description = "Lista de respostas aninhadas")
    private List<ComentarioDetailResponse> respostas;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

---

## CATEGORIA 5: FAVORITO DTOS

### 5.1 CreateFavoritoRequest
```java
package com.netflix.mercado.dto.favorito;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para adicionar mercado aos favoritos")
public class CreateFavoritoRequest {

    @NotNull(message = "ID do mercado não pode ser nulo")
    @Schema(description = "ID do mercado a ser favoritado", example = "1")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o mercado (opcional)", example = "Compro aqui toda semana")
    private String observacoes;

    @Schema(description = "Prioridade do favorito (1-5, sendo 1 menor)", example = "3")
    private Integer prioridade;
}
```

### 5.2 FavoritoResponse
```java
package com.netflix.mercado.dto.favorito;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações do favorito")
public class FavoritoResponse {

    @Schema(description = "ID do favorito", example = "1")
    private Long id;

    @Schema(description = "Informações do mercado favoritado")
    private MercadoResponse mercado;

    @Schema(description = "ID do usuário proprietário do favorito")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Observações sobre o mercado", example = "Compro aqui toda semana")
    private String observacoes;

    @Schema(description = "Prioridade do favorito (1-5)", example = "3")
    private Integer prioridade;

    @Schema(description = "Data de adição aos favoritos")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de última atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 5.3 FavoritoCheckResponse
```java
package com.netflix.mercado.dto.favorito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta para verificar se mercado está nos favoritos")
public class FavoritoCheckResponse {

    @Schema(description = "Indica se o mercado está nos favoritos do usuário", example = "true")
    private Boolean existe;

    @Schema(description = "ID do favorito, se existir", example = "1")
    private Long favoritoId;
}
```

---

## CATEGORIA 6: NOTIFICACAO DTOS

### 6.1 CreateNotificacaoRequest
```java
package com.netflix.mercado.dto.notificacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar notificação")
public class CreateNotificacaoRequest {

    @NotNull(message = "ID do usuário não pode ser nulo")
    @Schema(description = "ID do usuário destinatário", example = "1")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @NotBlank(message = "Título não pode estar em branco")
    @Size(min = 3, max = 150, message = "Título deve ter entre 3 e 150 caracteres")
    @Schema(description = "Título da notificação", example = "Nova promoção disponível")
    private String titulo;

    @NotBlank(message = "Conteúdo não pode estar em branco")
    @Size(min = 5, max = 1000, message = "Conteúdo deve ter entre 5 e 1000 caracteres")
    @Schema(description = "Conteúdo da notificação", example = "Confira a nova promoção no Mercado Central")
    private String conteudo;

    @NotNull(message = "Tipo não pode ser nulo")
    @Schema(description = "Tipo de notificação (INFO, PROMOCAO, ALERTA, AVISO)", example = "PROMOCAO")
    private String tipo;
}
```

### 6.2 NotificacaoResponse
```java
package com.netflix.mercado.dto.notificacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações de notificação")
public class NotificacaoResponse {

    @Schema(description = "ID da notificação", example = "1")
    private Long id;

    @Schema(description = "Título da notificação", example = "Nova promoção disponível")
    private String titulo;

    @Schema(description = "Conteúdo da notificação", example = "Confira a nova promoção no Mercado Central")
    private String conteudo;

    @Schema(description = "Tipo de notificação", example = "PROMOCAO")
    private String tipo;

    @Schema(description = "Indica se a notificação foi lida", example = "false")
    private Boolean lida;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de leitura")
    @JsonProperty("readAt")
    private LocalDateTime readAt;
}
```

### 6.3 NotificacaoStatsResponse
```java
package com.netflix.mercado.dto.notificacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com estatísticas de notificações")
public class NotificacaoStatsResponse {

    @Schema(description = "Total de notificações do usuário", example = "15")
    @JsonProperty("totalNotificacoes")
    private Integer totalNotificacoes;

    @Schema(description = "Total de notificações não lidas", example = "5")
    @JsonProperty("naoLidas")
    private Integer naoLidas;

    @Schema(description = "Total de notificações lidas", example = "10")
    @JsonProperty("lidas")
    private Integer lidas;

    @Schema(description = "Notificações não lidas nos últimos 7 dias", example = "3")
    @JsonProperty("naoLidasUltimos7Dias")
    private Integer naoLidasUltimos7Dias;
}
```

---

## CATEGORIA 7: PROMOCAO DTOS

### 7.1 CreatePromocaoRequest
```java
package com.netflix.mercado.dto.promocao;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
```

### 7.2 UpdatePromocaoRequest
```java
package com.netflix.mercado.dto.promocao;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualizar promoção")
public class UpdatePromocaoRequest {

    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    @Schema(description = "Descrição da promoção", example = "Desconto especial em produtos selecionados")
    private String descricao;

    @DecimalMin(value = "0.0", inclusive = false, message = "Desconto deve ser maior que 0")
    @DecimalMax(value = "100.0", message = "Desconto não pode ser maior que 100%")
    @Schema(description = "Percentual de desconto (0-100)", example = "20.00")
    @JsonProperty("percentualDesconto")
    private BigDecimal percentualDesconto;

    @DecimalMin(value = "0.0", message = "Valor máximo de desconto não pode ser negativo")
    @Schema(description = "Valor máximo de desconto permitido", example = "75.00")
    @JsonProperty("valorDescontoMaximo")
    private BigDecimal valorDescontoMaximo;

    @DecimalMin(value = "0.0", message = "Valor mínimo de compra não pode ser negativo")
    @Schema(description = "Valor mínimo de compra para usar promoção", example = "150.00")
    @JsonProperty("valorMinimoCompra")
    private BigDecimal valorMinimoCompra;

    @Future(message = "Data de validade deve ser no futuro")
    @Schema(description = "Data de validade da promoção", example = "2025-12-31T23:59:59")
    @JsonProperty("dataValidade")
    private LocalDateTime dataValidade;

    @Min(value = 1, message = "Máximo de utilizações deve ser no mínimo 1")
    @Schema(description = "Máximo de vezes que a promoção pode ser utilizada", example = "200")
    @JsonProperty("maxUtilizacoes")
    private Integer maxUtilizacoes;
}
```

### 7.3 PromocaoResponse
```java
package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
```

### 7.4 ValidatePromocaoResponse
```java
package com.netflix.mercado.dto.promocao;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
```

---

## CATEGORIA 8: HORARIO DTOS

### 8.1 CreateHorarioRequest
```java
package com.netflix.mercado.dto.horario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar horário de funcionamento")
public class CreateHorarioRequest {

    @NotBlank(message = "Dia da semana não pode estar em branco")
    @Pattern(regexp = "^(SEGUNDA|TERCA|QUARTA|QUINTA|SEXTA|SABADO|DOMINGO)$",
             message = "Dia da semana inválido")
    @Schema(description = "Dia da semana (SEGUNDA a DOMINGO)", example = "SEGUNDA")
    @JsonProperty("diaSemana")
    private String diaSemana;

    @NotBlank(message = "Hora de abertura não pode estar em branco")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", 
             message = "Hora de abertura deve estar no formato HH:mm")
    @Schema(description = "Hora de abertura", example = "08:00")
    @JsonProperty("horaAbertura")
    private String horaAbertura;

    @NotBlank(message = "Hora de fechamento não pode estar em branco")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "Hora de fechamento deve estar no formato HH:mm")
    @Schema(description = "Hora de fechamento", example = "20:00")
    @JsonProperty("horaFechamento")
    private String horaFechamento;

    @NotNull(message = "Status de abertura não pode ser nulo")
    @Schema(description = "Indica se o mercado funciona neste dia", example = "true")
    private Boolean aberto;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o horário (opcional)", example = "Encerra mais cedo nos finais de semana")
    private String observacoes;
}
```

### 8.2 UpdateHorarioRequest
```java
package com.netflix.mercado.dto.horario;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atualizar horário de funcionamento")
public class UpdateHorarioRequest {

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "Hora de abertura deve estar no formato HH:mm")
    @Schema(description = "Hora de abertura", example = "09:00")
    @JsonProperty("horaAbertura")
    private String horaAbertura;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "Hora de fechamento deve estar no formato HH:mm")
    @Schema(description = "Hora de fechamento", example = "21:00")
    @JsonProperty("horaFechamento")
    private String horaFechamento;

    @Schema(description = "Indica se o mercado funciona neste dia", example = "true")
    private Boolean aberto;

    @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres")
    @Schema(description = "Observações sobre o horário", example = "Encerra mais cedo nos finais de semana")
    private String observacoes;
}
```

### 8.3 HorarioResponse
```java
package com.netflix.mercado.dto.horario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com informações de horário de funcionamento")
public class HorarioResponse {

    @Schema(description = "ID do horário", example = "1")
    private Long id;

    @Schema(description = "Dia da semana", example = "SEGUNDA")
    @JsonProperty("diaSemana")
    private String diaSemana;

    @Schema(description = "Hora de abertura", example = "08:00")
    @JsonProperty("horaAbertura")
    private String horaAbertura;

    @Schema(description = "Hora de fechamento", example = "20:00")
    @JsonProperty("horaFechamento")
    private String horaFechamento;

    @Schema(description = "Indica se o mercado funciona neste dia", example = "true")
    private Boolean aberto;

    @Schema(description = "Observações", example = "Encerra mais cedo nos finais de semana")
    private String observacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

### 8.4 MercadoStatusResponse
```java
package com.netflix.mercado.dto.horario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com status de funcionamento do mercado")
public class MercadoStatusResponse {

    @Schema(description = "Indica se o mercado está aberto neste momento", example = "true")
    private Boolean aberto;

    @Schema(description = "Próximo horário de abertura", example = "Amanhã às 08:00")
    @JsonProperty("proximaAbertura")
    private String proximaAbertura;

    @Schema(description = "Próximo horário de fechamento", example = "Hoje às 20:00")
    @JsonProperty("proximoFechamento")
    private String proximoFechamento;

    @Schema(description = "Lista de horários para hoje")
    @JsonProperty("horariosHoje")
    private List<HorarioResponse> horariosHoje;

    @Schema(description = "Mensagem de status", example = "Aberto - Fecha às 20:00")
    private String mensagem;
}
```

---

## CATEGORIA 9: COMMON/GENERIC DTOS

### 9.1 ApiResponse<T>
```java
package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta genérica da API")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private Boolean sucesso;

    @Schema(description = "Mensagem descritiva da resposta", example = "Operação realizada com sucesso")
    private String mensagem;

    @Schema(description = "Dados retornados pela operação")
    private T dados;

    @Schema(description = "Timestamp da resposta")
    private LocalDateTime timestamp;

    /**
     * Construtor para sucesso com dados
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .sucesso(true)
                .mensagem(message)
                .dados(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Construtor para sucesso sem dados
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .sucesso(true)
                .mensagem(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Construtor para erro
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .sucesso(false)
                .mensagem(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

### 9.2 ErrorResponse
```java
package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta de erro padronizada")
public class ErrorResponse {

    @Schema(description = "Código do erro", example = "RECURSO_NAO_ENCONTRADO")
    private String codigo;

    @Schema(description = "Mensagem de erro", example = "Mercado não encontrado")
    private String mensagem;

    @Schema(description = "Detalhes adicionais sobre o erro", example = "Nenhum mercado encontrado com o ID 999")
    private String detalhes;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/mercados/999")
    private String path;

    @Schema(description = "Status HTTP da resposta", example = "404")
    private Integer status;

    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;
}
```

### 9.3 PageResponse<T>
```java
package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
```

### 9.4 ValidationErrorResponse
```java
package com.netflix.mercado.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta de erro de validação com detalhes de campos")
public class ValidationErrorResponse {

    @Schema(description = "Mensagem geral de erro", example = "Erro de validação")
    private String mensagem;

    @Schema(description = "Código do erro", example = "VALIDATION_ERROR")
    private String codigo;

    @Schema(description = "Status HTTP", example = "400")
    private Integer status;

    @Schema(description = "Lista de erros por campo")
    private List<FieldError> erros;

    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;

    /**
     * Classe interna para representar erro de campo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Erro de validação de um campo específico")
    public static class FieldError {

        @Schema(description = "Nome do campo com erro", example = "email")
        private String field;

        @Schema(description = "Valor rejeitado", example = "usuario@invalido")
        @JsonProperty("rejectedValue")
        private Object rejectedValue;

        @Schema(description = "Mensagem de erro", example = "Email deve ser válido")
        private String mensagem;

        @Schema(description = "Código do erro de validação", example = "Email.invalid")
        private String codigo;
    }
}
```

---

## Resumo de Dependências Necessárias (pom.xml)

```xml
<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>

<!-- Jakarta Validation API -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- Hibernate Validator (implementação de Jakarta Validation) -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.1.Final</version>
</dependency>

<!-- SpringDoc OpenAPI (Swagger/OpenAPI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- Jackson (para serialização JSON) -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.1</version>
</dependency>

<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.1</version>
</dependency>
```

---

## Estrutura de Diretórios Recomendada

```
src/main/java/com/netflix/mercado/
├── dto/
│   ├── auth/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── JwtAuthenticationResponse.java
│   │   ├── RefreshTokenRequest.java
│   │   ├── UserResponse.java
│   │   └── ChangePasswordRequest.java
│   ├── mercado/
│   │   ├── CreateMercadoRequest.java
│   │   ├── UpdateMercadoRequest.java
│   │   ├── MercadoResponse.java
│   │   ├── MercadoDetailResponse.java
│   │   ├── MercadoNearbyRequest.java
│   │   └── MercadoSearchRequest.java
│   ├── avaliacao/
│   │   ├── CreateAvaliacaoRequest.java
│   │   ├── UpdateAvaliacaoRequest.java
│   │   ├── AvaliacaoResponse.java
│   │   ├── AvaliacaoDetailResponse.java
│   │   └── RatingStatsResponse.java
│   ├── comentario/
│   │   ├── CreateComentarioRequest.java
│   │   ├── UpdateComentarioRequest.java
│   │   ├── ComentarioResponse.java
│   │   └── ComentarioDetailResponse.java
│   ├── favorito/
│   │   ├── CreateFavoritoRequest.java
│   │   ├── FavoritoResponse.java
│   │   └── FavoritoCheckResponse.java
│   ├── notificacao/
│   │   ├── CreateNotificacaoRequest.java
│   │   ├── NotificacaoResponse.java
│   │   └── NotificacaoStatsResponse.java
│   ├── promocao/
│   │   ├── CreatePromocaoRequest.java
│   │   ├── UpdatePromocaoRequest.java
│   │   ├── PromocaoResponse.java
│   │   └── ValidatePromocaoResponse.java
│   ├── horario/
│   │   ├── CreateHorarioRequest.java
│   │   ├── UpdateHorarioRequest.java
│   │   ├── HorarioResponse.java
│   │   └── MercadoStatusResponse.java
│   └── common/
│       ├── ApiResponse.java
│       ├── ErrorResponse.java
│       ├── PageResponse.java
│       └── ValidationErrorResponse.java
```

---

## Notas Importantes

1. **Validações**: Todos os DTOs incluem validações com Jakarta Validation Constraints
2. **Serialização**: Uso de `@JsonProperty` para mapear nomes de campos em snake_case para JSON
3. **Swagger**: Anotações `@Schema` para documentação automática no Swagger
4. **Lombok**: Todos os DTOs usam `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, e `@Builder`
5. **BigDecimal**: Usado para todos os valores monetários (evita problemas com ponto flutuante)
6. **LocalDateTime**: Usado para todos os timestamps
7. **Métodos Auxiliares**: `ApiResponse` inclui métodos estáticos para facilitar criação de respostas

---

## Exemplo de Uso nos Controllers

```java
@PostMapping("/auth/register")
@ResponseStatus(HttpStatus.CREATED)
public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserResponse user = authService.register(request);
    return ApiResponse.success("Usuário registrado com sucesso", user);
}

@GetMapping("/{id}")
public ApiResponse<MercadoDetailResponse> getMercadoDetails(@PathVariable Long id) {
    MercadoDetailResponse mercado = mercadoService.getMercadoDetails(id);
    return ApiResponse.success("Mercado encontrado", mercado);
}

@GetMapping("/search")
public ApiResponse<PageResponse<MercadoResponse>> searchMercados(
        @Valid MercadoSearchRequest request,
        Pageable pageable) {
    PageResponse<MercadoResponse> resultado = mercadoService.search(request, pageable);
    return ApiResponse.success("Mercados encontrados", resultado);
}
```
