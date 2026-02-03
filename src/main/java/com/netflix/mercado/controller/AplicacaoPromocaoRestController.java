package com.netflix.mercado.controller;

import com.netflix.mercado.dto.promocao.AplicarPromocaoRequest;
import com.netflix.mercado.dto.promocao.AplicarPromocaoResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.Promocao;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.AplicacaoPromocaoService;
import com.netflix.mercado.repository.PromocaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/promocoes/aplicacao")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Aplicação de Promoções", description = "Endpoints para aplicar e validar promoções")
public class AplicacaoPromocaoRestController {

    private static final Logger log = Logger.getLogger(AplicacaoPromocaoRestController.class.getName());
    private final AplicacaoPromocaoService aplicacaoPromocaoService;
    private final PromocaoRepository promocaoRepository;

    /**
     * Aplica uma promoção a um valor de compra
     */
    @PostMapping("/aplicar")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Aplicar promoção",
        description = "Aplica uma promoção a um valor de compra e calcula o desconto"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Promoção aplicada com sucesso",
            content = @Content(schema = @Schema(implementation = AplicarPromocaoResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou promoção expirada"),
        @ApiResponse(responseCode = "404", description = "Promoção não encontrada"),
        @ApiResponse(responseCode = "409", description = "Limite de utilização atingido"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<AplicarPromocaoResponse> aplicarPromocao(
            @Valid @RequestBody AplicarPromocaoRequest request) {
        try {
            User usuario = getCurrentUser();
            log.info("Aplicando promoção código: " + request.getCodigoPromocao() + 
                     " para usuário: " + usuario.getId());
            
            AplicarPromocaoResponse response = aplicacaoPromocaoService.aplicarPromocao(request, usuario);
            
            log.info("Promoção aplicada com sucesso. Desconto: " + response.getDesconto());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warning("Erro de validação ao aplicar promoção: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.severe("Erro ao aplicar promoção: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Valida se uma promoção pode ser aplicada
     */
    @PostMapping("/validar/{codigoPromocao}")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Validar promoção",
        description = "Verifica se uma promoção é válida e pode ser aplicada"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Validação completa (válido ou inválido)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(responseCode = "404", description = "Promoção não encontrada"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<?> validarPromocao(
            @PathVariable String codigoPromocao) {
        try {
            log.info("Validando promoção: " + codigoPromocao);
            
            var promocao = promocaoRepository.findByCodigo(codigoPromocao)
                .orElse(null);
            
            if (promocao == null) {
                return ResponseEntity.ok(new ValidacaoPromocaoResponse(false, 
                    "Promoção não encontrada"));
            }
            
            // Validações básicas
            if (!promocao.getAtiva() || 
                (promocao.getDataValidade() != null && 
                 promocao.getDataValidade().isBefore(LocalDateTime.now()))) {
                return ResponseEntity.ok(new ValidacaoPromocaoResponse(false, 
                    "Promoção inválida, expirada ou com limite atingido"));
            }
            
            return ResponseEntity.ok(new ValidacaoPromocaoResponse(true, "Promoção válida"));
            
        } catch (Exception e) {
            log.severe("Erro ao validar promoção: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    /**
     * DTO interno para resposta de validação
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    static class ValidacaoPromocaoResponse {
        private boolean valida;
        private String mensagem;
    }
}
