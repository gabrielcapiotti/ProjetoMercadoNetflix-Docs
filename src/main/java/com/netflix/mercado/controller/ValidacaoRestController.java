package com.netflix.mercado.controller;

import com.netflix.mercado.service.DataIntegrityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/validacao")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Validação de Dados", description = "Endpoints para validar integridade de dados")
public class ValidacaoRestController {

    private static final Logger log = Logger.getLogger(ValidacaoRestController.class.getName());
    private final DataIntegrityService dataIntegrityService;

    /**
     * Valida email com RFC 5322
     */
    @PostMapping("/email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Validar email",
        description = "Valida se um endereço de email está em formato correto (RFC 5322)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Validação completa",
            content = @Content(schema = @Schema(implementation = ValidacaoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ValidacaoResponse> validarEmail(
            @Parameter(description = "Email a validar")
            @RequestParam String email) {
        try {
            log.info("Validando email: " + email);
            
            try {
                dataIntegrityService.validarEmail(email);
                return ResponseEntity.ok(new ValidacaoResponse(true, "Email válido"));
            } catch (Exception validationError) {
                return ResponseEntity.ok(new ValidacaoResponse(false, 
                    "Email inválido (formato incorreto ou muito longo)"));
            }
            
        } catch (Exception e) {
            log.severe("Erro ao validar email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ValidacaoResponse(false, "Erro ao validar: " + e.getMessage()));
        }
    }

    /**
     * Valida URL bem-formada
     */
    @PostMapping("/url")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Validar URL",
        description = "Valida se uma URL está em formato correto"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Validação completa",
            content = @Content(schema = @Schema(implementation = ValidacaoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<ValidacaoResponse> validarUrl(
            @Parameter(description = "URL a validar")
            @RequestParam String url) {
        try {
            log.info("Validando URL: " + url);
            
            try {
                dataIntegrityService.validarURL(url);
                return ResponseEntity.ok(new ValidacaoResponse(true, "URL válida"));
            } catch (Exception validationError) {
                return ResponseEntity.ok(new ValidacaoResponse(false, 
                    "URL inválida (formato incorreto ou muito longa)"));
            }
            
        } catch (Exception e) {
            log.severe("Erro ao validar URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ValidacaoResponse(false, "Erro ao validar: " + e.getMessage()));
        }
    }

    /**
     * Sanitiza uma string removendo caracteres perigosos
     */
    @PostMapping("/sanitizar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER') or hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Sanitizar texto",
        description = "Remove caracteres perigosos (<, >, \", ', /) para prevenir XSS"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Texto sanitizado retornado",
            content = @Content(schema = @Schema(implementation = SanitizacaoResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<SanitizacaoResponse> sanitizar(
            @Parameter(description = "Texto a sanitizar")
            @RequestParam String texto) {
        try {
            log.info("Sanitizando texto");
            
            String sanitizado = dataIntegrityService.sanitizarString(texto);
            
            return ResponseEntity.ok(new SanitizacaoResponse(texto, sanitizado, 
                texto.equals(sanitizado)));
            
        } catch (Exception e) {
            log.severe("Erro ao sanitizar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DTO para resposta de validação simples
     */
    @Data
    @AllArgsConstructor
    static class ValidacaoResponse {
        private boolean valido;
        private String mensagem;
    }

    /**
     * DTO para resposta de sanitização
     */
    @Data
    @AllArgsConstructor
    static class SanitizacaoResponse {
        private String original;
        private String sanitizado;
        private boolean foiAlterado;
    }
}
