package com.netflix.mercado.controller;

import com.netflix.mercado.dto.recomendacao.MercadoRecomendacaoResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/recomendacoes")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Recomendações", description = "Sistema de recomendação de mercados personalizadas")
public class RecomendacaoRestController {

    private static final Logger log = Logger.getLogger(RecomendacaoRestController.class.getName());
    private final RecomendacaoService recomendacaoService;

    /**
     * Gera recomendações personalizadas baseadas nos favoritos do usuário
     */
    @GetMapping("/personalizadas")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Gerar recomendações personalizadas",
        description = "Retorna lista de mercados recomendados baseados no histórico e favoritos do usuário"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Recomendações geradas com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoRecomendacaoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar recomendações")
    })
    public ResponseEntity<List<MercadoRecomendacaoResponse>> gerarRecomendacoes(
            @Parameter(description = "Quantidade máxima de recomendações (padrão: 10)")
            @RequestParam(defaultValue = "10") int limite) {
        try {
            User usuario = getCurrentUser();
            log.info("Gerando " + limite + " recomendações para usuário: " + usuario.getId());
            
            List<MercadoRecomendacaoResponse> recomendacoes = 
                recomendacaoService.gerarRecomendacoes(usuario, limite);
            
            log.info("Recomendações geradas: " + recomendacoes.size() + " mercados");
            return ResponseEntity.ok(recomendacoes);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar recomendações: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gera recomendações baseadas em localização (estado)
     */
    @GetMapping("/por-localizacao")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Recomendações por localização",
        description = "Retorna mercados recomendados baseados em preferências de localização"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Recomendações por localização retornadas",
            content = @Content(schema = @Schema(implementation = MercadoRecomendacaoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar recomendações")
    })
    public ResponseEntity<List<MercadoRecomendacaoResponse>> recomendacoesPorLocalizacao(
            @Parameter(description = "Limite de resultados (padrão: 10)")
            @RequestParam(defaultValue = "10") int limite) {
        try {
            User usuario = getCurrentUser();
            log.info("Gerando recomendações por localização para usuário: " + usuario.getId());
            
            List<MercadoRecomendacaoResponse> recomendacoes = 
                recomendacaoService.recomendacoesPorLocalizacao(usuario, limite);
            
            return ResponseEntity.ok(recomendacoes);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar recomendações por localização: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recomenda mercados não visitados com boa avaliação
     */
    @GetMapping("/nao-visitados")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Recomendações de novos mercados",
        description = "Mercados com alta avaliação que o usuário ainda não visitou"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercados não visitados recomendados",
            content = @Content(schema = @Schema(implementation = MercadoRecomendacaoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar recomendações")
    })
    public ResponseEntity<List<MercadoRecomendacaoResponse>> recomendacoesNaoVisitados(
            @Parameter(description = "Limite de resultados (padrão: 10)")
            @RequestParam(defaultValue = "10") int limite) {
        try {
            User usuario = getCurrentUser();
            log.info("Buscando recomendações de mercados não visitados para usuário: " + usuario.getId());
            
            List<MercadoRecomendacaoResponse> recomendacoes = 
                recomendacaoService.recomendacoesNaoVisitados(usuario, limite);
            
            return ResponseEntity.ok(recomendacoes);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar recomendações de não visitados: " + e.getMessage());
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
}
