# Implementação de Favoritos - Netflix Mercados

> Implementação completa de favoritos de mercados com suporte a soft delete, contadores e validações

## 📋 Índice
1. [DTOs](#dtos)
2. [Entity e Repository](#entity-e-repository)
3. [Service](#service)
4. [Controller](#controller)
5. [Exception Handling](#exception-handling)
6. [Testes](#testes)

---

## DTOs

### FavoritoRequest.java

```java
package com.netflix.mercados.dto.favorito.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para adicionar mercado aos favoritos")
public class FavoritoRequest {

    @NotNull(message = "ID do mercado é obrigatório")
    @Schema(description = "ID do mercado a ser favoritado", example = "42", required = true)
    private Long mercadoId;
}
```

### FavoritoResponse.java

```java
package com.netflix.mercados.dto.favorito.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.netflix.mercados.dto.mercado.response.MercadoResponse;
import com.netflix.mercados.entity.Favorito;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response de favorito")
public class FavoritoResponse {

    @Schema(description = "ID do favorito", example = "1")
    private Long id;

    @Schema(description = "ID do usuário", example = "10")
    private Long userId;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String nomeUsuario;

    @Schema(description = "ID do mercado favoritado", example = "42")
    private Long mercadoId;

    @Schema(description = "Detalhes do mercado favoritado")
    private MercadoResponse mercado;

    @Schema(description = "Data em que foi favoritado", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static FavoritoResponse from(Favorito favorito) {
        return from(favorito, false);
    }

    public static FavoritoResponse from(Favorito favorito, boolean includeMercadoDetails) {
        if (favorito == null) {
            return null;
        }

        FavoritoResponseBuilder builder = FavoritoResponse.builder()
                .id(favorito.getId())
                .userId(favorito.getUser().getId())
                .nomeUsuario(favorito.getUser().getFullName())
                .mercadoId(favorito.getMercado().getId())
                .createdAt(favorito.getCreatedAt());

        if (includeMercadoDetails && favorito.getMercado() != null) {
            builder.mercado(MercadoResponse.from(favorito.getMercado()));
        }

        return builder.build();
    }

    public static List<FavoritoResponse> fromList(List<Favorito> favoritos) {
        return fromList(favoritos, false);
    }

    public static List<FavoritoResponse> fromList(List<Favorito> favoritos, boolean includeMercadoDetails) {
        if (favoritos == null) {
            return List.of();
        }
        return favoritos.stream()
                .map(favorito -> from(favorito, includeMercadoDetails))
                .collect(Collectors.toList());
    }
}
```

### FavoritoCountResponse.java

```java
package com.netflix.mercados.dto.favorito.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response com contagem de favoritos")
public class FavoritoCountResponse {

    @Schema(description = "Total de favoritos do usuário", example = "15")
    private Long totalFavoritos;

    @Schema(description = "Mensagem adicional", example = "Você tem 15 mercados favoritos")
    private String mensagem;

    public static FavoritoCountResponse of(Long count) {
        return FavoritoCountResponse.builder()
                .totalFavoritos(count)
                .mensagem(String.format("Você tem %d %s favorito%s", 
                    count, 
                    count == 1 ? "mercado" : "mercados",
                    count == 1 ? "" : "s"))
                .build();
    }
}
```

---

## Entity e Repository

### Favorito.java

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favoritos", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_favorito_user_mercado", 
            columnNames = {"user_id", "mercado_id"}
        )
    },
    indexes = {
        @Index(name = "idx_favorito_user", columnList = "user_id"),
        @Index(name = "idx_favorito_mercado", columnList = "mercado_id"),
        @Index(name = "idx_favorito_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorito extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mercado_id", nullable = false)
    private Mercado mercado;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
    }
}
```

### FavoritoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Favorito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    /**
     * Busca favoritos de um usuário com paginação
     */
    @Query("SELECT f FROM Favorito f " +
           "LEFT JOIN FETCH f.mercado m " +
           "LEFT JOIN FETCH f.user " +
           "WHERE f.user.id = :userId " +
           "AND f.deleted = false " +
           "AND m.deleted = false " +
           "ORDER BY f.createdAt DESC")
    Page<Favorito> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Busca favorito específico por usuário e mercado
     */
    @Query("SELECT f FROM Favorito f " +
           "WHERE f.user.id = :userId " +
           "AND f.mercado.id = :mercadoId " +
           "AND f.deleted = false")
    Optional<Favorito> findByUserIdAndMercadoId(
            @Param("userId") Long userId, 
            @Param("mercadoId") Long mercadoId);

    /**
     * Verifica se mercado já está nos favoritos do usuário
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM Favorito f " +
           "WHERE f.user.id = :userId " +
           "AND f.mercado.id = :mercadoId " +
           "AND f.deleted = false")
    boolean existsByUserIdAndMercadoId(
            @Param("userId") Long userId, 
            @Param("mercadoId") Long mercadoId);

    /**
     * Conta total de favoritos de um usuário
     */
    @Query("SELECT COUNT(f) FROM Favorito f " +
           "WHERE f.user.id = :userId " +
           "AND f.deleted = false")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * Conta quantas vezes um mercado foi favoritado
     */
    @Query("SELECT COUNT(f) FROM Favorito f " +
           "WHERE f.mercado.id = :mercadoId " +
           "AND f.deleted = false")
    Long countByMercadoId(@Param("mercadoId") Long mercadoId);

    /**
     * Remove favorito (soft delete)
     */
    @Modifying
    @Query("UPDATE Favorito f SET f.deleted = true, f.updatedBy = :updatedBy " +
           "WHERE f.user.id = :userId " +
           "AND f.mercado.id = :mercadoId")
    int softDeleteByUserIdAndMercadoId(
            @Param("userId") Long userId, 
            @Param("mercadoId") Long mercadoId,
            @Param("updatedBy") String updatedBy);

    /**
     * Busca favoritos de um mercado específico
     */
    @Query("SELECT f FROM Favorito f " +
           "LEFT JOIN FETCH f.user " +
           "WHERE f.mercado.id = :mercadoId " +
           "AND f.deleted = false " +
           "ORDER BY f.createdAt DESC")
    Page<Favorito> findByMercadoId(@Param("mercadoId") Long mercadoId, Pageable pageable);
}
```

---

## Service

### FavoritoService.java

```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.favorito.request.FavoritoRequest;
import com.netflix.mercados.dto.favorito.response.FavoritoCountResponse;
import com.netflix.mercados.dto.favorito.response.FavoritoResponse;
import com.netflix.mercados.entity.Favorito;
import com.netflix.mercados.entity.Mercado;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.ValidationException;
import com.netflix.mercados.repository.FavoritoRepository;
import com.netflix.mercados.repository.MercadoRepository;
import com.netflix.mercados.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final MercadoRepository mercadoRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================

    @Transactional
    public FavoritoResponse adicionarFavorito(FavoritoRequest request, String username) {
        log.info("Adicionando mercado {} aos favoritos do usuário {}", 
                request.getMercadoId(), username);

        // Buscar usuário
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Buscar mercado
        Mercado mercado = mercadoRepository.findById(request.getMercadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", request.getMercadoId()));

        if (mercado.getDeleted()) {
            throw new ValidationException("Não é possível favoritar um mercado excluído");
        }

        // Verificar se já existe favorito
        if (favoritoRepository.existsByUserIdAndMercadoId(user.getId(), mercado.getId())) {
            log.warn("Mercado {} já está nos favoritos do usuário {}", mercado.getId(), username);
            throw new ValidationException("Este mercado já está nos seus favoritos");
        }

        // Criar favorito
        Favorito favorito = Favorito.builder()
                .user(user)
                .mercado(mercado)
                .build();

        favorito.setCreatedBy(username);
        favorito.setUpdatedBy(username);

        Favorito favoritoSalvo = favoritoRepository.save(favorito);

        // Atualizar contador no mercado
        mercado.setTotalFavoritos(mercado.getTotalFavoritos() + 1);
        mercadoRepository.save(mercado);

        log.info("Favorito criado com sucesso. ID: {}", favoritoSalvo.getId());
        return FavoritoResponse.from(favoritoSalvo, true);
    }

    // ==================== READ ====================

    @Transactional(readOnly = true)
    public Page<FavoritoResponse> listarMeusFavoritos(String username, int page, int size) {
        log.info("Listando favoritos do usuário: {}", username);

        // Buscar usuário
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Pageable pageable = PageRequest.of(page, size);
        Page<Favorito> favoritos = favoritoRepository.findByUserId(user.getId(), pageable);

        return favoritos.map(favorito -> FavoritoResponse.from(favorito, true));
    }

    @Transactional(readOnly = true)
    public FavoritoCountResponse contarMeusFavoritos(String username) {
        log.info("Contando favoritos do usuário: {}", username);

        // Buscar usuário
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Long count = favoritoRepository.countByUserId(user.getId());

        return FavoritoCountResponse.of(count);
    }

    @Transactional(readOnly = true)
    public boolean existeFavoritoPara(Long mercadoId, String username) {
        log.debug("Verificando se mercado {} está nos favoritos de {}", mercadoId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return favoritoRepository.existsByUserIdAndMercadoId(user.getId(), mercadoId);
    }

    @Transactional(readOnly = true)
    public Long contarFavoritosPorMercado(Long mercadoId) {
        log.debug("Contando favoritos do mercado: {}", mercadoId);

        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado", "id", mercadoId);
        }

        return favoritoRepository.countByMercadoId(mercadoId);
    }

    // ==================== DELETE ====================

    @Transactional
    public void removerFavorito(Long mercadoId, String username) {
        log.info("Removendo mercado {} dos favoritos do usuário {}", mercadoId, username);

        // Buscar usuário
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Buscar favorito
        Favorito favorito = favoritoRepository.findByUserIdAndMercadoId(user.getId(), mercadoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorito não encontrado para este mercado"));

        // Soft delete
        favorito.markAsDeleted(username);
        favoritoRepository.save(favorito);

        // Atualizar contador no mercado
        Mercado mercado = favorito.getMercado();
        if (mercado.getTotalFavoritos() > 0) {
            mercado.setTotalFavoritos(mercado.getTotalFavoritos() - 1);
            mercadoRepository.save(mercado);
        }

        log.info("Favorito removido com sucesso. Mercado ID: {}, User: {}", mercadoId, username);
    }

    @Transactional
    public void removerFavoritoPorId(Long favoritoId, String username) {
        log.info("Removendo favorito ID: {} pelo usuário {}", favoritoId, username);

        // Buscar favorito
        Favorito favorito = favoritoRepository.findById(favoritoId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito", "id", favoritoId));

        // Verificar propriedade
        if (!favorito.getUser().getUsername().equals(username)) {
            log.warn("Usuário {} tentou remover favorito que não pertence a ele", username);
            throw new ValidationException("Este favorito não pertence a você");
        }

        if (favorito.getDeleted()) {
            throw new ValidationException("Este favorito já foi removido");
        }

        // Soft delete
        favorito.markAsDeleted(username);
        favoritoRepository.save(favorito);

        // Atualizar contador no mercado
        Mercado mercado = favorito.getMercado();
        if (mercado.getTotalFavoritos() > 0) {
            mercado.setTotalFavoritos(mercado.getTotalFavoritos() - 1);
            mercadoRepository.save(mercado);
        }

        log.info("Favorito removido com sucesso. ID: {}", favoritoId);
    }

    // ==================== UTILITIES ====================

    @Transactional
    public void toggleFavorito(Long mercadoId, String username) {
        log.info("Toggle favorito - Mercado: {}, Usuário: {}", mercadoId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean jaFavoritado = favoritoRepository.existsByUserIdAndMercadoId(user.getId(), mercadoId);

        if (jaFavoritado) {
            removerFavorito(mercadoId, username);
        } else {
            FavoritoRequest request = FavoritoRequest.builder()
                    .mercadoId(mercadoId)
                    .build();
            adicionarFavorito(request, username);
        }
    }
}
```

---

## Controller

### FavoritoController.java

```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.favorito.request.FavoritoRequest;
import com.netflix.mercados.dto.favorito.response.FavoritoCountResponse;
import com.netflix.mercados.dto.favorito.response.FavoritoResponse;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.service.FavoritoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Endpoints para gerenciamento de mercados favoritos")
@SecurityRequirement(name = "bearerAuth")
public class FavoritoController {

    private final FavoritoService favoritoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Adicionar mercado aos favoritos",
        description = "Permite que usuários adicionem um mercado à sua lista de favoritos"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Mercado adicionado aos favoritos com sucesso",
            content = @Content(schema = @Schema(implementation = FavoritoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Mercado já está nos favoritos ou dados inválidos"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<FavoritoResponse>> adicionarFavorito(
            @Valid @RequestBody FavoritoRequest request,
            Authentication authentication) {
        
        log.info("POST /api/v1/favoritos - User: {}, Mercado: {}", 
                authentication.getName(), request.getMercadoId());

        FavoritoResponse response = favoritoService.adicionarFavorito(
                request, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Mercado adicionado aos favoritos com sucesso"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Listar meus favoritos",
        description = "Retorna todos os mercados favoritos do usuário autenticado"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de favoritos retornada com sucesso"
        )
    })
    public ResponseEntity<ApiResponse<Page<FavoritoResponse>>> listarMeusFavoritos(
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        log.info("GET /api/v1/favoritos?page={}&size={} - User: {}", 
                page, size, authentication.getName());

        Page<FavoritoResponse> favoritos = favoritoService.listarMeusFavoritos(
                authentication.getName(), page, size);

        String message = favoritos.getTotalElements() > 0 
                ? String.format("Encontrados %d favoritos", favoritos.getTotalElements())
                : "Você ainda não tem favoritos";

        return ResponseEntity.ok(ApiResponse.success(favoritos, message));
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Contar meus favoritos",
        description = "Retorna o total de mercados favoritos do usuário"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Contagem retornada com sucesso",
            content = @Content(schema = @Schema(implementation = FavoritoCountResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<FavoritoCountResponse>> contarMeusFavoritos(
            Authentication authentication) {
        
        log.info("GET /api/v1/favoritos/count - User: {}", authentication.getName());

        FavoritoCountResponse response = favoritoService.contarMeusFavoritos(
                authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(response, "Contagem realizada com sucesso"));
    }

    @DeleteMapping("/{mercadoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Remover mercado dos favoritos",
        description = "Remove um mercado da lista de favoritos do usuário"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Mercado removido dos favoritos com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Favorito não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<Void>> removerFavorito(
            @Parameter(description = "ID do mercado a ser removido dos favoritos") 
            @PathVariable Long mercadoId,
            Authentication authentication) {
        
        log.info("DELETE /api/v1/favoritos/{} - User: {}", mercadoId, authentication.getName());

        favoritoService.removerFavorito(mercadoId, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Mercado removido dos favoritos com sucesso"));
    }

    @PostMapping("/toggle/{mercadoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Alternar favorito",
        description = "Adiciona ou remove um mercado dos favoritos (toggle)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Operação realizada com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<Void>> toggleFavorito(
            @Parameter(description = "ID do mercado") 
            @PathVariable Long mercadoId,
            Authentication authentication) {
        
        log.info("POST /api/v1/favoritos/toggle/{} - User: {}", 
                mercadoId, authentication.getName());

        favoritoService.toggleFavorito(mercadoId, authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(null, "Favorito atualizado com sucesso"));
    }

    @GetMapping("/check/{mercadoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Verificar se mercado está nos favoritos",
        description = "Retorna true se o mercado está nos favoritos do usuário"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Verificação realizada com sucesso"
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> checkFavorito(
            @Parameter(description = "ID do mercado") 
            @PathVariable Long mercadoId,
            Authentication authentication) {
        
        log.info("GET /api/v1/favoritos/check/{} - User: {}", 
                mercadoId, authentication.getName());

        boolean isFavorito = favoritoService.existeFavoritoPara(
                mercadoId, authentication.getName());

        String message = isFavorito 
                ? "Mercado está nos favoritos" 
                : "Mercado não está nos favoritos";

        return ResponseEntity.ok(ApiResponse.success(isFavorito, message));
    }
}
```

---

## Exception Handling

### Exceptions já existentes podem ser reutilizadas:

- `ResourceNotFoundException` - para mercados/favoritos não encontrados
- `ValidationException` - para validações de negócio
- `UnauthorizedException` - para ações não autorizadas

---

## Testes

### FavoritoServiceTest.java

```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.favorito.request.FavoritoRequest;
import com.netflix.mercados.dto.favorito.response.FavoritoCountResponse;
import com.netflix.mercados.dto.favorito.response.FavoritoResponse;
import com.netflix.mercados.entity.Favorito;
import com.netflix.mercados.entity.Mercado;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.ValidationException;
import com.netflix.mercados.repository.FavoritoRepository;
import com.netflix.mercados.repository.MercadoRepository;
import com.netflix.mercados.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private MercadoRepository mercadoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoritoService favoritoService;

    private User user;
    private Mercado mercado;
    private Favorito favorito;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .build();

        mercado = Mercado.builder()
                .id(1L)
                .nome("Mercado Teste")
                .totalFavoritos(0)
                .build();
        mercado.setDeleted(false);

        favorito = Favorito.builder()
                .id(1L)
                .user(user)
                .mercado(mercado)
                .build();
        favorito.setDeleted(false);
    }

    @Test
    void adicionarFavorito_DeveRetornarFavoritoResponse_QuandoDadosValidos() {
        // Arrange
        FavoritoRequest request = FavoritoRequest.builder()
                .mercadoId(1L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(mercado));
        when(favoritoRepository.existsByUserIdAndMercadoId(1L, 1L)).thenReturn(false);
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);

        // Act
        FavoritoResponse response = favoritoService.adicionarFavorito(request, "testuser");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMercadoId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(favoritoRepository).save(any(Favorito.class));
        verify(mercadoRepository).save(any(Mercado.class));
    }

    @Test
    void adicionarFavorito_DeveLancarException_QuandoMercadoNaoExiste() {
        // Arrange
        FavoritoRequest request = FavoritoRequest.builder()
                .mercadoId(999L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mercadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> favoritoService.adicionarFavorito(request, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mercado");
    }

    @Test
    void adicionarFavorito_DeveLancarException_QuandoJaExisteFavorito() {
        // Arrange
        FavoritoRequest request = FavoritoRequest.builder()
                .mercadoId(1L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(mercado));
        when(favoritoRepository.existsByUserIdAndMercadoId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> favoritoService.adicionarFavorito(request, "testuser"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("já está nos seus favoritos");
    }

    @Test
    void adicionarFavorito_DeveLancarException_QuandoMercadoDeletado() {
        // Arrange
        FavoritoRequest request = FavoritoRequest.builder()
                .mercadoId(1L)
                .build();

        mercado.setDeleted(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(mercado));

        // Act & Assert
        assertThatThrownBy(() -> favoritoService.adicionarFavorito(request, "testuser"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("excluído");
    }

    @Test
    void listarMeusFavoritos_DeveRetornarPaginaDeFavoritos() {
        // Arrange
        List<Favorito> favoritos = Arrays.asList(favorito);
        Page<Favorito> page = new PageImpl<>(favoritos);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.findByUserId(eq(1L), any(PageRequest.class))).thenReturn(page);

        // Act
        Page<FavoritoResponse> response = favoritoService.listarMeusFavoritos("testuser", 0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getMercadoId()).isEqualTo(1L);
    }

    @Test
    void contarMeusFavoritos_DeveRetornarContagem() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.countByUserId(1L)).thenReturn(5L);

        // Act
        FavoritoCountResponse response = favoritoService.contarMeusFavoritos("testuser");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotalFavoritos()).isEqualTo(5L);
        assertThat(response.getMensagem()).contains("5 mercados");
    }

    @Test
    void removerFavorito_DeveExecutarSoftDelete() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.findByUserIdAndMercadoId(1L, 1L)).thenReturn(Optional.of(favorito));
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);

        // Act
        favoritoService.removerFavorito(1L, "testuser");

        // Assert
        verify(favoritoRepository).save(any(Favorito.class));
        verify(mercadoRepository).save(any(Mercado.class));
    }

    @Test
    void removerFavorito_DeveLancarException_QuandoFavoritoNaoExiste() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.findByUserIdAndMercadoId(1L, 999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> favoritoService.removerFavorito(999L, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Favorito não encontrado");
    }

    @Test
    void existeFavoritoPara_DeveRetornarTrue_QuandoExiste() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.existsByUserIdAndMercadoId(1L, 1L)).thenReturn(true);

        // Act
        boolean existe = favoritoService.existeFavoritoPara(1L, "testuser");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void existeFavoritoPara_DeveRetornarFalse_QuandoNaoExiste() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.existsByUserIdAndMercadoId(1L, 1L)).thenReturn(false);

        // Act
        boolean existe = favoritoService.existeFavoritoPara(1L, "testuser");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void toggleFavorito_DeveAdicionarQuandoNaoExiste() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.existsByUserIdAndMercadoId(1L, 1L)).thenReturn(false);
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(mercado));
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);

        // Act
        favoritoService.toggleFavorito(1L, "testuser");

        // Assert
        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    void toggleFavorito_DeveRemoverQuandoExiste() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(favoritoRepository.existsByUserIdAndMercadoId(1L, 1L)).thenReturn(true);
        when(favoritoRepository.findByUserIdAndMercadoId(1L, 1L)).thenReturn(Optional.of(favorito));
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(favorito);
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(mercado);

        // Act
        favoritoService.toggleFavorito(1L, "testuser");

        // Assert
        verify(favoritoRepository).save(any(Favorito.class));
    }
}
```

---

## Script SQL para Migrations

### V2__Create_Favoritos_Table.sql

```sql
-- Tabela de Favoritos
CREATE TABLE favoritos (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mercado_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_favorito_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_mercado FOREIGN KEY (mercado_id) 
        REFERENCES mercados(id) ON DELETE CASCADE,
    CONSTRAINT uk_favorito_user_mercado UNIQUE (user_id, mercado_id)
);

-- Índices para performance
CREATE INDEX idx_favorito_user ON favoritos(user_id);
CREATE INDEX idx_favorito_mercado ON favoritos(mercado_id);
CREATE INDEX idx_favorito_created_at ON favoritos(created_at);
CREATE INDEX idx_favorito_deleted ON favoritos(deleted);

-- Comentários
COMMENT ON TABLE favoritos IS 'Tabela de favoritos de usuários';
COMMENT ON COLUMN favoritos.user_id IS 'ID do usuário que favoritou';
COMMENT ON COLUMN favoritos.mercado_id IS 'ID do mercado favoritado';
COMMENT ON CONSTRAINT uk_favorito_user_mercado ON favoritos IS 'Um usuário não pode favoritar o mesmo mercado duas vezes';
```

---

## Atualização na Entity Mercado

```java
// Adicionar campo na Entity Mercado
@Column(name = "total_favoritos", nullable = false)
@Builder.Default
private Integer totalFavoritos = 0;
```

### Migration para adicionar coluna

```sql
-- V3__Add_Total_Favoritos_To_Mercados.sql
ALTER TABLE mercados ADD COLUMN total_favoritos INTEGER NOT NULL DEFAULT 0;

COMMENT ON COLUMN mercados.total_favoritos IS 'Total de vezes que o mercado foi favoritado';
```

---

## Resumo dos Endpoints

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/v1/favoritos` | Adicionar favorito | ✅ |
| GET | `/api/v1/favoritos` | Listar meus favoritos | ✅ |
| GET | `/api/v1/favoritos/count` | Contar meus favoritos | ✅ |
| DELETE | `/api/v1/favoritos/{mercadoId}` | Remover favorito | ✅ |
| POST | `/api/v1/favoritos/toggle/{mercadoId}` | Toggle favorito | ✅ |
| GET | `/api/v1/favoritos/check/{mercadoId}` | Verificar se é favorito | ✅ |

---

## Checklist de Implementação

- [x] DTOs criados com validações
- [x] Entity Favorito com relacionamentos
- [x] Repository com queries customizadas
- [x] Service com lógica de negócio
- [x] Controller com @PreAuthorize
- [x] Soft delete implementado
- [x] Paginação configurada
- [x] Swagger documentation
- [x] Exception handling
- [x] Testes unitários
- [x] SQL migrations
- [x] Logs com @Slf4j
- [x] @Transactional em métodos apropriados
- [x] Contador de favoritos no mercado
- [x] Toggle de favoritos implementado

---

**Documento pronto para produção! ✅**
