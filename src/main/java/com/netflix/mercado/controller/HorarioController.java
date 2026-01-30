package com.netflix.mercados.controller;

import com.netflix.mercados.dto.horario.request.CreateHorarioRequest;
import com.netflix.mercados.dto.horario.request.UpdateHorarioRequest;
import com.netflix.mercados.dto.horario.response.HorarioFuncionamentoResponse;
import com.netflix.mercados.dto.horario.response.StatusLojaResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.security.UserPrincipal;
import com.netflix.mercados.service.HorarioService;
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
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@Transactional
@Tag(name = "Horários", description = "Gerenciamento de horários de funcionamento")
public class HorarioController {

    private final HorarioService horarioService;

    /**
     * Cria novo horário de funcionamento
     */
    @PostMapping("/mercados/{mercadoId}/horarios")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar horário",
        description = "Adiciona novo horário de funcionamento ao mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Horário criado com sucesso",
            content = @Content(schema = @Schema(implementation = HorarioFuncionamentoResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<HorarioFuncionamentoResponse> createHorario(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId,
            @Valid @RequestBody CreateHorarioRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Criando horário para mercado: {} por usuário: {}", mercadoId, user.getId());
            HorarioFuncionamentoResponse response = horarioService.createHorario(
                    mercadoId, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar horário", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista horários de funcionamento de um mercado
     */
    @GetMapping("/mercados/{mercadoId}/horarios")
    @Operation(
        summary = "Listar horários",
        description = "Retorna todos os horários de funcionamento de um mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Horários retornados com sucesso",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<List<HorarioFuncionamentoResponse>> listHorarios(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            log.debug("Listando horários do mercado: {}", mercadoId);
            List<HorarioFuncionamentoResponse> response = horarioService.listHorarios(mercadoId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar horários", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Atualiza um horário de funcionamento
     */
    @PutMapping("/horarios/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar horário",
        description = "Atualiza um horário de funcionamento existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Horário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = HorarioFuncionamentoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Horário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<HorarioFuncionamentoResponse> updateHorario(
            @Parameter(description = "ID do horário")
            @PathVariable Long id,
            @Valid @RequestBody UpdateHorarioRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Atualizando horário: {} por usuário: {}", id, user.getId());
            HorarioFuncionamentoResponse response = horarioService.updateHorario(id, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao atualizar horário", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta um horário de funcionamento
     */
    @DeleteMapping("/horarios/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar horário",
        description = "Remove um horário de funcionamento"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Horário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Horário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> deleteHorario(
            @Parameter(description = "ID do horário")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Deletando horário: {} por usuário: {}", id, user.getId());
            horarioService.deleteHorario(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar horário", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém status de abertura/fechamento de um mercado
     */
    @GetMapping("/mercados/{mercadoId}/status")
    @Operation(
        summary = "Obter status da loja",
        description = "Retorna se a loja está aberta ou fechada no momento"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Status retornado com sucesso",
            content = @Content(schema = @Schema(implementation = StatusLojaResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<StatusLojaResponse> getLojaStatus(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            log.debug("Obtendo status da loja: {}", mercadoId);
            StatusLojaResponse response = horarioService.getLojaStatus(mercadoId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter status da loja", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Verifica se um mercado está aberto no momento
     */
    @GetMapping("/mercados/{mercadoId}/aberto")
    @Operation(
        summary = "Verificar se está aberto",
        description = "Retorna true se o mercado está aberto no momento"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Status retornado com sucesso",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<Map<String, Boolean>> isOpen(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            log.debug("Verificando se mercado está aberto: {}", mercadoId);
            boolean isOpen = horarioService.isOpen(mercadoId);
            return ResponseEntity.ok(Map.of("isOpen", isOpen));
        } catch (Exception e) {
            log.error("Erro ao verificar status de abertura", e);
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
