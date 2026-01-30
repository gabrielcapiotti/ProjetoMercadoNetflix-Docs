# 📚 Guia Completo de Documentação Swagger/OpenAPI - Netflix Mercados

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Configuração](#configuração)
3. [URLs de Acesso](#urls-de-acesso)
4. [Annotations de Controllers](#annotations-de-controllers)
5. [Annotations de DTOs](#annotations-de-dtos)
6. [Exemplos Completos](#exemplos-completos)
7. [Boas Práticas](#boas-práticas)
8. [Troubleshooting](#troubleshooting)

---

## 🎯 Visão Geral

A API Netflix Mercados está documentada com **Swagger/OpenAPI 3.0** usando **springdoc-openapi v2.0.2**.

### Funcionalidades
- ✅ Documentação interativa completa
- ✅ Teste de endpoints direto no navegador
- ✅ Autenticação JWT integrada
- ✅ Agrupamento por tags
- ✅ Schemas de request/response
- ✅ Exemplos em JSON
- ✅ Códigos de status HTTP
- ✅ Paginação documentada

---

## ⚙️ Configuração

### 1. Dependências (pom.xml)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```

### 2. Configuração Principal
Arquivo criado: **OpenApiConfig.java**
- Localização: `src/main/java/com/netflix/mercado/config/OpenApiConfig.java`
- Bean `netflixMercadosOpenAPI()` configurado
- Security Scheme JWT definido
- Tags organizadas
- Servers (dev, homolog, prod)

### 3. Properties (application.yml)
```yaml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
  api-docs:
    path: /api/v3/api-docs
  packages-to-scan: com.netflix.mercados
  paths-to-match: /api/v1/**
```

---

## 🌐 URLs de Acesso

| Descrição | URL | Método |
|-----------|-----|--------|
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Browser |
| **OpenAPI JSON** | http://localhost:8080/api/v3/api-docs | GET |
| **OpenAPI YAML** | http://localhost:8080/api/v3/api-docs.yaml | GET |

### URLs por Grupo (opcional)
| Grupo | URL |
|-------|-----|
| Autenticação | http://localhost:8080/api/v3/api-docs/autenticacao |
| Mercados | http://localhost:8080/api/v3/api-docs/mercados |
| Avaliações | http://localhost:8080/api/v3/api-docs/avaliacoes |

---

## 🎨 Annotations de Controllers

### 1. @Tag - Nível de Classe
Agrupa endpoints relacionados.

```java
@Tag(name = "Nome da Tag", description = "Descrição detalhada")
```

**Exemplos por Controller:**

```java
// AuthController
@Tag(name = "Autenticação", description = "Endpoints de autenticação, login, registro e gerenciamento de tokens JWT")

// MercadoController
@Tag(name = "Mercados", description = "CRUD e gerenciamento de mercados/lojas. Inclui listagem, busca, criação e atualização")

// AvaliacaoController
@Tag(name = "Avaliações", description = "Sistema de avaliações e ratings para mercados. Permite criar, editar e visualizar avaliações")

// ComentarioController
@Tag(name = "Comentários", description = "Sistema de comentários em mercados e produtos. Suporte para threads e respostas")

// FavoritoController
@Tag(name = "Favoritos", description = "Gerenciamento de mercados favoritos do usuário. Adicionar/remover favoritos e listar")

// HorarioController
@Tag(name = "Horários", description = "Gerenciamento de horários de funcionamento dos mercados. CRUD completo de horários")

// NotificacaoController
@Tag(name = "Notificações", description = "Sistema de notificações em tempo real. Push notifications e gerenciamento de preferências")

// PromocaoController
@Tag(name = "Promoções", description = "CRUD de promoções e ofertas especiais. Gerenciamento de campanhas promocionais")
```

### 2. @Operation - Nível de Método
Documenta cada endpoint individual.

```java
@Operation(
    summary = "Título curto do endpoint",
    description = "Descrição detalhada do que o endpoint faz, parâmetros, comportamento esperado"
)
```

### 3. @ApiResponses - Códigos de Status
Define todas as respostas possíveis.

```java
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Operação bem-sucedida",
        content = @Content(schema = @Schema(implementation = ResponseDTO.class))
    ),
    @ApiResponse(
        responseCode = "400", 
        description = "Dados de entrada inválidos"
    ),
    @ApiResponse(
        responseCode = "401", 
        description = "Token JWT ausente ou inválido"
    ),
    @ApiResponse(
        responseCode = "403", 
        description = "Usuário sem permissão para esta operação"
    ),
    @ApiResponse(
        responseCode = "404", 
        description = "Recurso não encontrado"
    ),
    @ApiResponse(
        responseCode = "500", 
        description = "Erro interno do servidor"
    )
})
```

### 4. @Parameter - Parâmetros de Request
Documenta path variables, query params, etc.

```java
@Parameter(
    name = "id",
    description = "ID único do mercado",
    required = true,
    example = "1"
)
```

### 5. @SecurityRequirement - Autenticação
Indica que o endpoint requer JWT.

```java
@SecurityRequirement(name = "bearer-jwt")
```

---

## 📦 Annotations de DTOs

### 1. @Schema - Nível de Classe
```java
@Schema(
    description = "Descrição do DTO",
    example = "Exemplo JSON completo (opcional)"
)
public class MeuDTO {
    // ...
}
```

### 2. @Schema - Nível de Campo
```java
@Schema(
    description = "Descrição do campo",
    example = "valor-exemplo",
    required = true,              // Campo obrigatório
    minLength = 3,                // Validação mínima
    maxLength = 100,              // Validação máxima
    pattern = "^[A-Za-z]+$",     // Regex pattern
    minimum = "0",                // Valor mínimo (números)
    maximum = "5"                 // Valor máximo (números)
)
private String campo;
```

### 3. Tipos de Dados Comuns
```java
// String
@Schema(description = "Nome do usuário", example = "João Silva")
private String nome;

// Email
@Schema(description = "Email do usuário", example = "joao@example.com")
private String email;

// Integer
@Schema(description = "Idade do usuário", example = "25", minimum = "18", maximum = "120")
private Integer idade;

// Long (ID)
@Schema(description = "ID único do recurso", example = "1")
private Long id;

// BigDecimal (Preço)
@Schema(description = "Preço do produto", example = "99.90")
private BigDecimal preco;

// Boolean
@Schema(description = "Usuário está ativo", example = "true")
private Boolean ativo;

// Enum
@Schema(description = "Tipo de usuário", example = "CUSTOMER", allowableValues = {"CUSTOMER", "SELLER", "ADMIN"})
private UserRole role;

// LocalDateTime
@Schema(description = "Data de criação", example = "2024-01-30T10:30:00")
private LocalDateTime criadoEm;

// LocalDate
@Schema(description = "Data de nascimento", example = "1990-05-15")
private LocalDate dataNascimento;

// List
@Schema(description = "Lista de IDs dos mercados favoritos")
private List<Long> favoritosIds;
```

---

## 💡 Exemplos Completos

### Exemplo 1: AuthController Completo

```java
package com.netflix.mercados.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação, login, registro e gerenciamento de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário no sistema. " +
                      "O email deve ser único e a senha deve conter pelo menos 8 caracteres " +
                      "com letras maiúsculas, minúsculas, números e caracteres especiais."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso. Retorna JWT tokens e dados do usuário.",
            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos (validação falhou)"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email ou username já está em uso"
        )
    })
    public ResponseEntity<JwtAuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Iniciando registro de novo usuário: {}", request.getEmail());
        JwtAuthenticationResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Fazer login",
        description = "Autentica um usuário existente com email e senha. " +
                      "Retorna access token (válido por 24h) e refresh token (válido por 7 dias)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas (email ou senha incorretos)"
        )
    })
    public ResponseEntity<JwtAuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Tentativa de login para: {}", request.getEmail());
        JwtAuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar access token",
        description = "Gera um novo access token usando o refresh token. " +
                      "Útil quando o access token expira mas o refresh token ainda é válido."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token renovado com sucesso",
            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido ou expirado"
        )
    })
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        log.info("Renovando access token");
        JwtAuthenticationResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Fazer logout",
        description = "Invalida o refresh token atual do usuário. " +
                      "O access token continuará válido até expirar (máx 24h)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Logout realizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT ausente ou inválido"
        )
    })
    public ResponseEntity<Void> logout() {
        log.info("Usuário fazendo logout");
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Obter dados do usuário atual",
        description = "Retorna os dados do usuário autenticado baseado no JWT token."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dados do usuário retornados com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT ausente ou inválido"
        )
    })
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}
```

### Exemplo 2: MercadoController com Paginação

```java
@RestController
@RequestMapping("/api/v1/mercados")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Mercados", description = "CRUD e gerenciamento de mercados/lojas")
public class MercadoController {

    private final MercadoService mercadoService;

    @GetMapping
    @Operation(
        summary = "Listar todos os mercados",
        description = "Retorna lista paginada de mercados com filtros opcionais de busca por nome, categoria e localização."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<MercadoResponse>> listMercados(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Quantidade de itens por página", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Campo de ordenação", example = "nome,asc")
            @RequestParam(required = false) String sort,
            
            @Parameter(description = "Filtro de busca por nome", example = "Mercado Central")
            @RequestParam(required = false) String nome,
            
            @Parameter(description = "Filtro por categoria", example = "SUPERMERCADO")
            @RequestParam(required = false) String categoria
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MercadoResponse> mercados = mercadoService.listMercados(nome, categoria, pageable);
        return ResponseEntity.ok(mercados);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar mercado por ID",
        description = "Retorna detalhes completos de um mercado específico incluindo horários de funcionamento."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercado encontrado",
            content = @Content(schema = @Schema(implementation = MercadoDetailResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<MercadoDetailResponse> getMercadoById(
            @Parameter(description = "ID único do mercado", required = true, example = "1")
            @PathVariable Long id
    ) {
        MercadoDetailResponse mercado = mercadoService.getMercadoById(id);
        return ResponseEntity.ok(mercado);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar novo mercado",
        description = "Cria um novo mercado. Requer role SELLER. " +
                      "O CNPJ e email devem ser únicos no sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Mercado criado com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário não tem permissão SELLER"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "CNPJ ou email já cadastrado"
        )
    })
    public ResponseEntity<MercadoResponse> createMercado(
            @Valid @RequestBody CreateMercadoRequest request
    ) {
        User user = getCurrentUser();
        MercadoResponse response = mercadoService.createMercado(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar mercado",
        description = "Atualiza dados de um mercado existente. " +
                      "Apenas o proprietário (SELLER) pode atualizar."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercado atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário não é proprietário do mercado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<MercadoResponse> updateMercado(
            @Parameter(description = "ID do mercado a atualizar", required = true, example = "1")
            @PathVariable Long id,
            
            @Valid @RequestBody UpdateMercadoRequest request
    ) {
        User user = getCurrentUser();
        MercadoResponse response = mercadoService.updateMercado(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar mercado",
        description = "Remove um mercado do sistema (soft delete). " +
                      "Apenas o proprietário ou ADMIN podem deletar."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Mercado deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário sem permissão"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<Void> deleteMercado(
            @Parameter(description = "ID do mercado a deletar", required = true, example = "1")
            @PathVariable Long id
    ) {
        User user = getCurrentUser();
        mercadoService.deleteMercado(id, user);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }
}
```

### Exemplo 3: RegisterRequest DTO Completo

```java
package com.netflix.mercados.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Requisição para registro de novo usuário no sistema",
    example = """
        {
          "username": "joao.silva",
          "email": "joao@example.com",
          "password": "Senha@123",
          "confirmPassword": "Senha@123",
          "fullName": "João Silva Santos"
        }
        """
)
public class RegisterRequest {

    @NotBlank(message = "Username não pode estar em branco")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Schema(
        description = "Nome de usuário único. Apenas letras, números, pontos e underscores.",
        example = "joao.silva",
        required = true,
        minLength = 3,
        maxLength = 50,
        pattern = "^[a-zA-Z0-9._]+$"
    )
    private String username;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    @Schema(
        description = "Endereço de email válido. Será usado para login e notificações.",
        example = "joao@example.com",
        required = true,
        format = "email"
    )
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Schema(
        description = "Senha forte contendo letras maiúsculas, minúsculas, números e caracteres especiais (@$!%*?&)",
        example = "Senha@123",
        required = true,
        minLength = 8,
        maxLength = 100,
        format = "password"
    )
    private String password;

    @NotBlank(message = "Confirmação de senha não pode estar em branco")
    @Schema(
        description = "Deve ser idêntica ao campo password",
        example = "Senha@123",
        required = true,
        format = "password"
    )
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @NotBlank(message = "Nome completo não pode estar em branco")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(
        description = "Nome completo do usuário",
        example = "João Silva Santos",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String fullName;

    @Schema(
        description = "Tipo de usuário (role). Se não informado, será CUSTOMER por padrão.",
        example = "CUSTOMER",
        allowableValues = {"CUSTOMER", "SELLER"},
        defaultValue = "CUSTOMER"
    )
    private String role;
}
```

### Exemplo 4: CreateMercadoRequest DTO

```java
package com.netflix.mercados.dto.mercado.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criação de novo mercado")
public class CreateMercadoRequest {

    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 3, max = 100)
    @Schema(
        description = "Nome comercial do mercado",
        example = "Mercado Central da Cidade",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(min = 10, max = 1000)
    @Schema(
        description = "Descrição detalhada do mercado e seus diferenciais",
        example = "Mercado completo com produtos frescos, padaria própria e açougue",
        required = true,
        minLength = 10,
        maxLength = 1000
    )
    private String descricao;

    @NotBlank(message = "CNPJ não pode estar em branco")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    @Schema(
        description = "CNPJ do estabelecimento (apenas números)",
        example = "12345678901234",
        required = true,
        pattern = "\\d{14}"
    )
    private String cnpj;

    @NotBlank(message = "Email não pode estar em branco")
    @Email
    @Schema(
        description = "Email comercial do mercado",
        example = "contato@mercadocentral.com.br",
        required = true,
        format = "email"
    )
    private String email;

    @NotBlank(message = "Telefone não pode estar em branco")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    @Schema(
        description = "Telefone comercial (apenas números, com DDD)",
        example = "11987654321",
        required = true,
        pattern = "\\d{10,11}"
    )
    private String telefone;

    @NotBlank(message = "Endereço não pode estar em branco")
    @Schema(
        description = "Endereço completo (rua, número, bairro)",
        example = "Rua das Flores, 123, Centro",
        required = true
    )
    private String endereco;

    @NotBlank(message = "Cidade não pode estar em branco")
    @Schema(
        description = "Cidade onde o mercado está localizado",
        example = "São Paulo",
        required = true
    )
    private String cidade;

    @NotBlank(message = "Estado não pode estar em branco")
    @Size(min = 2, max = 2)
    @Schema(
        description = "UF (sigla do estado)",
        example = "SP",
        required = true,
        minLength = 2,
        maxLength = 2
    )
    private String estado;

    @NotBlank(message = "CEP não pode estar em branco")
    @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos")
    @Schema(
        description = "CEP (apenas números)",
        example = "01310100",
        required = true,
        pattern = "\\d{8}"
    )
    private String cep;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(
        description = "Latitude para geolocalização",
        example = "-23.550520",
        minimum = "-90.0",
        maximum = "90.0"
    )
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(
        description = "Longitude para geolocalização",
        example = "-46.633308",
        minimum = "-180.0",
        maximum = "180.0"
    )
    private BigDecimal longitude;

    @Schema(
        description = "Categoria do mercado",
        example = "SUPERMERCADO",
        allowableValues = {"SUPERMERCADO", "MINIMERCADO", "ATACADO", "HORTIFRUTI", "PADARIA", "ACOUGUE"}
    )
    private String categoria;

    @Schema(
        description = "URL da logo do mercado",
        example = "https://cdn.example.com/logos/mercado-central.png"
    )
    private String logoUrl;

    @Schema(
        description = "URL da imagem de capa do mercado",
        example = "https://cdn.example.com/covers/mercado-central-cover.jpg"
    )
    private String imagemCapaUrl;
}
```

### Exemplo 5: AvaliacaoController

```java
@RestController
@RequestMapping("/api/v1/avaliacoes")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Avaliações", description = "Sistema de avaliações e ratings para mercados")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar nova avaliação",
        description = "Cria uma avaliação para um mercado. " +
                      "Rating deve ser entre 1 e 5. " +
                      "Usuário pode avaliar cada mercado apenas uma vez."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Avaliação criada com sucesso",
            content = @Content(schema = @Schema(implementation = AvaliacaoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Rating inválido ou dados incorretos"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Usuário já avaliou este mercado"
        )
    })
    public ResponseEntity<AvaliacaoResponse> createAvaliacao(
            @Valid @RequestBody CreateAvaliacaoRequest request
    ) {
        User user = getCurrentUser();
        AvaliacaoResponse response = avaliacaoService.createAvaliacao(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mercado/{mercadoId}")
    @Operation(
        summary = "Listar avaliações de um mercado",
        description = "Retorna lista paginada de avaliações de um mercado específico, ordenadas por data."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<Page<AvaliacaoResponse>> getAvaliacoesByMercado(
            @Parameter(description = "ID do mercado", required = true, example = "1")
            @PathVariable Long mercadoId,
            
            @Parameter(description = "Número da página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Itens por página", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AvaliacaoResponse> avaliacoes = avaliacaoService.getAvaliacoesByMercado(mercadoId, pageable);
        return ResponseEntity.ok(avaliacoes);
    }
}
```

---

## 🎯 Boas Práticas

### 1. **Sempre inclua todas as respostas HTTP possíveis**
```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
    @ApiResponse(responseCode = "401", description = "Não autenticado"),
    @ApiResponse(responseCode = "403", description = "Sem permissão"),
    @ApiResponse(responseCode = "404", description = "Não encontrado"),
    @ApiResponse(responseCode = "500", description = "Erro interno")
})
```

### 2. **Use descrições em português-BR**
```java
@Operation(
    summary = "Criar novo mercado",
    description = "Cria um novo mercado no sistema com validações de CNPJ e email únicos"
)
```

### 3. **Sempre forneça exemplos realistas**
```java
@Schema(
    description = "Email do usuário",
    example = "joao.silva@example.com"  // ✅ BOM
    // example = "string"  // ❌ RUIM
)
```

### 4. **Use @SecurityRequirement em endpoints protegidos**
```java
@GetMapping("/me")
@SecurityRequirement(name = "bearer-jwt")
public ResponseEntity<UserResponse> getCurrentUser() {
    // ...
}
```

### 5. **Documente paginação consistentemente**
```java
@Parameter(description = "Número da página (inicia em 0)", example = "0")
@RequestParam(defaultValue = "0") int page,

@Parameter(description = "Quantidade de itens por página", example = "20")
@RequestParam(defaultValue = "20") int size
```

### 6. **Use allowableValues para Enums**
```java
@Schema(
    description = "Tipo de usuário",
    allowableValues = {"CUSTOMER", "SELLER", "ADMIN"},
    example = "CUSTOMER"
)
private String role;
```

### 7. **Especifique formatos de dados**
```java
@Schema(description = "Email do usuário", format = "email", example = "user@example.com")
private String email;

@Schema(description = "Senha", format = "password")
private String password;

@Schema(description = "Data de criação", format = "date-time", example = "2024-01-30T10:30:00")
private LocalDateTime criadoEm;
```

### 8. **Documente validações**
```java
@Schema(
    description = "Nome do usuário",
    minLength = 3,
    maxLength = 50,
    pattern = "^[a-zA-Z ]+$",
    example = "João Silva"
)
@Size(min = 3, max = 50)
@Pattern(regexp = "^[a-zA-Z ]+$")
private String nome;
```

---

## 🔧 Troubleshooting

### Problema: Swagger UI não carrega
**Solução:**
1. Verifique se a aplicação está rodando: `http://localhost:8080`
2. Acesse: `http://localhost:8080/swagger-ui.html`
3. Verifique logs de erro
4. Confirme dependência no pom.xml

### Problema: Endpoints não aparecem
**Solução:**
1. Verifique `packages-to-scan` no application.yml
2. Confirme que o controller tem `@RestController`
3. Verifique se o path está em `/api/v1/**`

### Problema: JWT não funciona no "Try it out"
**Solução:**
1. Faça login em `/api/v1/auth/login`
2. Copie o `accessToken`
3. Clique em **Authorize** (cadeado no topo)
4. Cole o token
5. Clique em **Authorize** novamente

### Problema: Schemas vazios nos DTOs
**Solução:**
1. Adicione `@Schema` em todos os campos do DTO
2. Use `@Schema(implementation = DTO.class)` no `@ApiResponse`
3. Verifique getters/setters (Lombok @Data)

### Problema: Descrições não aparecem
**Solução:**
1. Use `description` ao invés de `value`:
   ```java
   // ✅ Correto
   @Operation(summary = "...", description = "...")
   
   // ❌ Errado
   @Operation(value = "...")
   ```

---

## 📝 Checklist de Documentação

### Para cada Controller:
- [ ] `@Tag` no nível da classe
- [ ] `@Operation` em cada método
- [ ] `@ApiResponses` com todos códigos HTTP
- [ ] `@SecurityRequirement` nos endpoints protegidos
- [ ] `@Parameter` em todos os parâmetros

### Para cada DTO:
- [ ] `@Schema` no nível da classe
- [ ] `@Schema` em cada campo
- [ ] Exemplos realistas em todos os campos
- [ ] Validações documentadas (min, max, pattern)
- [ ] `allowableValues` para enums

### Geral:
- [ ] OpenApiConfig.java criado
- [ ] application.yml configurado
- [ ] Dependência springdoc no pom.xml
- [ ] Testado em http://localhost:8080/swagger-ui.html
- [ ] JWT Authorization testado
- [ ] Todos os endpoints testados com "Try it out"

---

## 🚀 Como Testar

1. **Inicie a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o Swagger UI:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Teste autenticação:**
   - Vá em **Autenticação** > **POST /api/v1/auth/login**
   - Clique em **Try it out**
   - Insira credenciais:
     ```json
     {
       "email": "user@example.com",
       "password": "Senha@123"
     }
     ```
   - Clique em **Execute**
   - Copie o `accessToken`

4. **Autorize:**
   - Clique em **Authorize** (cadeado verde no topo)
   - Cole o token
   - Clique em **Authorize**

5. **Teste outros endpoints:**
   - Agora você pode testar endpoints protegidos
   - Use **Try it out** em qualquer endpoint

---

## 📚 Referências

- [Springdoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification 3.0](https://swagger.io/specification/)
- [Swagger Annotations Guide](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)

---

**Última atualização:** 30 de Janeiro de 2024  
**Versão:** 1.0.0  
**Autor:** Netflix Mercados Team
