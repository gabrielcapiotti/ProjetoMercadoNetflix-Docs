# AvaliacaoController - REST API

> Controller REST profissional para gerenciamento de Avaliações com Java 21 e Spring Boot 3.2

## AvaliacaoController.java

```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.dto.response.PageResponse;
import com.netflix.mercados.dto.avaliacao.request.CreateAvaliacaoRequest;
import com.netflix.mercados.dto.avaliacao.request.UpdateAvaliacaoRequest;
import com.netflix.mercados.dto.avaliacao.response.AvaliacaoResponse;
import com.netflix.mercados.dto.avaliacao.response.AvaliacaoDetailResponse;
import com.netflix.mercados.dto.mercado.response.RatingStatsResponse;
import com.netflix.mercados.service.AvaliacaoService;
import com.netflix.mercados.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/avaliacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Avaliações", description = "API REST para gerenciamento de Avaliações de Mercados")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    // ==================== CREATE ====================

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Criar avaliação", 
               description = "Cria uma nova avaliação para um mercado. Um usuário pode avaliar um mercado apenas uma vez")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Avaliação criada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Usuário já avaliou este mercado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<AvaliacaoResponse>> criarAvaliacao(
            @Valid @RequestBody CreateAvaliacaoRequest request,
            Authentication authentication) {
        
        log.info("Iniciando criação de avaliação para mercado ID: {} por usuário: {}", 
                 request.getMercadoId(), authentication.getName());
        
        AvaliacaoResponse avaliacao = avaliacaoService.criarAvaliacao(request, authentication.getName());
        
        log.info("Avaliação criada com sucesso. ID: {}", avaliacao.getId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Avaliação criada com sucesso", avaliacao));
    }

    // ==================== READ ====================

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Listar avaliações com filtros e paginação", 
               description = "Retorna lista de avaliações com suporte a paginação, ordenação e filtros")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de avaliações retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<ApiResponse<PageResponse<AvaliacaoResponse>>> listarAvaliacoes(
            @Parameter(description = "Número da página (começando em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "criadoEm") String sortBy,
            @Parameter(description = "Direção de ordenação (ASC/DESC)") @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @Parameter(description = "Filtro por ID do mercado") @RequestParam(required = false) Long mercadoId,
            @Parameter(description = "Filtro por ID do usuário avaliador") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Filtro por nota mínima (1-5)") @RequestParam(required = false) Integer notaMinima,
            @Parameter(description = "Filtro por nota máxima (1-5)") @RequestParam(required = false) Integer notaMaxima) {
        
        log.debug("Listando avaliações. Page: {}, Size: {}, SortBy: {}, Direction: {}", 
                  page, size, sortBy, direction);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<AvaliacaoResponse> avaliacoes = avaliacaoService.listarAvaliacoes(
            pageable, mercadoId, usuarioId, notaMinima, notaMaxima
        );
        
        PageResponse<AvaliacaoResponse> pageResponse = PageResponse.fromPage(avaliacoes);
        return ResponseEntity.ok(ApiResponse.success("Avaliações listadas com sucesso", pageResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Obter detalhes de uma avaliação", 
               description = "Retorna informações completas de uma avaliação específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalhes da avaliação retornados com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoDetailResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<ApiResponse<AvaliacaoDetailResponse>> obterDetalhes(
            @Parameter(description = "ID da avaliação") @PathVariable Long id) {
        
        log.debug("Obtendo detalhes da avaliação ID: {}", id);
        AvaliacaoDetailResponse avaliacao = avaliacaoService.obterDetalhes(id);
        
        return ResponseEntity.ok(ApiResponse.success("Detalhes da avaliação obtidos com sucesso", avaliacao));
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Atualizar avaliação", 
               description = "Atualiza uma avaliação existente. Apenas o criador da avaliação ou admin pode atualizar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Avaliação atualizada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<AvaliacaoResponse>> atualizarAvaliacao(
            @Parameter(description = "ID da avaliação") @PathVariable Long id,
            @Valid @RequestBody UpdateAvaliacaoRequest request,
            Authentication authentication) {
        
        log.info("Atualizando avaliação ID: {} por usuário: {}", id, authentication.getName());
        AvaliacaoResponse avaliacao = avaliacaoService.atualizarAvaliacao(id, request, authentication.getName());
        
        log.info("Avaliação ID: {} atualizada com sucesso", id);
        return ResponseEntity.ok(ApiResponse.success("Avaliação atualizada com sucesso", avaliacao));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Deletar avaliação (soft delete)", 
               description = "Realiza soft delete de uma avaliação. Apenas o criador da avaliação ou admin pode deletar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Avaliação deletada com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Avaliação não encontrado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deletarAvaliacao(
            @Parameter(description = "ID da avaliação") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Deletando avaliação ID: {} por usuário: {}", id, authentication.getName());
        avaliacaoService.deletarAvaliacao(id, authentication.getName());
        
        log.info("Avaliação ID: {} deletada com sucesso", id);
        return ResponseEntity.noContent().build();
    }

    // ==================== MERCADO SPECIFIC ====================

    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Listar avaliações de um mercado", 
               description = "Retorna lista de todas as avaliações para um mercado específico com paginação")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Avaliações do mercado retornadas com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<ApiResponse<PageResponse<AvaliacaoResponse>>> listarAvaliacoesMercado(
            @Parameter(description = "ID do mercado") @PathVariable Long mercadoId,
            @Parameter(description = "Número da página (começando em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "nota") String sortBy,
            @Parameter(description = "Direção de ordenação (ASC/DESC)") @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @Parameter(description = "Filtro por nota mínima") @RequestParam(required = false) Integer notaMinima,
            @Parameter(description = "Filtro por nota máxima") @RequestParam(required = false) Integer notaMaxima) {
        
        log.debug("Listando avaliações do mercado ID: {}. Page: {}, Size: {}", 
                  mercadoId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<AvaliacaoResponse> avaliacoes = avaliacaoService.listarAvaliacoesMercado(
            mercadoId, pageable, notaMinima, notaMaxima
        );
        
        PageResponse<AvaliacaoResponse> pageResponse = PageResponse.fromPage(avaliacoes);
        return ResponseEntity.ok(ApiResponse.success("Avaliações do mercado listadas com sucesso", pageResponse));
    }

    @GetMapping("/mercado/{mercadoId}/stats")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Obter estatísticas de avaliação do mercado", 
               description = "Retorna estatísticas agregadas de avaliações (média, distribuição por nota, percentual de aprovação)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Estatísticas retornadas com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RatingStatsResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<ApiResponse<RatingStatsResponse>> obterEstatisticasAvaliacao(
            @Parameter(description = "ID do mercado") @PathVariable Long mercadoId) {
        
        log.debug("Obtendo estatísticas de avaliação do mercado ID: {}", mercadoId);
        RatingStatsResponse stats = avaliacaoService.obterEstatisticasAvaliacao(mercadoId);
        
        return ResponseEntity.ok(ApiResponse.success("Estatísticas obtidas com sucesso", stats));
    }
}
```

## DTOs de Suporte para Avaliação

### CreateAvaliacaoRequest.java

```java
package com.netflix.mercados.dto.avaliacao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAvaliacaoRequest {

    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser um número positivo")
    private Long mercadoId;

    @NotNull(message = "Nota é obrigatória")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private Integer nota;

    @NotBlank(message = "Comentário é obrigatório")
    @Size(min = 10, max = 500, message = "Comentário deve ter entre 10 e 500 caracteres")
    private String comentario;

    @NotNull(message = "Avaliação de atendimento é obrigatória")
    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoAtendimento;

    @NotNull(message = "Avaliação de limpeza é obrigatória")
    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoLimpeza;

    @NotNull(message = "Avaliação de produtos é obrigatória")
    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoProdutos;

    @NotNull(message = "Avaliação de preços é obrigatória")
    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoPrecos;

    private Boolean recomenda; // Se recomenda o mercado

    private String fotosUrl; // JSON array de URLs de fotos
}
```

### UpdateAvaliacaoRequest.java

```java
package com.netflix.mercados.dto.avaliacao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvaliacaoRequest {

    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private Integer nota;

    @Size(min = 10, max = 500, message = "Comentário deve ter entre 10 e 500 caracteres")
    private String comentario;

    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoAtendimento;

    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoLimpeza;

    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoProdutos;

    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer avaliacaoPrecos;

    private Boolean recomenda;

    private String fotosUrl;
}
```

### AvaliacaoResponse.java

```java
package com.netflix.mercados.dto.avaliacao.response;

import com.netflix.mercados.entity.Avaliacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoResponse {

    private Long id;
    private Long mercadoId;
    private String mercadoNome;
    private Long usuarioId;
    private String usuarioNome;
    private Integer nota;
    private String comentario;
    private Integer avaliacaoAtendimento;
    private Integer avaliacaoLimpeza;
    private Integer avaliacaoProdutos;
    private Integer avaliacaoPrecos;
    private Boolean recomenda;
    private Integer totalLikes;
    private Integer totalDislikes;
    private Boolean usuarioTemLike;
    private Boolean usuarioTemDislike;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static AvaliacaoResponse from(Avaliacao avaliacao) {
        return AvaliacaoResponse.builder()
            .id(avaliacao.getId())
            .mercadoId(avaliacao.getMercado().getId())
            .mercadoNome(avaliacao.getMercado().getNome())
            .usuarioId(avaliacao.getUsuario().getId())
            .usuarioNome(avaliacao.getUsuario().getNome())
            .nota(avaliacao.getNota())
            .comentario(avaliacao.getComentario())
            .avaliacaoAtendimento(avaliacao.getAvaliacaoAtendimento())
            .avaliacaoLimpeza(avaliacao.getAvaliacaoLimpeza())
            .avaliacaoProdutos(avaliacao.getAvaliacaoProdutos())
            .avaliacaoPrecos(avaliacao.getAvaliacaoPrecos())
            .recomenda(avaliacao.getRecomenda())
            .totalLikes(avaliacao.getTotalLikes())
            .totalDislikes(avaliacao.getTotalDislikes())
            .criadoEm(avaliacao.getCreatedAt())
            .atualizadoEm(avaliacao.getUpdatedAt())
            .build();
    }
}
```

### AvaliacaoDetailResponse.java

```java
package com.netflix.mercados.dto.avaliacao.response;

import com.netflix.mercados.entity.Avaliacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDetailResponse {

    private Long id;
    
    // Mercado
    private Long mercadoId;
    private String mercadoNome;
    
    // Usuário
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private String usuarioFoto;
    
    // Avaliação
    private Integer nota;
    private String comentario;
    private Integer avaliacaoAtendimento;
    private Integer avaliacaoLimpeza;
    private Integer avaliacaoProdutos;
    private Integer avaliacaoPrecos;
    private Double mediaCriterios;
    private Boolean recomenda;
    
    // Engajamento
    private Integer totalLikes;
    private Integer totalDislikes;
    private List<String> fotosUrl;
    
    // Auditoria
    private String criadoPor;
    private LocalDateTime criadoEm;
    private String atualizadoPor;
    private LocalDateTime atualizadoEm;

    public static AvaliacaoDetailResponse from(Avaliacao avaliacao) {
        Double media = (double) (avaliacao.getAvaliacaoAtendimento() 
            + avaliacao.getAvaliacaoLimpeza() 
            + avaliacao.getAvaliacaoProdutos() 
            + avaliacao.getAvaliacaoPrecos()) / 4;

        return AvaliacaoDetailResponse.builder()
            .id(avaliacao.getId())
            .mercadoId(avaliacao.getMercado().getId())
            .mercadoNome(avaliacao.getMercado().getNome())
            .usuarioId(avaliacao.getUsuario().getId())
            .usuarioNome(avaliacao.getUsuario().getNome())
            .usuarioEmail(avaliacao.getUsuario().getEmail())
            .usuarioFoto(avaliacao.getUsuario().getFoto())
            .nota(avaliacao.getNota())
            .comentario(avaliacao.getComentario())
            .avaliacaoAtendimento(avaliacao.getAvaliacaoAtendimento())
            .avaliacaoLimpeza(avaliacao.getAvaliacaoLimpeza())
            .avaliacaoProdutos(avaliacao.getAvaliacaoProdutos())
            .avaliacaoPrecos(avaliacao.getAvaliacaoPrecos())
            .mediaCriterios(media)
            .recomenda(avaliacao.getRecomenda())
            .totalLikes(avaliacao.getTotalLikes())
            .totalDislikes(avaliacao.getTotalDislikes())
            .criadoPor(avaliacao.getCreatedBy())
            .criadoEm(avaliacao.getCreatedAt())
            .atualizadoPor(avaliacao.getUpdatedBy())
            .atualizadoEm(avaliacao.getUpdatedAt())
            .build();
    }
}
```

## Configuração no application.yml

Adicione as seguintes configurações para swagger e validações:

```yaml
# Swagger/OpenAPI
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
    tagsSorter: alpha
    filter: true
    show-extensions: true
    doc-expansion: list
    syntax-highlight:
      theme: atom-one-dark
    try-it-out-enabled: true

  packages-to-scan: com.netflix.mercados.controller
  paths-to-match: /api/**
  
  cache:
    disabled: false

# Validação
spring:
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false

# Logging
logging:
  level:
    com.netflix.mercados: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## Padrão de Resposta Unificado

### ApiResponse.java

```java
package com.netflix.mercados.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .error(ErrorDetails.builder()
                .message(message)
                .build())
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .error(ErrorDetails.builder()
                .message(message)
                .code(code)
                .build())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String message;
    }
}
```

### PageResponse.java

```java
package com.netflix.mercados.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
    }
}
```

## Global Exception Handler

```java
package com.netflix.mercados.exception;

import com.netflix.mercados.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            ValidationException ex, WebRequest request) {
        log.error("Erro de validação: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(), "VALIDATION_ERROR"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Erro de validação de argumentos: {}", errors);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.builder()
                .success(false)
                .message("Erros de validação encontrados")
                .error(ApiResponse.ErrorDetails.builder()
                    .code("VALIDATION_ERROR")
                    .build())
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.error("Acesso negado: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Acesso negado. Você não tem permissão para acessar este recurso", "ACCESS_DENIED"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception ex, WebRequest request) {
        log.error("Erro interno do servidor", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Erro interno do servidor. Tente novamente mais tarde", "INTERNAL_SERVER_ERROR"));
    }
}
```

## DTOs para Horário de Funcionamento

### CreateHorarioRequest.java

```java
package com.netflix.mercados.dto.horario.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHorarioRequest {

    @NotBlank(message = "Dia da semana é obrigatório")
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$",
             message = "Dia da semana inválido")
    private String diaSemana;

    @NotNull(message = "Horário de abertura é obrigatório")
    private LocalTime horarioAbertura;

    @NotNull(message = "Horário de fechamento é obrigatório")
    private LocalTime horarioFechamento;

    @NotNull(message = "Status de abertura é obrigatório")
    private Boolean aberto;

    private String observacoes;
}
```

## Resumo de Features

- ✅ Endpoints RESTful profissionais
- ✅ Validação com @Valid e @NotNull
- ✅ Paginação com Spring Data
- ✅ Soft Delete com @SoftDelete
- ✅ Auditoria com BaseEntity
- ✅ Logging com @Slf4j
- ✅ Documentação Swagger completa com @Operation
- ✅ Tratamento global de exceções
- ✅ Resposta unificada (ApiResponse e PageResponse)
- ✅ CORS configurado
- ✅ Segurança com @PreAuthorize e JWT
- ✅ HTTP Status codes apropriados
- ✅ Transações com @Transactional nos services
