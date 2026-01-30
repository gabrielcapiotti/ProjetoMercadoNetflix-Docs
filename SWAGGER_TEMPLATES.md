# 📋 Templates Prontos - Swagger Annotations Netflix Mercados

## 🎯 Templates para Controllers

### Template Base de Controller
```java
package com.netflix.mercados.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/RESOURCE")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "TAG_NAME", description = "DESCRIPTION")
public class ResourceController {

    private final ResourceService service;

    // Templates abaixo...
}
```

---

## 📝 Templates de Operações CRUD

### 1. GET ALL (Listar com Paginação)
```java
@GetMapping
@Operation(
    summary = "Listar todos os recursos",
    description = "Retorna lista paginada de recursos com filtros opcionais."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lista retornada com sucesso",
        content = @Content(schema = @Schema(implementation = Page.class))
    )
})
public ResponseEntity<Page<ResourceResponse>> listAll(
        @Parameter(description = "Número da página (inicia em 0)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "Quantidade de itens por página", example = "20")
        @RequestParam(defaultValue = "20") int size,
        
        @Parameter(description = "Campo de ordenação", example = "nome,asc")
        @RequestParam(required = false) String sort
) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ResourceResponse> resources = service.listAll(pageable);
    return ResponseEntity.ok(resources);
}
```

### 2. GET BY ID
```java
@GetMapping("/{id}")
@Operation(
    summary = "Buscar recurso por ID",
    description = "Retorna detalhes completos de um recurso específico."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Recurso encontrado",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
public ResponseEntity<ResourceResponse> getById(
        @Parameter(description = "ID único do recurso", required = true, example = "1")
        @PathVariable Long id
) {
    ResourceResponse resource = service.getById(id);
    return ResponseEntity.ok(resource);
}
```

### 3. POST (Create)
```java
@PostMapping
@SecurityRequirement(name = "bearer-jwt")
@Operation(
    summary = "Criar novo recurso",
    description = "Cria um novo recurso no sistema. Requer autenticação JWT."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Recurso criado com sucesso",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
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
        responseCode = "409",
        description = "Recurso já existe (conflito)"
    )
})
public ResponseEntity<ResourceResponse> create(
        @Valid @RequestBody CreateResourceRequest request
) {
    ResourceResponse response = service.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### 4. PUT (Update)
```java
@PutMapping("/{id}")
@SecurityRequirement(name = "bearer-jwt")
@Operation(
    summary = "Atualizar recurso",
    description = "Atualiza dados de um recurso existente. Requer autenticação JWT."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Recurso atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
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
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
public ResponseEntity<ResourceResponse> update(
        @Parameter(description = "ID do recurso a atualizar", required = true, example = "1")
        @PathVariable Long id,
        
        @Valid @RequestBody UpdateResourceRequest request
) {
    ResourceResponse response = service.update(id, request);
    return ResponseEntity.ok(response);
}
```

### 5. DELETE
```java
@DeleteMapping("/{id}")
@SecurityRequirement(name = "bearer-jwt")
@Operation(
    summary = "Deletar recurso",
    description = "Remove um recurso do sistema (soft delete). Requer autenticação JWT."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "Recurso deletado com sucesso"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Token JWT ausente ou inválido"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
public ResponseEntity<Void> delete(
        @Parameter(description = "ID do recurso a deletar", required = true, example = "1")
        @PathVariable Long id
) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

### 6. PATCH (Update Parcial)
```java
@PatchMapping("/{id}")
@SecurityRequirement(name = "bearer-jwt")
@Operation(
    summary = "Atualizar parcialmente recurso",
    description = "Atualiza apenas campos específicos de um recurso. Requer autenticação JWT."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Recurso atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
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
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
public ResponseEntity<ResourceResponse> partialUpdate(
        @Parameter(description = "ID do recurso", required = true, example = "1")
        @PathVariable Long id,
        
        @Valid @RequestBody PatchResourceRequest request
) {
    ResourceResponse response = service.partialUpdate(id, request);
    return ResponseEntity.ok(response);
}
```

---

## 🔐 Templates com Autorização

### GET com Role SELLER
```java
@GetMapping("/meus-recursos")
@PreAuthorize("hasRole('SELLER')")
@SecurityRequirement(name = "bearer-jwt")
@Operation(
    summary = "Listar meus recursos",
    description = "Retorna recursos do usuário autenticado. Requer role SELLER."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lista retornada com sucesso"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Token JWT ausente ou inválido"
    ),
    @ApiResponse(
        responseCode = "403",
        description = "Usuário não tem role SELLER"
    )
})
public ResponseEntity<List<ResourceResponse>> getMyResources() {
    User user = getCurrentUser();
    List<ResourceResponse> resources = service.getByUser(user);
    return ResponseEntity.ok(resources);
}
```

### POST com Role ADMIN
```java
@PostMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-jwt")
@Operation(
    summary = "Criar recurso (Admin)",
    description = "Cria um recurso com privilégios administrativos. Requer role ADMIN."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Recurso criado com sucesso",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Token JWT ausente ou inválido"
    ),
    @ApiResponse(
        responseCode = "403",
        description = "Usuário não tem role ADMIN"
    )
})
public ResponseEntity<ResourceResponse> createAsAdmin(
        @Valid @RequestBody CreateResourceRequest request
) {
    ResourceResponse response = service.createAsAdmin(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

---

## 📋 Templates de DTOs

### Request DTO Base
```java
package com.netflix.mercados.dto.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criação de recurso")
public class CreateResourceRequest {

    @NotBlank(message = "Campo não pode estar em branco")
    @Size(min = 3, max = 100)
    @Schema(
        description = "Descrição do campo",
        example = "Valor exemplo",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String campo;
}
```

### Response DTO Base
```java
package com.netflix.mercados.dto.resource.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta contendo dados do recurso")
public class ResourceResponse {

    @Schema(description = "ID único do recurso", example = "1")
    private Long id;

    @Schema(description = "Nome do recurso", example = "Nome Exemplo")
    private String nome;

    @Schema(description = "Data de criação", example = "2024-01-30T10:30:00")
    private LocalDateTime criadoEm;

    @Schema(description = "Data de última atualização", example = "2024-01-30T15:45:00")
    private LocalDateTime atualizadoEm;
}
```

---

## 🔤 Templates de Campos por Tipo

### String Simples
```java
@NotBlank(message = "Nome não pode estar em branco")
@Size(min = 3, max = 100)
@Schema(
    description = "Nome do recurso",
    example = "João Silva",
    required = true,
    minLength = 3,
    maxLength = 100
)
private String nome;
```

### Email
```java
@NotBlank(message = "Email não pode estar em branco")
@Email(message = "Email deve ser válido")
@Schema(
    description = "Endereço de email",
    example = "joao@example.com",
    required = true,
    format = "email"
)
private String email;
```

### Telefone
```java
@NotBlank(message = "Telefone não pode estar em branco")
@Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
@Schema(
    description = "Telefone com DDD (apenas números)",
    example = "11987654321",
    required = true,
    pattern = "\\d{10,11}"
)
private String telefone;
```

### CPF
```java
@NotBlank(message = "CPF não pode estar em branco")
@Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
@Schema(
    description = "CPF (apenas números)",
    example = "12345678901",
    required = true,
    pattern = "\\d{11}"
)
private String cpf;
```

### CNPJ
```java
@NotBlank(message = "CNPJ não pode estar em branco")
@Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 dígitos")
@Schema(
    description = "CNPJ (apenas números)",
    example = "12345678901234",
    required = true,
    pattern = "\\d{14}"
)
private String cnpj;
```

### CEP
```java
@NotBlank(message = "CEP não pode estar em branco")
@Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos")
@Schema(
    description = "CEP (apenas números)",
    example = "01310100",
    required = true,
    pattern = "\\d{8}"
)
private String cep;
```

### Integer com Range
```java
@NotNull(message = "Idade não pode ser nula")
@Min(value = 18, message = "Idade mínima é 18")
@Max(value = 120, message = "Idade máxima é 120")
@Schema(
    description = "Idade do usuário",
    example = "25",
    required = true,
    minimum = "18",
    maximum = "120"
)
private Integer idade;
```

### BigDecimal (Preço/Dinheiro)
```java
@NotNull(message = "Preço não pode ser nulo")
@DecimalMin(value = "0.01", message = "Preço mínimo é 0.01")
@DecimalMax(value = "999999.99", message = "Preço máximo é 999999.99")
@Schema(
    description = "Preço do produto em reais",
    example = "99.90",
    required = true,
    minimum = "0.01",
    maximum = "999999.99"
)
private BigDecimal preco;
```

### BigDecimal (Latitude)
```java
@DecimalMin(value = "-90.0")
@DecimalMax(value = "90.0")
@Schema(
    description = "Latitude para geolocalização",
    example = "-23.550520",
    minimum = "-90.0",
    maximum = "90.0"
)
private BigDecimal latitude;
```

### BigDecimal (Longitude)
```java
@DecimalMin(value = "-180.0")
@DecimalMax(value = "180.0")
@Schema(
    description = "Longitude para geolocalização",
    example = "-46.633308",
    minimum = "-180.0",
    maximum = "180.0"
)
private BigDecimal longitude;
```

### Boolean
```java
@Schema(
    description = "Indica se o recurso está ativo",
    example = "true",
    defaultValue = "true"
)
private Boolean ativo;
```

### Enum
```java
@NotNull(message = "Status não pode ser nulo")
@Schema(
    description = "Status do recurso",
    example = "ATIVO",
    required = true,
    allowableValues = {"ATIVO", "INATIVO", "PENDENTE", "BLOQUEADO"}
)
private StatusEnum status;
```

### LocalDate
```java
@NotNull(message = "Data não pode ser nula")
@Schema(
    description = "Data de nascimento",
    example = "1990-05-15",
    required = true,
    format = "date"
)
private LocalDate dataNascimento;
```

### LocalDateTime
```java
@Schema(
    description = "Data e hora de criação",
    example = "2024-01-30T10:30:00",
    format = "date-time"
)
private LocalDateTime criadoEm;
```

### LocalTime
```java
@NotNull(message = "Horário não pode ser nulo")
@Schema(
    description = "Horário de abertura",
    example = "08:00:00",
    required = true,
    format = "time"
)
private LocalTime horarioAbertura;
```

### List (Array)
```java
@Schema(
    description = "Lista de IDs dos produtos",
    example = "[1, 2, 3, 4, 5]"
)
private List<Long> produtosIds;
```

### List de Objetos
```java
@Schema(description = "Lista de horários de funcionamento")
private List<HorarioFuncionamentoDTO> horarios;
```

### URL
```java
@Schema(
    description = "URL da imagem",
    example = "https://cdn.example.com/images/produto.jpg",
    format = "uri"
)
private String imagemUrl;
```

### UUID
```java
@Schema(
    description = "Identificador único universal",
    example = "550e8400-e29b-41d4-a716-446655440000",
    format = "uuid"
)
private UUID uuid;
```

### Rating (Nota de 1 a 5)
```java
@NotNull(message = "Rating não pode ser nulo")
@Min(value = 1, message = "Rating mínimo é 1")
@Max(value = 5, message = "Rating máximo é 5")
@Schema(
    description = "Avaliação de 1 a 5 estrelas",
    example = "4",
    required = true,
    minimum = "1",
    maximum = "5"
)
private Integer rating;
```

### Senha
```java
@NotBlank(message = "Senha não pode estar em branco")
@Size(min = 8, max = 100)
@Schema(
    description = "Senha do usuário (min 8 caracteres)",
    example = "Senha@123",
    required = true,
    format = "password",
    minLength = 8,
    maxLength = 100
)
private String password;
```

### Texto Longo (Descrição)
```java
@NotBlank(message = "Descrição não pode estar em branco")
@Size(min = 10, max = 1000)
@Schema(
    description = "Descrição detalhada do recurso",
    example = "Esta é uma descrição completa do recurso com todos os detalhes necessários.",
    required = true,
    minLength = 10,
    maxLength = 1000
)
private String descricao;
```

---

## 🏷️ Tags Prontas por Controller

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

---

## 📊 Template de Respostas Comuns

### Sucesso Simples (200 OK)
```java
@ApiResponse(
    responseCode = "200",
    description = "Operação bem-sucedida",
    content = @Content(schema = @Schema(implementation = ResourceResponse.class))
)
```

### Criação (201 Created)
```java
@ApiResponse(
    responseCode = "201",
    description = "Recurso criado com sucesso",
    content = @Content(schema = @Schema(implementation = ResourceResponse.class))
)
```

### Sem Conteúdo (204 No Content)
```java
@ApiResponse(
    responseCode = "204",
    description = "Operação bem-sucedida sem corpo de resposta"
)
```

### Bad Request (400)
```java
@ApiResponse(
    responseCode = "400",
    description = "Dados de entrada inválidos (validação falhou)"
)
```

### Unauthorized (401)
```java
@ApiResponse(
    responseCode = "401",
    description = "Token JWT ausente ou inválido"
)
```

### Forbidden (403)
```java
@ApiResponse(
    responseCode = "403",
    description = "Usuário sem permissão para esta operação"
)
```

### Not Found (404)
```java
@ApiResponse(
    responseCode = "404",
    description = "Recurso não encontrado"
)
```

### Conflict (409)
```java
@ApiResponse(
    responseCode = "409",
    description = "Conflito: recurso já existe"
)
```

### Internal Server Error (500)
```java
@ApiResponse(
    responseCode = "500",
    description = "Erro interno do servidor"
)
```

---

## 🎯 Template Completo de @ApiResponses

### Para Endpoints Públicos (GET)
```java
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Operação bem-sucedida",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
```

### Para Endpoints Protegidos (GET)
```java
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Operação bem-sucedida",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Token JWT ausente ou inválido"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
```

### Para POST (Create)
```java
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Recurso criado com sucesso",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
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
        responseCode = "409",
        description = "Recurso já existe (conflito)"
    )
})
```

### Para PUT (Update)
```java
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Recurso atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = ResourceResponse.class))
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
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
```

### Para DELETE
```java
@ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "Recurso deletado com sucesso"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Token JWT ausente ou inválido"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado"
    )
})
```

---

## ✅ Como Usar Este Guia

1. **Escolha o template** apropriado para seu caso de uso
2. **Copie e cole** no seu código
3. **Substitua** os placeholders:
   - `Resource` → Nome da sua entidade (Mercado, Avaliacao, etc)
   - `TAG_NAME` → Nome da tag
   - `DESCRIPTION` → Descrição apropriada
4. **Ajuste** exemplos e valores conforme necessário
5. **Teste** no Swagger UI

---

**Última atualização:** 30 de Janeiro de 2024  
**Versão:** 1.0.0
