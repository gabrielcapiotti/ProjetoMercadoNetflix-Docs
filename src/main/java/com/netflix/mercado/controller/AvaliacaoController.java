package com.netflix.mercado.controller;

import com.netflix.mercado.dto.avaliacao.CreateAvaliacaoRequest;
import com.netflix.mercado.dto.avaliacao.UpdateAvaliacaoRequest;
import com.netflix.mercado.dto.avaliacao.AvaliacaoResponse;
import com.netflix.mercado.dto.avaliacao.RatingStatsResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.AvaliacaoService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/avaliacoes")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Avaliações", description = "Gerenciamento de avaliações de mercados")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    /**
     * Cria uma nova avaliação
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar avaliação",
        description = "Cria uma nova avaliação para um mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Avaliação criada com sucesso",
            content = @Content(schema = @Schema(implementation = AvaliacaoResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "409", description = "Usuário já avaliou este mercado")
    })
    public ResponseEntity<AvaliacaoResponse> createAvaliacao(
            @Valid @RequestBody CreateAvaliacaoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Criando avaliação para mercado: {} por usuário: {}", 
                    request.getMercadoId(), user.getId());
            AvaliacaoResponse response = avaliacaoService.createAvaliacao(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar avaliação", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista todas as avaliações com paginação
     */
    @GetMapping
    @Operation(
        summary = "Listar avaliações",
        description = "Retorna lista paginada de avaliações"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<Page<AvaliacaoResponse>> listAvaliacoes(
            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Ordenação")
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        try {
            log.debug("Listando avaliações - page: {}, size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<AvaliacaoResponse> response = avaliacaoService.listAvaliacoes(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar avaliações", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém uma avaliação específica
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obter avaliação",
        description = "Retorna detalhes de uma avaliação específica"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliação retornada com sucesso",
            content = @Content(schema = @Schema(implementation = AvaliacaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<AvaliacaoResponse> getAvaliacaoById(
            @Parameter(description = "ID da avaliação")
            @PathVariable Long id) {
        try {
            log.debug("Obtendo avaliação: {}", id);
            AvaliacaoResponse response = avaliacaoService.getAvaliacaoById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter avaliação", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Atualiza uma avaliação (apenas autor ou admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar avaliação",
        description = "Atualiza uma avaliação existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliação atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = AvaliacaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar")
    })
    public ResponseEntity<AvaliacaoResponse> updateAvaliacao(
            @Parameter(description = "ID da avaliação")
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvaliacaoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Atualizando avaliação: {} por usuário: {}", id, user.getId());
            AvaliacaoResponse response = avaliacaoService.updateAvaliacao(id, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao atualizar avaliação", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta uma avaliação (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar avaliação",
        description = "Deleta uma avaliação (soft delete)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Avaliação deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> deleteAvaliacao(
            @Parameter(description = "ID da avaliação")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Deletando avaliação: {} por usuário: {}", id, user.getId());
            avaliacaoService.deleteAvaliacao(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar avaliação", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista avaliações de um mercado específico
     */
    @GetMapping("/mercado/{mercadoId}")
    @Operation(
        summary = "Listar avaliações do mercado",
        description = "Retorna todas as avaliações de um mercado específico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliações retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<Page<AvaliacaoResponse>> listAvaliacoesByMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.debug("Listando avaliações do mercado: {}", mercadoId);
            Pageable pageable = PageRequest.of(page, size);
            Page<AvaliacaoResponse> response = avaliacaoService
                    .listAvaliacoesByMercado(mercadoId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar avaliações do mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém estatísticas de rating de um mercado
     */
    @GetMapping("/mercado/{mercadoId}/stats")
    @Operation(
        summary = "Obter estatísticas de rating",
        description = "Retorna média de rating, total de avaliações e distribuição"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estatísticas retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = RatingStatsResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<RatingStatsResponse> getRatingStats(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            log.debug("Obtendo estatísticas de rating do mercado: {}", mercadoId);
            RatingStatsResponse response = avaliacaoService.getRatingStats(mercadoId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter estatísticas de rating", e);
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
