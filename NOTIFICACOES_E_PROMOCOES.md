# Notificações (WebSocket) e Promoções - Netflix Mercados

> Documentação pronta para produção com Controllers, Services, DTOs, Enums e WebSocket real-time para Spring Boot 3.2 (Java 21).

## 📋 Índice
1. [Endpoints](#endpoints)
2. [Enums](#enums)
3. [DTOs](#dtos)
4. [Services](#services)
5. [Controllers](#controllers)
6. [WebSocket (Real-time)](#websocket-real-time)
7. [Notas de Paginação e Soft Delete](#notas-de-paginação-e-soft-delete)

---

## Endpoints

### Notificações
```
GET    /api/v1/notificacoes                 # Listar minhas notificações (paginação)
GET    /api/v1/notificacoes/unread/count    # Contar não lidas
PUT    /api/v1/notificacoes/{id}/read       # Marcar como lida
POST   /api/v1/notificacoes/mark-all-read   # Marcar todas como lidas
DELETE /api/v1/notificacoes/{id}            # Soft delete
WebSocket: /ws/notificacoes/{userId}        # Canal real-time por usuário
```

### Promoções
```
POST   /api/v1/mercados/{mercadoId}/promocoes      # Criar promoção
GET    /api/v1/mercados/{mercadoId}/promocoes      # Listar promoções do mercado
GET    /api/v1/promocoes/{id}                      # Detalhes
PUT    /api/v1/promocoes/{id}                      # Atualizar
DELETE /api/v1/promocoes/{id}                      # Soft delete
GET    /api/v1/promocoes/code/{code}/validate      # Validar código
POST   /api/v1/promocoes/{id}/apply                # Aplicar promoção
```

---

## Enums

### TipoNotificacao
```java
package com.netflix.mercados.enums;

public enum TipoNotificacao {
    PROMOCAO,
    PEDIDO,
    AVALIACAO,
    SISTEMA,
    SUPORTE
}
```

### StatusPromocao
```java
package com.netflix.mercados.enums;

public enum StatusPromocao {
    ATIVA,
    PAUSADA,
    EXPIRADA,
    ESGOTADA
}
```

---

## DTOs

### CreateNotificacaoRequest
```java
package com.netflix.mercados.dto.notificacao.request;

import com.netflix.mercados.enums.TipoNotificacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação de notificação")
public class CreateNotificacaoRequest {

    @NotNull(message = "ID do usuário é obrigatório")
    @Positive(message = "ID do usuário deve ser positivo")
    @Schema(example = "12")
    private Long userId;

    @NotNull(message = "Tipo de notificação é obrigatório")
    @Schema(example = "PROMOCAO")
    private TipoNotificacao tipo;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 120)
    @Schema(example = "Nova promoção disponível")
    private String titulo;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(min = 5, max = 500)
    @Schema(example = "Seu mercado favorito liberou 20% OFF")
    private String mensagem;

    @Schema(description = "URL opcional de redirecionamento", example = "/promocoes/123")
    private String link;
}
```

### NotificacaoResponse
```java
package com.netflix.mercados.dto.notificacao.response;

import com.netflix.mercados.enums.TipoNotificacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta de notificação")
public class NotificacaoResponse {

    private Long id;
    private Long userId;
    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;
    private String link;
    private Boolean lida;
    private LocalDateTime dataCriacao;
}
```

### NotificacioneStatsResponse
```java
package com.netflix.mercados.dto.notificacao.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO com estatísticas de notificações")
public class NotificacioneStatsResponse {

    @Schema(description = "Quantidade de notificações não lidas", example = "5")
    private Long unreadCount;
}
```

### CreatePromocaoRequest
```java
package com.netflix.mercados.dto.promocao.request;

import com.netflix.mercados.enums.StatusPromocao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação de promoção")
public class CreatePromocaoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 120)
    private String nome;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 30)
    private String code;

    @NotNull(message = "Percentual é obrigatório")
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "90.00")
    @Schema(description = "Percentual de desconto (0-90)", example = "20.0")
    private BigDecimal percentualDesconto;

    @NotNull(message = "Valor mínimo é obrigatório")
    @DecimalMin(value = "0.00")
    private BigDecimal valorMinimo;

    @NotNull(message = "Início é obrigatório")
    private LocalDateTime inicio;

    @NotNull(message = "Fim é obrigatório")
    private LocalDateTime fim;

    @NotNull(message = "Status é obrigatório")
    private StatusPromocao status;

    @PositiveOrZero(message = "Limite de uso deve ser >= 0")
    private Integer limiteUso;

    @PositiveOrZero(message = "Uso por usuário deve ser >= 0")
    private Integer limitePorUsuario;
}
```

### PromocaoResponse
```java
package com.netflix.mercados.dto.promocao.response;

import com.netflix.mercados.enums.StatusPromocao;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocaoResponse {

    private Long id;
    private Long mercadoId;
    private String nome;
    private String code;
    private BigDecimal percentualDesconto;
    private BigDecimal valorMinimo;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private StatusPromocao status;
    private Integer limiteUso;
    private Integer limitePorUsuario;
    private Integer usos;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
}
```

### ValidatePromocaoResponse
```java
package com.netflix.mercados.dto.promocao.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidatePromocaoResponse {

    private Boolean valido;
    private String motivo;
    private BigDecimal percentualDesconto;
}
```

---

## Services

### NotificacaoService
```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.notificacao.request.CreateNotificacaoRequest;
import com.netflix.mercados.dto.notificacao.response.NotificacaoResponse;
import com.netflix.mercados.dto.notificacao.response.NotificacioneStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificacaoService {

    NotificacaoResponse criar(CreateNotificacaoRequest request);

    Page<NotificacaoResponse> listarMinhas(String username, Pageable pageable);

    NotificacaoResponse marcarComoLido(Long id, String username);

    void marcarTodasComoLidas(String username);

    void deletar(Long id, String username);

    NotificacioneStatsResponse contarNaoLidas(String username);
}
```

### NotificacaoServiceImpl
```java
package com.netflix.mercados.service.impl;

import com.netflix.mercados.dto.notificacao.request.CreateNotificacaoRequest;
import com.netflix.mercados.dto.notificacao.response.NotificacaoResponse;
import com.netflix.mercados.dto.notificacao.response.NotificacioneStatsResponse;
import com.netflix.mercados.entity.Notificacao;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.UnauthorizedException;
import com.netflix.mercados.repository.NotificacaoRepository;
import com.netflix.mercados.repository.UserRepository;
import com.netflix.mercados.service.NotificacaoService;
import com.netflix.mercados.websocket.NotificacaoHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UserRepository userRepository;
    private final NotificacaoHandler notificacaoHandler;

    @Override
    @Transactional
    public NotificacaoResponse criar(CreateNotificacaoRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Notificacao notificacao = Notificacao.create(request, user);
        notificacaoRepository.save(notificacao);

        NotificacaoResponse response = NotificacaoResponseMapper.toResponse(notificacao);
        notificacaoHandler.sendToUser(user.getId(), response);

        log.info("Notificação criada para userId={}", user.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacaoResponse> listarMinhas(String username, Pageable pageable) {
        return notificacaoRepository.findMinhas(username, pageable)
                .map(NotificacaoResponseMapper::toResponse);
    }

    @Override
    @Transactional
    public NotificacaoResponse marcarComoLido(Long id, String username) {
        Notificacao notificacao = notificacaoRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", "id", id));

        if (!notificacao.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("Você não tem permissão para acessar esta notificação");
        }

        notificacao.marcarComoLida();
        notificacaoRepository.save(notificacao);

        return NotificacaoResponseMapper.toResponse(notificacao);
    }

    @Override
    @Transactional
    public void marcarTodasComoLidas(String username) {
        notificacaoRepository.marcarTodasComoLidas(username);
        log.info("Todas as notificações marcadas como lidas para username={}", username);
    }

    @Override
    @Transactional
    public void deletar(Long id, String username) {
        Notificacao notificacao = notificacaoRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", "id", id));

        if (!notificacao.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("Você não tem permissão para excluir esta notificação");
        }

        notificacao.markAsDeleted(username);
        notificacaoRepository.save(notificacao);
        log.info("Notificação {} removida (soft delete)", id);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificacioneStatsResponse contarNaoLidas(String username) {
        Long count = notificacaoRepository.countNaoLidas(username);
        return NotificacioneStatsResponse.builder().unreadCount(count).build();
    }
}
```

### PromocaoService
```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.promocao.request.CreatePromocaoRequest;
import com.netflix.mercados.dto.promocao.response.PromocaoResponse;
import com.netflix.mercados.dto.promocao.response.ValidatePromocaoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PromocaoService {

    PromocaoResponse criar(Long mercadoId, CreatePromocaoRequest request, String username);

    Page<PromocaoResponse> listarPorMercado(Long mercadoId, Pageable pageable);

    PromocaoResponse buscarPorId(Long id);

    PromocaoResponse atualizar(Long id, CreatePromocaoRequest request, String username);

    void deletar(Long id, String username);

    ValidatePromocaoResponse validarCodigo(String code, BigDecimal totalCompra);

    PromocaoResponse aplicarPromocao(Long id, BigDecimal totalCompra, String username);

    BigDecimal calcularDesconto(BigDecimal totalCompra, BigDecimal percentualDesconto);
}
```

### PromocaoServiceImpl
```java
package com.netflix.mercados.service.impl;

import com.netflix.mercados.dto.promocao.request.CreatePromocaoRequest;
import com.netflix.mercados.dto.promocao.response.PromocaoResponse;
import com.netflix.mercados.dto.promocao.response.ValidatePromocaoResponse;
import com.netflix.mercados.entity.Mercado;
import com.netflix.mercados.entity.Promocao;
import com.netflix.mercados.enums.StatusPromocao;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.UnauthorizedException;
import com.netflix.mercados.repository.MercadoRepository;
import com.netflix.mercados.repository.PromocaoRepository;
import com.netflix.mercados.service.PromocaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromocaoServiceImpl implements PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final MercadoRepository mercadoRepository;

    @Override
    @Transactional
    public PromocaoResponse criar(Long mercadoId, CreatePromocaoRequest request, String username) {
        Mercado mercado = mercadoRepository.findById(mercadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        if (!mercado.isOwner(username)) {
            throw new UnauthorizedException("Você não tem permissão para criar promoção neste mercado");
        }

        Promocao promocao = Promocao.create(request, mercado);
        promocaoRepository.save(promocao);

        log.info("Promoção criada no mercado {}", mercadoId);
        return PromocaoResponseMapper.toResponse(promocao);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromocaoResponse> listarPorMercado(Long mercadoId, Pageable pageable) {
        return promocaoRepository.findByMercadoId(mercadoId, pageable)
                .map(PromocaoResponseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PromocaoResponse buscarPorId(Long id) {
        Promocao promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", "id", id));
        return PromocaoResponseMapper.toResponse(promocao);
    }

    @Override
    @Transactional
    public PromocaoResponse atualizar(Long id, CreatePromocaoRequest request, String username) {
        Promocao promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", "id", id));

        if (!promocao.getMercado().isOwner(username)) {
            throw new UnauthorizedException("Você não tem permissão para editar esta promoção");
        }

        promocao.updateFrom(request);
        promocaoRepository.save(promocao);

        return PromocaoResponseMapper.toResponse(promocao);
    }

    @Override
    @Transactional
    public void deletar(Long id, String username) {
        Promocao promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", "id", id));

        if (!promocao.getMercado().isOwner(username)) {
            throw new UnauthorizedException("Você não tem permissão para excluir esta promoção");
        }

        promocao.markAsDeleted(username);
        promocaoRepository.save(promocao);
        log.info("Promoção {} removida (soft delete)", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ValidatePromocaoResponse validarCodigo(String code, BigDecimal totalCompra) {
        Promocao promocao = promocaoRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", "code", code));

        if (promocao.getStatus() != StatusPromocao.ATIVA) {
            return ValidatePromocaoResponse.builder()
                    .valido(false)
                    .motivo("Promoção não está ativa")
                    .build();
        }

        if (promocao.getFim().isBefore(LocalDateTime.now())) {
            return ValidatePromocaoResponse.builder()
                    .valido(false)
                    .motivo("Promoção expirada")
                    .build();
        }

        if (totalCompra.compareTo(promocao.getValorMinimo()) < 0) {
            return ValidatePromocaoResponse.builder()
                    .valido(false)
                    .motivo("Valor mínimo não atingido")
                    .build();
        }

        return ValidatePromocaoResponse.builder()
                .valido(true)
                .percentualDesconto(promocao.getPercentualDesconto())
                .build();
    }

    @Override
    @Transactional
    public PromocaoResponse aplicarPromocao(Long id, BigDecimal totalCompra, String username) {
        Promocao promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção", "id", id));

        ValidatePromocaoResponse validacao = validarCodigo(promocao.getCode(), totalCompra);
        if (!validacao.getValido()) {
            throw new IllegalArgumentException(validacao.getMotivo());
        }

        promocao.incrementarUso();
        promocaoRepository.save(promocao);

        log.info("Promoção aplicada por user={} promocaoId={}", username, id);
        return PromocaoResponseMapper.toResponse(promocao);
    }

    @Override
    public BigDecimal calcularDesconto(BigDecimal totalCompra, BigDecimal percentualDesconto) {
        return totalCompra.multiply(percentualDesconto).divide(BigDecimal.valueOf(100));
    }
}
```

---

## Controllers

### NotificacaoController
```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.notificacao.response.NotificacaoResponse;
import com.netflix.mercados.dto.notificacao.response.NotificacioneStatsResponse;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Endpoints de notificações em tempo real")
@SecurityRequirement(name = "bearerAuth")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping("/notificacoes")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Listar minhas notificações", description = "Lista notificações do usuário autenticado com paginação")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<ApiResponse<Page<NotificacaoResponse>>> listarMinhas(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Page<NotificacaoResponse> response = notificacaoService
                .listarMinhas(authentication.getName(), PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(response, "Notificações recuperadas"));
    }

    @GetMapping("/notificacoes/unread/count")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Contar notificações não lidas")
    public ResponseEntity<ApiResponse<NotificacioneStatsResponse>> contarNaoLidas(Authentication authentication) {
        NotificacioneStatsResponse response = notificacaoService.contarNaoLidas(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Total de não lidas"));
    }

    @PutMapping("/notificacoes/{id}/read")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Marcar notificação como lida")
    public ResponseEntity<ApiResponse<NotificacaoResponse>> marcarComoLida(
            @PathVariable Long id,
            Authentication authentication) {

        NotificacaoResponse response = notificacaoService.marcarComoLido(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Notificação marcada como lida"));
    }

    @PostMapping("/notificacoes/mark-all-read")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Marcar todas como lidas")
    public ResponseEntity<ApiResponse<Void>> marcarTodas(Authentication authentication) {
        notificacaoService.marcarTodasComoLidas(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Todas marcadas como lidas"));
    }

    @DeleteMapping("/notificacoes/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Deletar notificação (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @PathVariable Long id,
            Authentication authentication) {

        notificacaoService.deletar(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Notificação removida"));
    }
}
```

### PromocaoController
```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.promocao.request.CreatePromocaoRequest;
import com.netflix.mercados.dto.promocao.response.PromocaoResponse;
import com.netflix.mercados.dto.promocao.response.ValidatePromocaoResponse;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.service.PromocaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Promoções", description = "Endpoints para gerenciamento de promoções")
@SecurityRequirement(name = "bearerAuth")
public class PromocaoController {

    private final PromocaoService promocaoService;

    @PostMapping("/mercados/{mercadoId}/promocoes")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Criar promoção")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Promoção criada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<PromocaoResponse>> criar(
            @PathVariable Long mercadoId,
            @Valid @RequestBody CreatePromocaoRequest request,
            Authentication authentication) {

        PromocaoResponse response = promocaoService.criar(mercadoId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Promoção criada"));
    }

    @GetMapping("/mercados/{mercadoId}/promocoes")
    @Operation(summary = "Listar promoções por mercado")
    public ResponseEntity<ApiResponse<Page<PromocaoResponse>>> listarPorMercado(
            @PathVariable Long mercadoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<PromocaoResponse> response = promocaoService
                .listarPorMercado(mercadoId, PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(response, "Promoções recuperadas"));
    }

    @GetMapping("/promocoes/{id}")
    @Operation(summary = "Detalhar promoção")
    public ResponseEntity<ApiResponse<PromocaoResponse>> detalhar(@PathVariable Long id) {
        PromocaoResponse response = promocaoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Promoção encontrada"));
    }

    @PutMapping("/promocoes/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Atualizar promoção")
    public ResponseEntity<ApiResponse<PromocaoResponse>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CreatePromocaoRequest request,
            Authentication authentication) {

        PromocaoResponse response = promocaoService.atualizar(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Promoção atualizada"));
    }

    @DeleteMapping("/promocoes/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Deletar promoção (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @PathVariable Long id,
            Authentication authentication) {

        promocaoService.deletar(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Promoção removida"));
    }

    @GetMapping("/promocoes/code/{code}/validate")
    @Operation(summary = "Validar código de promoção")
    public ResponseEntity<ApiResponse<ValidatePromocaoResponse>> validar(
            @PathVariable String code,
            @RequestParam BigDecimal totalCompra) {

        ValidatePromocaoResponse response = promocaoService.validarCodigo(code, totalCompra);
        return ResponseEntity.ok(ApiResponse.success(response, "Validação concluída"));
    }

    @PostMapping("/promocoes/{id}/apply")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Aplicar promoção")
    public ResponseEntity<ApiResponse<PromocaoResponse>> aplicar(
            @PathVariable Long id,
            @RequestParam BigDecimal totalCompra,
            Authentication authentication) {

        PromocaoResponse response = promocaoService.aplicarPromocao(id, totalCompra, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Promoção aplicada"));
    }
}
```

---

## WebSocket (Real-time)

### WebSocketConfig
```java
package com.netflix.mercados.config;

import com.netflix.mercados.websocket.NotificacaoHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificacaoHandler notificacaoHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificacaoHandler, "/ws/notificacoes/{userId}")
                .setAllowedOrigins("*");
    }
}
```

### NotificacaoHandler
```java
package com.netflix.mercados.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.mercados.dto.notificacao.response.NotificacaoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<Long, CopyOnWriteArrayList<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session.getUri().getPath());
        sessionsByUser.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(session);
        log.info("WebSocket conectado para userId={}", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session.getUri().getPath());
        sessionsByUser.getOrDefault(userId, new CopyOnWriteArrayList<>()).remove(session);
        log.info("WebSocket desconectado para userId={}", userId);
    }

    public void sendToUser(Long userId, NotificacaoResponse payload) {
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            log.error("Erro ao serializar notificação", ex);
            return;
        }

        sessionsByUser.getOrDefault(userId, new CopyOnWriteArrayList<>())
                .forEach(session -> send(session, json));
    }

    private void send(WebSocketSession session, String payload) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }
        } catch (Exception ex) {
            log.warn("Falha ao enviar mensagem WebSocket", ex);
        }
    }

    private Long extractUserId(String path) {
        String[] parts = path.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
```

---

## Notas de Paginação e Soft Delete

- **Paginação:** use `PageRequest.of(page, size)` e responda com `ApiResponse<Page<...>>`.
- **Soft delete:** mantenha campos como `deletedAt`, `deletedBy` e `ativo` nas entidades. Métodos `markAsDeleted(username)` devem alterar flags sem remover registros.
- **Auditoria:** utilize `@CreatedDate`, `@LastModifiedDate` e `@EntityListeners(AuditingEntityListener.class)`.
- **Transações:** operações de escrita anotadas com `@Transactional`.

---

✅ Este documento está pronto para implementação em Spring Boot 3.2 / Java 21.
