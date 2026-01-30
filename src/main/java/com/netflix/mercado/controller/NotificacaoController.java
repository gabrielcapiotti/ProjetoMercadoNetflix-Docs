package com.netflix.mercados.controller;

import com.netflix.mercados.dto.notificacao.response.NotificacaoResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.security.UserPrincipal;
import com.netflix.mercados.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificacoes")
@Slf4j
@RequiredArgsConstructor
@Transactional
@Tag(name = "Notificações", description = "Gerenciamento de notificações do usuário")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    /**
     * Lista notificações do usuário autenticado
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Listar notificações",
        description = "Retorna as notificações do usuário autenticado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Notificações retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<NotificacaoResponse>> listNotificacoes(
            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filtrar apenas não lidas")
            @RequestParam(required = false) Boolean unreadOnly) {
        try {
            User user = getCurrentUser();
            log.debug("Listando notificações do usuário: {}", user.getId());
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificacaoResponse> response = notificacaoService
                    .listNotificacoes(user, pageable, unreadOnly);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar notificações", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Conta notificações não lidas
     */
    @GetMapping("/unread/count")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Contar notificações não lidas",
        description = "Retorna quantidade de notificações não lidas"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contagem retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, Long>> countUnread() {
        try {
            User user = getCurrentUser();
            long count = notificacaoService.countUnreadNotificacoes(user);
            return ResponseEntity.ok(Map.of("unreadCount", count));
        } catch (Exception e) {
            log.error("Erro ao contar notificações não lidas", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Marca uma notificação como lida
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Marcar como lida",
        description = "Marca uma notificação como lida"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Notificação marcada como lida",
            content = @Content(schema = @Schema(implementation = NotificacaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<NotificacaoResponse> markAsRead(
            @Parameter(description = "ID da notificação")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Marcando notificação {} como lida", id);
            NotificacaoResponse response = notificacaoService.markAsRead(id, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao marcar notificação como lida", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta uma notificação
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar notificação",
        description = "Remove uma notificação"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificação deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<Void> deleteNotificacao(
            @Parameter(description = "ID da notificação")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Deletando notificação: {} para usuário: {}", id, user.getId());
            notificacaoService.deleteNotificacao(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar notificação", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Marca todas as notificações como lidas
     */
    @PostMapping("/mark-all-read")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Marcar todas como lidas",
        description = "Marca todas as notificações do usuário como lidas"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Notificações marcadas como lidas",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        try {
            User user = getCurrentUser();
            log.info("Marcando todas as notificações como lidas para usuário: {}", user.getId());
            notificacaoService.markAllAsRead(user);
            return ResponseEntity.ok(Map.of("status", "Todas as notificações marcadas como lidas"));
        } catch (Exception e) {
            log.error("Erro ao marcar todas as notificações como lidas", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta todas as notificações do usuário
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar todas",
        description = "Remove todas as notificações do usuário"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificações deletadas com sucesso")
    })
    public ResponseEntity<Void> deleteAllNotificacoes() {
        try {
            User user = getCurrentUser();
            log.info("Deletando todas as notificações do usuário: {}", user.getId());
            notificacaoService.deleteAllNotificacoes(user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar todas as notificações", e);
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
