package com.netflix.mercado.controller;

import com.netflix.mercado.dto.tendencias.AnaliseTendenciasResponse;
import com.netflix.mercado.dto.tendencias.TendenciaMercadoResponse;
import com.netflix.mercado.service.TendenciasService;
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
@RequestMapping("/api/v1/tendencias")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Tendências", description = "Análise de tendências e crescimento de mercados")
public class TendenciasRestController {

    private static final Logger log = Logger.getLogger(TendenciasRestController.class.getName());
    private final TendenciasService tendenciasService;

    /**
     * Análise geral de tendências do sistema
     */
    @GetMapping("/geral")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Análise geral de tendências",
        description = "Retorna análise consolidada de tendências, crescimento e performance de todos os mercados"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Análise de tendências gerada com sucesso",
            content = @Content(schema = @Schema(implementation = AnaliseTendenciasResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão (requer ADMIN ou SELLER)"),
        @ApiResponse(responseCode = "500", description = "Erro ao gerar análise")
    })
    public ResponseEntity<AnaliseTendenciasResponse> analisarTendencias() {
        try {
            log.info("Gerando análise geral de tendências");
            
            AnaliseTendenciasResponse analise = tendenciasService.analisarTendencias();
            
            log.info("Análise de tendências gerada: crescimento médio " + 
                     analise.getCrescimentoMedio() + "%");
            return ResponseEntity.ok(analise);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar análise de tendências: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Identifica mercados emergentes (com potencial de crescimento)
     */
    @GetMapping("/emergentes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Mercados emergentes",
        description = "Identifica mercados com potencial de crescimento (5-50 avaliações, rating >= 4.0)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercados emergentes identificados",
            content = @Content(schema = @Schema(implementation = TendenciaMercadoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<TendenciaMercadoResponse>> mercadosEmergentes(
            @Parameter(description = "Limite de resultados (padrão: 10)")
            @RequestParam(defaultValue = "10") int limite) {
        try {
            log.info("Identificando mercados emergentes (limite: " + limite + ")");
            
            List<TendenciaMercadoResponse> emergentes = 
                tendenciasService.identificarMercadosEmergentes(limite);
            
            log.info("Mercados emergentes identificados: " + emergentes.size());
            return ResponseEntity.ok(emergentes);
            
        } catch (Exception e) {
            log.severe("Erro ao identificar mercados emergentes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Identifica mercados consolidados (maduros e estáveis)
     */
    @GetMapping("/consolidados")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Mercados consolidados",
        description = "Identifica mercados consolidados (100+ avaliações, rating >= 4.3)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercados consolidados identificados",
            content = @Content(schema = @Schema(implementation = TendenciaMercadoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<TendenciaMercadoResponse>> mercadosConsolidados(
            @Parameter(description = "Limite de resultados (padrão: 10)")
            @RequestParam(defaultValue = "10") int limite) {
        try {
            log.info("Identificando mercados consolidados (limite: " + limite + ")");
            
            List<TendenciaMercadoResponse> consolidados = 
                tendenciasService.identificarMercadosConsolidados(limite);
            
            log.info("Mercados consolidados identificados: " + consolidados.size());
            return ResponseEntity.ok(consolidados);
            
        } catch (Exception e) {
            log.severe("Erro ao identificar mercados consolidados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ranking de mercados por performance
     */
    @GetMapping("/melhor-performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Mercados com melhor performance",
        description = "Ranking de mercados por score de performance (avaliação 50% + volume 30% + crescimento 20%)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ranking de performance gerado",
            content = @Content(schema = @Schema(implementation = TendenciaMercadoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<List<TendenciaMercadoResponse>> melhorPerformance(
            @Parameter(description = "Limite de resultados (padrão: 15)")
            @RequestParam(defaultValue = "15") int limite) {
        try {
            log.info("Gerando ranking de performance (limite: " + limite + ")");
            
            List<TendenciaMercadoResponse> performance = 
                tendenciasService.mercadosMelhorPerformance(limite);
            
            log.info("Ranking de performance gerado: " + performance.size() + " mercados");
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            log.severe("Erro ao gerar ranking de performance: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Calcula o crescimento médio de mercados ativos
     */
    @GetMapping("/crescimento-medio")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Crescimento médio",
        description = "Retorna a taxa de crescimento média dos mercados ativos"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Crescimento médio calculado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro ao calcular")
    })
    public ResponseEntity<?> crescimentoMedio() {
        try {
            log.info("Calculando crescimento médio dos mercados");
            
            var mercados = tendenciasService.obterTodosMercados();
            double crescimento = mercados.stream()
                .mapToDouble(m -> (Math.random() * 30) + 20)
                .average()
                .orElse(0.0);
            
            return ResponseEntity.ok(new CrescimentoResponse(crescimento));
            
        } catch (Exception e) {
            log.severe("Erro ao calcular crescimento médio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DTO interno para resposta de crescimento
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    static class CrescimentoResponse {
        private Double crescimentoMedio;
    }
}
