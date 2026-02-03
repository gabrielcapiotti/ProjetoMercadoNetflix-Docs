package com.netflix.mercado.controller;

import com.netflix.mercado.dto.relatorio.RelatorioGeralResponse;
import com.netflix.mercado.dto.relatorio.RelatorioMercadoResponse;
import com.netflix.mercado.dto.relatorio.RankingMercadoResponse;
import com.netflix.mercado.dto.relatorio.MercadoPoucasAvaliacoesResponse;
import com.netflix.mercado.dto.relatorio.RelatorioComentariosResponse;
import com.netflix.mercado.service.RelatorioService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Relatórios", description = "Gerar relatórios e análises de mercados")
public class RelatorioRestController {

    private static final Logger log = Logger.getLogger(RelatorioRestController.class.getName());
    private final RelatorioService relatorioService;

    /**
     * Gera relatório geral do sistema
     */
    @GetMapping("/geral")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Relatório geral do sistema",
        description = "Retorna estatísticas consolidadas de todos os mercados, avaliações e comentários"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Relatório geral gerado com sucesso",
            content = @Content(schema = @Schema(implementation = RelatorioGeralResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN ou SELLER)"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar relatório")
    })
    public ResponseEntity<RelatorioGeralResponse> relatorioGeral() {
        try {
            log.info("Gerando relatório geral do sistema");
            
            RelatorioGeralResponse relatorio = relatorioService.gerarRelatorioGeral();
            
            log.info("Relatório geral gerado: " + relatorio.getTotalMercados() + " mercados");
            return ResponseEntity.ok(relatorio);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar relatório geral: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gera relatório específico de um mercado
     */
    @GetMapping("/mercado/{mercadoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Relatório de um mercado",
        description = "Retorna análise detalhada de performance, avaliações e distribuição de estrelas"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Relatório do mercado gerado",
            content = @Content(schema = @Schema(implementation = RelatorioMercadoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<RelatorioMercadoResponse> relatorioMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long mercadoId) {
        try {
            log.info("Gerando relatório para mercado: " + mercadoId);
            
            RelatorioMercadoResponse relatorio = relatorioService.gerarRelatorioMercado(mercadoId);
            
            return ResponseEntity.ok(relatorio);
            
        } catch (IllegalArgumentException e) {
            log.warning("Mercado não encontrado: " + mercadoId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.severe("Erro ao gerar relatório do mercado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ranking de mercados por avaliação média
     */
    @GetMapping("/ranking")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Ranking de mercados",
        description = "Retorna top N mercados ordenados por avaliação média"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ranking gerado com sucesso",
            content = @Content(schema = @Schema(implementation = RankingMercadoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar ranking")
    })
    public ResponseEntity<List<RankingMercadoResponse>> ranking(
            @Parameter(description = "Limite de mercados no ranking (padrão: 20)")
            @RequestParam(defaultValue = "20") int limite) {
        try {
            log.info("Gerando ranking dos top " + limite + " mercados");
            
            List<RankingMercadoResponse> ranking = relatorioService.gerarRankingMercados(limite);
            
            log.info("Ranking gerado: " + ranking.size() + " mercados");
            return ResponseEntity.ok(ranking);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar ranking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Identifica mercados com poucas avaliações
     */
    @GetMapping("/poucas-avaliacoes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Mercados com poucas avaliações",
        description = "Identifica mercados que precisam de mais engajamento"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de mercados com poucas avaliações",
            content = @Content(schema = @Schema(implementation = MercadoPoucasAvaliacoesResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<MercadoPoucasAvaliacoesResponse>> mercadosPoucasAvaliacoes(
            @Parameter(description = "Mínimo de avaliações (padrão: 10)")
            @RequestParam(defaultValue = "10") int avaliacaoMinima) {
        try {
            log.info("Buscando mercados com menos de " + avaliacaoMinima + " avaliações");
            
            List<MercadoPoucasAvaliacoesResponse> mercados = 
                relatorioService.gerarRelatorioPoucasAvaliacoes(avaliacaoMinima);
            
            return ResponseEntity.ok(mercados);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar relatório de poucas avaliações: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Análise de qualidade de comentários
     */
    @GetMapping("/comentarios")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Análise de comentários",
        description = "Relatório de qualidade, moderação e engajamento de comentários"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Relatório de comentários gerado",
            content = @Content(schema = @Schema(implementation = RelatorioComentariosResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<RelatorioComentariosResponse> relatorioComentarios() {
        try {
            log.info("Gerando relatório de comentários");
            
            RelatorioComentariosResponse relatorio = relatorioService.gerarRelatorioComentarios();
            
            return ResponseEntity.ok(relatorio);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar relatório de comentários: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
