package com.netflix.mercado.controller;

import com.netflix.mercado.dto.comentario.CreateComentarioRequest;
import com.netflix.mercado.dto.comentario.UpdateComentarioRequest;
import com.netflix.mercado.dto.comentario.ComentarioResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.ComentarioService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Comentários", description = "Gerenciamento de comentários em avaliações")
public class ComentarioController {

    private static final Logger log = Logger.getLogger(ComentarioController.class.getName());

    private final ComentarioService comentarioService;

    /**
     * Cria novo comentário em uma avaliação
     */
    @PostMapping("/avaliacoes/{avaliacaoId}/comentarios")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar comentário",
        description = "Adiciona novo comentário a uma avaliação"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Comentário criado com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<ComentarioResponse> createComentario(
            @Parameter(description = "ID da avaliação")
            @PathVariable Long avaliacaoId,
            @Valid @RequestBody CreateComentarioRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Criando comentário na avaliação: {} por usuário: " + 
                    avaliacaoId, user.getId());
            ComentarioResponse response = comentarioService.createComentario(
                    avaliacaoId, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.severe("Erro ao criar comentário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista comentários de uma avaliação
     */
    @GetMapping("/avaliacoes/{avaliacaoId}/comentarios")
    @Operation(
        summary = "Listar comentários",
        description = "Retorna todos os comentários de uma avaliação"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentários retornados com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<Page<ComentarioResponse>> listComentarios(
            @Parameter(description = "ID da avaliação")
            @PathVariable Long avaliacaoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.fine("Listando comentários da avaliação: " + avaliacaoId + "");
            Pageable pageable = PageRequest.of(page, size);
            Page<ComentarioResponse> response = comentarioService
                    .listComentarios(avaliacaoId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao listar comentários: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém um comentário específico
     */
    @GetMapping("/comentarios/{id}")
    @Operation(
        summary = "Obter comentário",
        description = "Retorna detalhes de um comentário específico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário retornado com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<ComentarioResponse> getComentarioById(
            @Parameter(description = "ID do comentário")
            @PathVariable Long id) {
        try {
            log.fine("Obtendo comentário: " + id + "");
            ComentarioResponse response = comentarioService.getComentarioById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao obter comentário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Atualiza um comentário (apenas autor ou admin)
     */
    @PutMapping("/comentarios/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar comentário",
        description = "Atualiza um comentário existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<ComentarioResponse> updateComentario(
            @Parameter(description = "ID do comentário")
            @PathVariable Long id,
            @Valid @RequestBody UpdateComentarioRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Atualizando comentário: {} por usuário: " + id, user.getId());
            ComentarioResponse response = comentarioService.updateComentario(id, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao atualizar comentário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta um comentário (soft delete)
     */
    @DeleteMapping("/comentarios/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar comentário",
        description = "Deleta um comentário (soft delete)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comentário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> deleteComentario(
            @Parameter(description = "ID do comentário")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Deletando comentário: {} por usuário: " + id, user.getId());
            comentarioService.deleteComentario(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.severe("Erro ao deletar comentário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Responde a um comentário
     */
    @PostMapping("/comentarios/{id}/reply")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Responder comentário",
        description = "Cria uma resposta a um comentário existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Resposta criada com sucesso",
            content = @Content(schema = @Schema(implementation = ComentarioResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ComentarioResponse> replyComentario(
            @Parameter(description = "ID do comentário a responder")
            @PathVariable Long id,
            @Valid @RequestBody CreateComentarioRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Respondendo comentário: {} por usuário: " + id, user.getId());
            ComentarioResponse response = comentarioService.replyComentario(id, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.severe("Erro ao responder comentário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém o usuário autenticado do contexto de segurança
     */
    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getUser();
    }
}
