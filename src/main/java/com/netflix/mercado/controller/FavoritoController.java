package com.netflix.mercados.controller;

import com.netflix.mercados.dto.favorito.request.CreateFavoritoRequest;
import com.netflix.mercados.dto.favorito.response.FavoritoResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.security.UserPrincipal;
import com.netflix.mercados.service.FavoritoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/favoritos")
@Slf4j
@RequiredArgsConstructor
@Transactional
@Tag(name = "Favoritos", description = "Gerenciamento de mercados favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    /**
     * Cria novo favorito
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Adicionar favorito",
        description = "Adiciona um mercado aos favoritos do usuário"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Favorito criado com sucesso",
            content = @Content(schema = @Schema(implementation = FavoritoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "409", description = "Já é favorito")
    })
    public ResponseEntity<FavoritoResponse> createFavorito(
            @Valid @RequestBody CreateFavoritoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Adicionando favorito para usuário: {}", user.getId());
            FavoritoResponse response = favoritoService.createFavorito(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar favorito", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista todos os favoritos do usuário
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Listar favoritos",
        description = "Retorna lista de todos os mercados favoritos do usuário"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Favoritos retornados com sucesso",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<FavoritoResponse>> listFavoritos() {
        try {
            User user = getCurrentUser();
            log.debug("Listando favoritos do usuário: {}", user.getId());
            List<FavoritoResponse> response = favoritoService.listFavoritos(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar favoritos", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta um favorito
     */
    @DeleteMapping("/{mercadoId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Remover favorito",
        description = "Remove um mercado dos favoritos do usuário"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Favorito removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Favorito não encontrado")
    })
    public ResponseEntity<Void> deleteFavorito(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            User user = getCurrentUser();
            log.info("Removendo favorito {} do usuário: {}", mercadoId, user.getId());
            favoritoService.deleteFavorito(mercadoId, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar favorito", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Conta total de favoritos do usuário
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Contar favoritos",
        description = "Retorna a quantidade de favoritos do usuário"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contagem retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, Long>> countFavoritos() {
        try {
            User user = getCurrentUser();
            long count = favoritoService.countFavoritos(user);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            log.error("Erro ao contar favoritos", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Toggle favorito (adiciona ou remove)
     */
    @PostMapping("/{mercadoId}/toggle")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Toggle favorito",
        description = "Adiciona ou remove um mercado dos favoritos"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Toggle realizado com sucesso",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<Map<String, Boolean>> toggleFavorito(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            User user = getCurrentUser();
            log.info("Toggle favorito {} para usuário: {}", mercadoId, user.getId());
            boolean isFavorite = favoritoService.toggleFavorito(mercadoId, user);
            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
        } catch (Exception e) {
            log.error("Erro ao fazer toggle de favorito", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Verifica se um mercado é favorito do usuário
     */
    @GetMapping("/check/{mercadoId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Verificar se é favorito",
        description = "Verifica se um mercado está nos favoritos do usuário"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Status verificado com sucesso",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, Boolean>> checkFavorito(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            User user = getCurrentUser();
            boolean isFavorite = favoritoService.isFavorite(mercadoId, user);
            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
        } catch (Exception e) {
            log.error("Erro ao verificar favorito", e);
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
