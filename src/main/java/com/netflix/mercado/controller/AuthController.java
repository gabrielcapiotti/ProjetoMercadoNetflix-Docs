package com.netflix.mercado.controller;

import com.netflix.mercado.dto.auth.LoginRequest;
import com.netflix.mercado.dto.auth.RegisterRequest;
import com.netflix.mercado.dto.auth.RefreshTokenRequest;
import com.netflix.mercado.dto.auth.JwtAuthenticationResponse;
import com.netflix.mercado.dto.auth.UserResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.AuthService;
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
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Autenticação", description = "Endpoints de autenticação e autorização")
public class AuthController {

    private static final Logger log = Logger.getLogger(AuthController.class.getName());

    private final AuthService authService;

    /**
     * Registra um novo usuário no sistema
     * 
     * @param request dados de registro (nome, email, senha)
     * @return JWT tokens e dados do usuário criado
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário com email e senha"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Email já registrado")
    })
    public ResponseEntity<JwtAuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Iniciando registro de novo usuário: " + request.getEmail());
            JwtAuthenticationResponse response = authService.register(request);
            log.info("Usuário registrado com sucesso: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.severe("Erro ao registrar usuário: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Autentica um usuário existente
     * 
     * @param request credenciais (email, senha)
     * @return JWT tokens se autenticação bem-sucedida
     */
    @PostMapping("/login")
    @Operation(
        summary = "Fazer login",
        description = "Autentica um usuário existente e retorna JWT tokens"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login bem-sucedido",
            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<JwtAuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login attempt para: " + request.getEmail());
            JwtAuthenticationResponse response = authService.login(request);
            log.info("Login bem-sucedido: " + request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao fazer login: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Renova o JWT token usando refresh token
     * 
     * @param request contém o refresh token
     * @return novo JWT token
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar token JWT",
        description = "Gera um novo JWT token a partir do refresh token"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token renovado com sucesso",
            content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        try {
            log.info("Renovando JWT token");
            JwtAuthenticationResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao renovar token: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Faz logout do usuário autenticado
     * Remove o refresh token do banco de dados
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Fazer logout",
        description = "Invalida o refresh token do usuário atual"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> logout() {
        try {
            User user = getCurrentUser();
            log.info("Logout do usuário: " + user.getUsername());
            authService.logout(user.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.severe("Erro ao fazer logout: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém os dados do usuário autenticado
     * 
     * @return dados do usuário atual
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Obter dados do usuário autenticado",
        description = "Retorna as informações do usuário atualmente autenticado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dados do usuário retornados com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<UserResponse> getCurrentUserInfo() {
        try {
            User user = getCurrentUser();
            log.fine("Obtendo informações do usuário: " + user.getUsername());
            UserResponse response = UserResponse.from(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao obter informações do usuário: " + e.getMessage());
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
