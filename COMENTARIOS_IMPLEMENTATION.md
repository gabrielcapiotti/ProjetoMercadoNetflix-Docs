# Implementação de Comentários - Netflix Mercados

> Implementação completa de comentários nested/replies para avaliações com suporte a soft delete, paginação e validações

## 📋 Índice
1. [DTOs](#dtos)
2. [Entity e Repository](#entity-e-repository)
3. [Service](#service)
4. [Controller](#controller)
5. [Exception Handling](#exception-handling)
6. [Testes](#testes)

---

## DTOs

### CreateComentarioRequest.java

```java
package com.netflix.mercados.dto.comentario.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para criar um novo comentário")
public class CreateComentarioRequest {

    @NotBlank(message = "Conteúdo do comentário é obrigatório")
    @Size(min = 3, max = 1000, message = "Comentário deve ter entre 3 e 1000 caracteres")
    @Schema(description = "Conteúdo do comentário", example = "Excelente avaliação! Concordo totalmente.")
    private String conteudo;

    @Schema(description = "ID do comentário pai (usado para respostas/replies)", example = "123")
    private Long parentComentarioId;
}
```

### UpdateComentarioRequest.java

```java
package com.netflix.mercados.dto.comentario.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para atualizar um comentário existente")
public class UpdateComentarioRequest {

    @NotBlank(message = "Conteúdo do comentário é obrigatório")
    @Size(min = 3, max = 1000, message = "Comentário deve ter entre 3 e 1000 caracteres")
    @Schema(description = "Novo conteúdo do comentário", example = "Comentário atualizado com novas informações.")
    private String conteudo;
}
```

### ComentarioResponse.java

```java
package com.netflix.mercados.dto.comentario.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.netflix.mercados.entity.Comentario;
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
@Schema(description = "Response de um comentário")
public class ComentarioResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "ID da avaliação", example = "10")
    private Long avaliacaoId;

    @Schema(description = "Conteúdo do comentário", example = "Ótima avaliação!")
    private String conteudo;

    @Schema(description = "Nome do usuário que comentou", example = "João Silva")
    private String nomeUsuario;

    @Schema(description = "ID do usuário que comentou", example = "42")
    private Long usuarioId;

    @Schema(description = "ID do comentário pai (se for resposta)", example = "5")
    private Long parentComentarioId;

    @Schema(description = "Indica se o comentário foi editado", example = "true")
    private Boolean editado;

    @Schema(description = "Total de respostas a este comentário", example = "3")
    private Integer totalRespostas;

    @Schema(description = "Lista de respostas (comentários filhos)")
    private List<ComentarioResponse> respostas;

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-01-15T14:20:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static ComentarioResponse from(Comentario comentario) {
        return from(comentario, false);
    }

    public static ComentarioResponse from(Comentario comentario, boolean includeRespostas) {
        if (comentario == null) {
            return null;
        }

        ComentarioResponseBuilder builder = ComentarioResponse.builder()
                .id(comentario.getId())
                .avaliacaoId(comentario.getAvaliacao().getId())
                .conteudo(comentario.getConteudo())
                .nomeUsuario(comentario.getUser().getFullName())
                .usuarioId(comentario.getUser().getId())
                .parentComentarioId(comentario.getParentComentario() != null ? 
                    comentario.getParentComentario().getId() : null)
                .editado(comentario.getEditado())
                .totalRespostas(comentario.getRespostas() != null ? 
                    comentario.getRespostas().size() : 0)
                .createdAt(comentario.getCreatedAt())
                .updatedAt(comentario.getUpdatedAt());

        if (includeRespostas && comentario.getRespostas() != null && !comentario.getRespostas().isEmpty()) {
            List<ComentarioResponse> respostas = comentario.getRespostas().stream()
                    .filter(resposta -> !resposta.getDeleted())
                    .map(resposta -> ComentarioResponse.from(resposta, false))
                    .collect(Collectors.toList());
            builder.respostas(respostas);
        }

        return builder.build();
    }

    public static List<ComentarioResponse> fromList(List<Comentario> comentarios) {
        return fromList(comentarios, false);
    }

    public static List<ComentarioResponse> fromList(List<Comentario> comentarios, boolean includeRespostas) {
        if (comentarios == null) {
            return List.of();
        }
        return comentarios.stream()
                .map(comentario -> from(comentario, includeRespostas))
                .collect(Collectors.toList());
    }
}
```

---

## Entity e Repository

### Comentario.java

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comentarios", indexes = {
    @Index(name = "idx_comentario_avaliacao", columnList = "avaliacao_id"),
    @Index(name = "idx_comentario_user", columnList = "user_id"),
    @Index(name = "idx_comentario_parent", columnList = "parent_comentario_id"),
    @Index(name = "idx_comentario_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario extends BaseEntity {

    @NotBlank(message = "Conteúdo do comentário não pode ser vazio")
    @Size(min = 3, max = 1000, message = "Comentário deve ter entre 3 e 1000 caracteres")
    @Column(name = "conteudo", nullable = false, length = 1000)
    private String conteudo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    private Avaliacao avaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comentario_id")
    private Comentario parentComentario;

    @OneToMany(mappedBy = "parentComentario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comentario> respostas = new ArrayList<>();

    @Column(name = "editado", nullable = false)
    @Builder.Default
    private Boolean editado = false;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (this.editado == null) {
            this.editado = false;
        }
    }

    public void addResposta(Comentario resposta) {
        respostas.add(resposta);
        resposta.setParentComentario(this);
    }

    public void removeResposta(Comentario resposta) {
        respostas.remove(resposta);
        resposta.setParentComentario(null);
    }

    public boolean isResposta() {
        return this.parentComentario != null;
    }

    public boolean isComentarioPrincipal() {
        return this.parentComentario == null;
    }
}
```

### ComentarioRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    /**
     * Busca comentários principais (não respostas) de uma avaliação
     */
    @Query("SELECT c FROM Comentario c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.avaliacao.id = :avaliacaoId " +
           "AND c.parentComentario IS NULL " +
           "AND c.deleted = false " +
           "ORDER BY c.createdAt DESC")
    Page<Comentario> findComentariosPrincipaisByAvaliacaoId(
            @Param("avaliacaoId") Long avaliacaoId, 
            Pageable pageable);

    /**
     * Busca respostas de um comentário específico
     */
    @Query("SELECT c FROM Comentario c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.parentComentario.id = :parentId " +
           "AND c.deleted = false " +
           "ORDER BY c.createdAt ASC")
    List<Comentario> findRespostasByParentId(@Param("parentId") Long parentId);

    /**
     * Busca comentário por ID com joins
     */
    @Query("SELECT c FROM Comentario c " +
           "LEFT JOIN FETCH c.user " +
           "LEFT JOIN FETCH c.avaliacao " +
           "WHERE c.id = :id AND c.deleted = false")
    Optional<Comentario> findByIdWithDetails(@Param("id") Long id);

    /**
     * Conta comentários de uma avaliação
     */
    @Query("SELECT COUNT(c) FROM Comentario c " +
           "WHERE c.avaliacao.id = :avaliacaoId " +
           "AND c.deleted = false")
    Long countByAvaliacaoId(@Param("avaliacaoId") Long avaliacaoId);

    /**
     * Conta respostas de um comentário
     */
    @Query("SELECT COUNT(c) FROM Comentario c " +
           "WHERE c.parentComentario.id = :parentId " +
           "AND c.deleted = false")
    Long countRespostasByParentId(@Param("parentId") Long parentId);

    /**
     * Busca comentários de um usuário
     */
    @Query("SELECT c FROM Comentario c " +
           "LEFT JOIN FETCH c.avaliacao " +
           "WHERE c.user.id = :userId " +
           "AND c.deleted = false " +
           "ORDER BY c.createdAt DESC")
    Page<Comentario> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Verifica se usuário é dono do comentário
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM Comentario c " +
           "WHERE c.id = :comentarioId " +
           "AND c.user.id = :userId " +
           "AND c.deleted = false")
    boolean isOwner(@Param("comentarioId") Long comentarioId, @Param("userId") Long userId);
}
```

---

## Service

### ComentarioService.java

```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.comentario.request.CreateComentarioRequest;
import com.netflix.mercados.dto.comentario.request.UpdateComentarioRequest;
import com.netflix.mercados.dto.comentario.response.ComentarioResponse;
import com.netflix.mercados.entity.Avaliacao;
import com.netflix.mercados.entity.Comentario;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.UnauthorizedException;
import com.netflix.mercados.exception.ValidationException;
import com.netflix.mercados.repository.AvaliacaoRepository;
import com.netflix.mercados.repository.ComentarioRepository;
import com.netflix.mercados.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================

    @Transactional
    public ComentarioResponse criarComentario(Long avaliacaoId, 
                                             CreateComentarioRequest request, 
                                             String username) {
        log.info("Criando comentário para avaliação ID: {} pelo usuário: {}", avaliacaoId, username);

        // Buscar avaliação
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação", "id", avaliacaoId));

        if (avaliacao.getDeleted()) {
            throw new ValidationException("Não é possível comentar em uma avaliação excluída");
        }

        // Buscar usuário
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Validar comentário pai se for resposta
        Comentario parentComentario = null;
        if (request.getParentComentarioId() != null) {
            parentComentario = comentarioRepository.findByIdWithDetails(request.getParentComentarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", 
                        request.getParentComentarioId()));

            if (!parentComentario.getAvaliacao().getId().equals(avaliacaoId)) {
                throw new ValidationException("Comentário pai pertence a outra avaliação");
            }

            if (parentComentario.isResposta()) {
                throw new ValidationException("Não é permitido responder a uma resposta. " +
                        "Responda ao comentário principal.");
            }
        }

        // Criar comentário
        Comentario comentario = Comentario.builder()
                .conteudo(request.getConteudo())
                .avaliacao(avaliacao)
                .user(user)
                .parentComentario(parentComentario)
                .editado(false)
                .build();

        comentario.setCreatedBy(username);
        comentario.setUpdatedBy(username);

        Comentario comentarioSalvo = comentarioRepository.save(comentario);

        // Incrementar contador na avaliação
        avaliacao.setTotalComentarios(avaliacao.getTotalComentarios() + 1);
        avaliacaoRepository.save(avaliacao);

        log.info("Comentário criado com sucesso. ID: {}", comentarioSalvo.getId());
        return ComentarioResponse.from(comentarioSalvo);
    }

    @Transactional
    public ComentarioResponse criarResposta(Long comentarioId, 
                                           CreateComentarioRequest request, 
                                           String username) {
        log.info("Criando resposta para comentário ID: {} pelo usuário: {}", comentarioId, username);

        // Buscar comentário pai
        Comentario parentComentario = comentarioRepository.findByIdWithDetails(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", comentarioId));

        if (parentComentario.isResposta()) {
            throw new ValidationException("Não é permitido responder a uma resposta. " +
                    "Responda ao comentário principal.");
        }

        // Criar request com parent ID
        CreateComentarioRequest replyRequest = CreateComentarioRequest.builder()
                .conteudo(request.getConteudo())
                .parentComentarioId(comentarioId)
                .build();

        return criarComentario(parentComentario.getAvaliacao().getId(), replyRequest, username);
    }

    // ==================== READ ====================

    @Transactional(readOnly = true)
    public Page<ComentarioResponse> buscarComentariosPorAvaliacao(Long avaliacaoId, 
                                                                  int page, 
                                                                  int size) {
        log.info("Buscando comentários da avaliação ID: {}", avaliacaoId);

        // Validar que avaliação existe
        if (!avaliacaoRepository.existsById(avaliacaoId)) {
            throw new ResourceNotFoundException("Avaliação", "id", avaliacaoId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Comentario> comentarios = comentarioRepository
                .findComentariosPrincipaisByAvaliacaoId(avaliacaoId, pageable);

        return comentarios.map(comentario -> ComentarioResponse.from(comentario, true));
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> buscarRespostas(Long comentarioId) {
        log.info("Buscando respostas do comentário ID: {}", comentarioId);

        // Validar que comentário existe
        if (!comentarioRepository.existsById(comentarioId)) {
            throw new ResourceNotFoundException("Comentário", "id", comentarioId);
        }

        List<Comentario> respostas = comentarioRepository.findRespostasByParentId(comentarioId);
        return ComentarioResponse.fromList(respostas);
    }

    @Transactional(readOnly = true)
    public ComentarioResponse buscarPorId(Long comentarioId) {
        log.info("Buscando comentário ID: {}", comentarioId);

        Comentario comentario = comentarioRepository.findByIdWithDetails(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", comentarioId));

        return ComentarioResponse.from(comentario, true);
    }

    // ==================== UPDATE ====================

    @Transactional
    public ComentarioResponse atualizarComentario(Long comentarioId, 
                                                 UpdateComentarioRequest request, 
                                                 String username) {
        log.info("Atualizando comentário ID: {} pelo usuário: {}", comentarioId, username);

        // Buscar comentário
        Comentario comentario = comentarioRepository.findByIdWithDetails(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", comentarioId));

        // Verificar propriedade
        if (!comentario.getUser().getUsername().equals(username)) {
            log.warn("Usuário {} tentou atualizar comentário que não pertence a ele", username);
            throw new UnauthorizedException("Você não tem permissão para editar este comentário");
        }

        // Atualizar
        comentario.setConteudo(request.getConteudo());
        comentario.setEditado(true);
        comentario.setUpdatedBy(username);

        Comentario comentarioAtualizado = comentarioRepository.save(comentario);

        log.info("Comentário atualizado com sucesso. ID: {}", comentarioId);
        return ComentarioResponse.from(comentarioAtualizado);
    }

    // ==================== DELETE ====================

    @Transactional
    public void deletarComentario(Long comentarioId, String username) {
        log.info("Deletando comentário ID: {} pelo usuário: {}", comentarioId, username);

        // Buscar comentário
        Comentario comentario = comentarioRepository.findByIdWithDetails(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", comentarioId));

        // Verificar propriedade
        if (!comentario.getUser().getUsername().equals(username)) {
            log.warn("Usuário {} tentou deletar comentário que não pertence a ele", username);
            throw new UnauthorizedException("Você não tem permissão para excluir este comentário");
        }

        // Soft delete
        comentario.markAsDeleted(username);
        comentarioRepository.save(comentario);

        // Decrementar contador na avaliação
        Avaliacao avaliacao = comentario.getAvaliacao();
        if (avaliacao.getTotalComentarios() > 0) {
            avaliacao.setTotalComentarios(avaliacao.getTotalComentarios() - 1);
            avaliacaoRepository.save(avaliacao);
        }

        log.info("Comentário deletado com sucesso (soft delete). ID: {}", comentarioId);
    }

    // ==================== UTILITIES ====================

    @Transactional(readOnly = true)
    public Long contarComentariosPorAvaliacao(Long avaliacaoId) {
        return comentarioRepository.countByAvaliacaoId(avaliacaoId);
    }

    @Transactional(readOnly = true)
    public Long contarRespostas(Long comentarioId) {
        return comentarioRepository.countRespostasByParentId(comentarioId);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long comentarioId, Long userId) {
        return comentarioRepository.isOwner(comentarioId, userId);
    }
}
```

---

## Controller

### ComentarioController.java

```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.comentario.request.CreateComentarioRequest;
import com.netflix.mercados.dto.comentario.request.UpdateComentarioRequest;
import com.netflix.mercados.dto.comentario.response.ComentarioResponse;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.service.ComentarioService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Comentários", description = "Endpoints para gerenciamento de comentários em avaliações")
@SecurityRequirement(name = "bearerAuth")
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping("/avaliacoes/{avaliacaoId}/comentarios")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Criar comentário em uma avaliação",
        description = "Permite que usuários autenticados comentem em uma avaliação"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Comentário criado com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Avaliação não encontrada"
        )
    })
    public ResponseEntity<ApiResponse<ComentarioResponse>> criarComentario(
            @Parameter(description = "ID da avaliação") 
            @PathVariable Long avaliacaoId,
            @Valid @RequestBody CreateComentarioRequest request,
            Authentication authentication) {
        
        log.info("POST /api/v1/avaliacoes/{}/comentarios - User: {}", 
                avaliacaoId, authentication.getName());

        ComentarioResponse response = comentarioService.criarComentario(
                avaliacaoId, request, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Comentário criado com sucesso"));
    }

    @GetMapping("/avaliacoes/{avaliacaoId}/comentarios")
    @Operation(
        summary = "Listar comentários de uma avaliação",
        description = "Retorna todos os comentários principais (não respostas) de uma avaliação com paginação"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de comentários retornada com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Avaliação não encontrada"
        )
    })
    public ResponseEntity<ApiResponse<Page<ComentarioResponse>>> listarComentarios(
            @Parameter(description = "ID da avaliação") 
            @PathVariable Long avaliacaoId,
            @Parameter(description = "Número da página (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/v1/avaliacoes/{}/comentarios?page={}&size={}", 
                avaliacaoId, page, size);

        Page<ComentarioResponse> comentarios = comentarioService
                .buscarComentariosPorAvaliacao(avaliacaoId, page, size);

        return ResponseEntity.ok(ApiResponse.success(comentarios, 
                "Comentários recuperados com sucesso"));
    }

    @GetMapping("/comentarios/{id}/respostas")
    @Operation(
        summary = "Listar respostas de um comentário",
        description = "Retorna todas as respostas de um comentário específico"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de respostas retornada com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<List<ComentarioResponse>>> listarRespostas(
            @Parameter(description = "ID do comentário pai") 
            @PathVariable Long id) {
        
        log.info("GET /api/v1/comentarios/{}/respostas", id);

        List<ComentarioResponse> respostas = comentarioService.buscarRespostas(id);

        return ResponseEntity.ok(ApiResponse.success(respostas, 
                "Respostas recuperadas com sucesso"));
    }

    @PostMapping("/comentarios/{id}/reply")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Responder a um comentário",
        description = "Cria uma resposta a um comentário existente"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Resposta criada com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou tentativa de responder a uma resposta"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<ComentarioResponse>> responderComentario(
            @Parameter(description = "ID do comentário a ser respondido") 
            @PathVariable Long id,
            @Valid @RequestBody CreateComentarioRequest request,
            Authentication authentication) {
        
        log.info("POST /api/v1/comentarios/{}/reply - User: {}", 
                id, authentication.getName());

        ComentarioResponse response = comentarioService.criarResposta(
                id, request, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Resposta criada com sucesso"));
    }

    @PutMapping("/comentarios/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Atualizar comentário",
        description = "Permite que o autor atualize seu próprio comentário"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Comentário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Usuário não é o autor do comentário"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<ComentarioResponse>> atualizarComentario(
            @Parameter(description = "ID do comentário") 
            @PathVariable Long id,
            @Valid @RequestBody UpdateComentarioRequest request,
            Authentication authentication) {
        
        log.info("PUT /api/v1/comentarios/{} - User: {}", id, authentication.getName());

        ComentarioResponse response = comentarioService.atualizarComentario(
                id, request, authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(response, 
                "Comentário atualizado com sucesso"));
    }

    @DeleteMapping("/comentarios/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @Operation(
        summary = "Deletar comentário",
        description = "Permite que o autor delete seu próprio comentário (soft delete)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Comentário deletado com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Usuário não é o autor do comentário"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado"
        )
    })
    public ResponseEntity<ApiResponse<Void>> deletarComentario(
            @Parameter(description = "ID do comentário") 
            @PathVariable Long id,
            Authentication authentication) {
        
        log.info("DELETE /api/v1/comentarios/{} - User: {}", id, authentication.getName());

        comentarioService.deletarComentario(id, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Comentário deletado com sucesso"));
    }
}
```

---

## Exception Handling

### Atualização do GlobalExceptionHandler.java

```java
// Adicionar ao GlobalExceptionHandler existente

@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(
        UnauthorizedException ex, 
        WebRequest request) {
    
    log.error("Unauthorized access: {}", ex.getMessage());
    
    ApiResponse<Object> response = ApiResponse.error(
        ex.getMessage(),
        HttpStatus.FORBIDDEN.value(),
        request.getDescription(false)
    );
    
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
}
```

---

## Testes

### ComentarioServiceTest.java

```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.comentario.request.CreateComentarioRequest;
import com.netflix.mercados.dto.comentario.request.UpdateComentarioRequest;
import com.netflix.mercados.dto.comentario.response.ComentarioResponse;
import com.netflix.mercados.entity.Avaliacao;
import com.netflix.mercados.entity.Comentario;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.UnauthorizedException;
import com.netflix.mercados.exception.ValidationException;
import com.netflix.mercados.repository.AvaliacaoRepository;
import com.netflix.mercados.repository.ComentarioRepository;
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
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    private User user;
    private Avaliacao avaliacao;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .build();

        avaliacao = Avaliacao.builder()
                .id(1L)
                .totalComentarios(0L)
                .build();
        avaliacao.setDeleted(false);

        comentario = Comentario.builder()
                .id(1L)
                .conteudo("Comentário teste")
                .avaliacao(avaliacao)
                .user(user)
                .editado(false)
                .build();
        comentario.setDeleted(false);
    }

    @Test
    void criarComentario_DeveRetornarComentarioResponse_QuandoDadosValidos() {
        // Arrange
        CreateComentarioRequest request = CreateComentarioRequest.builder()
                .conteudo("Novo comentário")
                .build();

        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        ComentarioResponse response = comentarioService.criarComentario(1L, request, "testuser");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getConteudo()).isEqualTo("Comentário teste");
        verify(comentarioRepository).save(any(Comentario.class));
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    void criarComentario_DeveLancarException_QuandoAvaliacaoNaoExiste() {
        // Arrange
        CreateComentarioRequest request = CreateComentarioRequest.builder()
                .conteudo("Novo comentário")
                .build();

        when(avaliacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> comentarioService.criarComentario(999L, request, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Avaliação");
    }

    @Test
    void criarResposta_DeveRetornarComentarioResponse_QuandoDadosValidos() {
        // Arrange
        CreateComentarioRequest request = CreateComentarioRequest.builder()
                .conteudo("Resposta ao comentário")
                .build();

        Comentario comentarioSalvo = Comentario.builder()
                .id(2L)
                .conteudo("Resposta ao comentário")
                .avaliacao(avaliacao)
                .user(user)
                .parentComentario(comentario)
                .build();

        when(comentarioRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(comentario));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioSalvo);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        ComentarioResponse response = comentarioService.criarResposta(1L, request, "testuser");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getParentComentarioId()).isEqualTo(1L);
    }

    @Test
    void criarResposta_DeveLancarException_QuandoRespondeAUmaResposta() {
        // Arrange
        Comentario resposta = Comentario.builder()
                .id(2L)
                .parentComentario(comentario)
                .build();

        CreateComentarioRequest request = CreateComentarioRequest.builder()
                .conteudo("Resposta inválida")
                .build();

        when(comentarioRepository.findByIdWithDetails(2L)).thenReturn(Optional.of(resposta));

        // Act & Assert
        assertThatThrownBy(() -> comentarioService.criarResposta(2L, request, "testuser"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("responder a uma resposta");
    }

    @Test
    void atualizarComentario_DeveRetornarComentarioAtualizado_QuandoUsuarioEhDono() {
        // Arrange
        UpdateComentarioRequest request = UpdateComentarioRequest.builder()
                .conteudo("Comentário atualizado")
                .build();

        when(comentarioRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(comentario));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ComentarioResponse response = comentarioService.atualizarComentario(1L, request, "testuser");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getConteudo()).isEqualTo("Comentário atualizado");
        assertThat(response.getEditado()).isTrue();
    }

    @Test
    void atualizarComentario_DeveLancarException_QuandoUsuarioNaoEhDono() {
        // Arrange
        UpdateComentarioRequest request = UpdateComentarioRequest.builder()
                .conteudo("Tentativa de atualização")
                .build();

        when(comentarioRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(comentario));

        // Act & Assert
        assertThatThrownBy(() -> comentarioService.atualizarComentario(1L, request, "outrouser"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("permissão");
    }

    @Test
    void deletarComentario_DeveExecutarSoftDelete_QuandoUsuarioEhDono() {
        // Arrange
        when(comentarioRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(comentario));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        comentarioService.deletarComentario(1L, "testuser");

        // Assert
        verify(comentarioRepository).save(any(Comentario.class));
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    void deletarComentario_DeveLancarException_QuandoUsuarioNaoEhDono() {
        // Arrange
        when(comentarioRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(comentario));

        // Act & Assert
        assertThatThrownBy(() -> comentarioService.deletarComentario(1L, "outrouser"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("permissão");
    }

    @Test
    void buscarComentariosPorAvaliacao_DeveRetornarPaginaDeComentarios() {
        // Arrange
        List<Comentario> comentarios = Arrays.asList(comentario);
        Page<Comentario> page = new PageImpl<>(comentarios);

        when(avaliacaoRepository.existsById(1L)).thenReturn(true);
        when(comentarioRepository.findComentariosPrincipaisByAvaliacaoId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        // Act
        Page<ComentarioResponse> response = comentarioService.buscarComentariosPorAvaliacao(1L, 0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }
}
```

---

## Script SQL para Migrations

### V1__Create_Comentarios_Table.sql

```sql
-- Tabela de Comentários
CREATE TABLE comentarios (
    id BIGSERIAL PRIMARY KEY,
    conteudo VARCHAR(1000) NOT NULL,
    avaliacao_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_comentario_id BIGINT,
    editado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_comentario_avaliacao FOREIGN KEY (avaliacao_id) 
        REFERENCES avaliacoes(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_parent FOREIGN KEY (parent_comentario_id) 
        REFERENCES comentarios(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_comentario_avaliacao ON comentarios(avaliacao_id);
CREATE INDEX idx_comentario_user ON comentarios(user_id);
CREATE INDEX idx_comentario_parent ON comentarios(parent_comentario_id);
CREATE INDEX idx_comentario_created_at ON comentarios(created_at);
CREATE INDEX idx_comentario_deleted ON comentarios(deleted);

-- Comentários
COMMENT ON TABLE comentarios IS 'Tabela de comentários em avaliações';
COMMENT ON COLUMN comentarios.conteudo IS 'Conteúdo do comentário';
COMMENT ON COLUMN comentarios.parent_comentario_id IS 'ID do comentário pai (para respostas)';
COMMENT ON COLUMN comentarios.editado IS 'Indica se o comentário foi editado';
```

---

## Configuração Swagger

### Adicionar ao OpenApiConfig.java

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Netflix Mercados API")
            .version("1.0")
            .description("API para gerenciamento de mercados, avaliações e comentários"))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
}
```

---

## Resumo dos Endpoints

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/v1/avaliacoes/{avaliacaoId}/comentarios` | Criar comentário | ✅ |
| GET | `/api/v1/avaliacoes/{avaliacaoId}/comentarios` | Listar comentários | ❌ |
| GET | `/api/v1/comentarios/{id}/respostas` | Listar respostas | ❌ |
| POST | `/api/v1/comentarios/{id}/reply` | Responder comentário | ✅ |
| PUT | `/api/v1/comentarios/{id}` | Atualizar comentário | ✅ |
| DELETE | `/api/v1/comentarios/{id}` | Deletar comentário | ✅ |

---

## Checklist de Implementação

- [x] DTOs criados com validações
- [x] Entity Comentario com relacionamentos
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

---

**Documento pronto para produção! ✅**
```
