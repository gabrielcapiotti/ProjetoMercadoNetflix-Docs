package com.netflix.mercado.controller;

import com.netflix.mercado.dto.promocao.CreatePromocaoRequest;
import com.netflix.mercado.dto.promocao.UpdatePromocaoRequest;
import com.netflix.mercado.dto.promocao.PromocaoResponse;
import com.netflix.mercado.dto.promocao.ValidatePromocaoResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.PromocaoService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Promoções", description = "Gerenciamento de promoções")
public class PromocaoController {

    private final PromocaoService promocaoService;

    /**
     * Cria nova promoção para um mercado
     */
    @PostMapping("/mercados/{mercadoId}/promocoes")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar promoção",
        description = "Cria nova promoção para um mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Promoção criada com sucesso",
            content = @Content(schema = @Schema(implementation = PromocaoResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "409", description = "Código de promoção já existe")
    })
    public ResponseEntity<PromocaoResponse> createPromocao(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId,
            @Valid @RequestBody CreatePromocaoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Criando promoção para mercado: {} por usuário: {}", mercadoId, user.getId());
            PromocaoResponse response = promocaoService.createPromocao(mercadoId, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar promoção", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista promoções de um mercado
     */
    @GetMapping("/mercados/{mercadoId}/promocoes")
    @Operation(
        summary = "Listar promoções",
        description = "Retorna as promoções ativas de um mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Promoções retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<Page<PromocaoResponse>> listPromocoesByMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId,
            @Parameter(description = "Apenas ativas")
            @RequestParam(defaultValue = "true") Boolean onlyActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.debug("Listando promoções do mercado: {}", mercadoId);
            Pageable pageable = PageRequest.of(page, size);
            Page<PromocaoResponse> response = promocaoService
                    .listPromocoesByMercado(mercadoId, onlyActive, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar promoções", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém detalhes de uma promoção
     */
    @GetMapping("/promocoes/{id}")
    @Operation(
        summary = "Obter promoção",
        description = "Retorna detalhes de uma promoção específica"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Promoção retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PromocaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Promoção não encontrada")
    })
    public ResponseEntity<PromocaoResponse> getPromocaoById(
            @Parameter(description = "ID da promoção")
            @PathVariable Long id) {
        try {
            log.debug("Obtendo promoção: {}", id);
            PromocaoResponse response = promocaoService.getPromocaoById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter promoção", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Atualiza uma promoção
     */
    @PutMapping("/promocoes/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar promoção",
        description = "Atualiza uma promoção existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Promoção atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = PromocaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Promoção não encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<PromocaoResponse> updatePromocao(
            @Parameter(description = "ID da promoção")
            @PathVariable Long id,
            @Valid @RequestBody UpdatePromocaoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Atualizando promoção: {} por usuário: {}", id, user.getId());
            PromocaoResponse response = promocaoService.updatePromocao(id, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao atualizar promoção", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta uma promoção
     */
    @DeleteMapping("/promocoes/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar promoção",
        description = "Remove uma promoção"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Promoção deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Promoção não encontrada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> deletePromocao(
            @Parameter(description = "ID da promoção")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Deletando promoção: {} por usuário: {}", id, user.getId());
            promocaoService.deletePromocao(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar promoção", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Valida um código de promoção
     */
    @GetMapping("/promocoes/code/{code}/validate")
    @Operation(
        summary = "Validar código de promoção",
        description = "Verifica se um código de promoção é válido e ativo"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Validação retornada com sucesso",
            content = @Content(schema = @Schema(implementation = ValidatePromocaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Código de promoção não encontrado")
    })
    public ResponseEntity<ValidatePromocaoResponse> validatePromoCode(
            @Parameter(description = "Código de promoção")
            @PathVariable String code) {
        try {
            log.debug("Validando código de promoção: {}", code);
            ValidatePromocaoResponse response = promocaoService.validatePromoCode(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao validar código de promoção", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Aplica uma promoção (usa código de desconto)
     */
    @PostMapping("/promocoes/{id}/apply")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Aplicar promoção",
        description = "Aplica uma promoção para o usuário autenticado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Promoção aplicada com sucesso",
            content = @Content(schema = @Schema(implementation = PromocaoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Promoção não encontrada"),
        @ApiResponse(responseCode = "410", description = "Promoção expirada ou inativa"),
        @ApiResponse(responseCode = "429", description = "Limite de uso atingido")
    })
    public ResponseEntity<PromocaoResponse> applyPromocao(
            @Parameter(description = "ID da promoção")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Aplicando promoção {} para usuário: {}", id, user.getId());
            PromocaoResponse response = promocaoService.applyPromocao(id, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao aplicar promoção", e);
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
