package com.netflix.mercado.service;

import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.RefreshToken;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.UserRepository;
import com.netflix.mercado.repository.RefreshTokenRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.auth.RegisterRequest;
import com.netflix.mercado.dto.auth.LoginRequest;
import com.netflix.mercado.dto.auth.JwtAuthenticationResponse;
import com.netflix.mercado.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsável por autenticação, autorização e gerenciamento de tokens JWT.
 * Implementa lógica de login, registro, refresh token e logout.
 */
@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Registra um novo usuário no sistema.
     *
     * @param request dados de registro
     * @return resposta com tokens JWT
     */
    public JwtAuthenticationResponse register(RegisterRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());

        User user = userService.createUser(request);

        // Gerar tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.criarRefreshToken(user);

        auditLogRepository.save(new AuditLog(
                null,
                user,
                "LOGIN",
                "USER",
                user.getId(),
                "Usuário registrado e autenticado",
                LocalDateTime.now()
        ));

        log.info("Usuário registrado com sucesso: {}", user.getEmail());

        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpirationTime())
                .build();
    }

    /**
     * Autentica um usuário e gera tokens JWT.
     *
     * @param request dados de login
     * @return resposta com tokens JWT
     * @throws ValidationException se credenciais são inválidas
     */
    public JwtAuthenticationResponse login(LoginRequest request) {
        log.info("Tentativa de login para email: {}", request.getEmail());

        try {
            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

            // Verificar se usuário está ativo
            if (!user.isActive()) {
                log.warn("Tentativa de login com usuário inativo: {}", request.getEmail());
                throw new ValidationException("Usuário está inativo");
            }

            // Gerar tokens
            String accessToken = jwtTokenProvider.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.criarRefreshToken(user);

            // Registrar login no audit log
            auditLogRepository.save(new AuditLog(
                    null,
                    user,
                    "LOGIN",
                    "USER",
                    user.getId(),
                    "Login realizado com sucesso",
                    LocalDateTime.now()
            ));

            log.info("Login bem-sucedido para: {}", request.getEmail());

            return JwtAuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getJwtExpirationTime())
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Falha na autenticação para email: {}", request.getEmail());
            throw new ValidationException("Email ou senha inválidos");
        }
    }

    /**
     * Renova o access token usando um refresh token.
     *
     * @param refreshTokenString o refresh token
     * @return resposta com novo access token
     * @throws ValidationException se refresh token é inválido
     */
    public JwtAuthenticationResponse refreshToken(String refreshTokenString) {
        log.debug("Renovando access token");

        RefreshToken refreshToken = refreshTokenService.obterRefreshToken(refreshTokenString);

        if (!refreshTokenService.validarRefreshToken(refreshTokenString)) {
            log.warn("Tentativa de refresh com token inválido ou expirado");
            throw new ValidationException("Refresh token inválido ou expirado");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user);

        log.debug("Access token renovado com sucesso");

        return JwtAuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpirationTime())
                .build();
    }

    /**
     * Realiza logout de um usuário revogando seus refresh tokens.
     *
     * @param userId ID do usuário
     */
    public void logout(Long userId) {
        log.info("Logout do usuário ID: {}", userId);

        User user = userService.findUserById(userId);
        refreshTokenService.revogarTodosOsTokensDoUsuario(user);

        auditLogRepository.save(new AuditLog(
                null,
                user,
                "LOGOUT",
                "USER",
                user.getId(),
                "Logout realizado",
                LocalDateTime.now()
        ));

        log.info("Logout realizado para usuário ID: {}", userId);
    }

    /**
     * Valida um token JWT.
     *
     * @param token o token a validar
     * @return true se token é válido, false caso contrário
     */
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        log.debug("Validando token JWT");
        try {
            return jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtém o usuário a partir de um token JWT.
     *
     * @param token o token JWT
     * @return o usuário encontrado
     * @throws ValidationException se token é inválido
     */
    @Transactional(readOnly = true)
    public User getUserFromToken(String token) {
        log.debug("Extraindo usuário do token");

        if (!validateToken(token)) {
            log.warn("Tentativa de extrair usuário de token inválido");
            throw new ValidationException("Token inválido ou expirado");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
    public AuthService() {
    }

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, AuditLogRepository auditLogRepository, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, UserService userService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogRepository = auditLogRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserRepository getUserRepository() {
        return this.userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RefreshTokenRepository getRefreshTokenRepository() {
        return this.refreshTokenRepository;
    }

    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuditLogRepository getAuditLogRepository() {
        return this.auditLogRepository;
    }

    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public JwtTokenProvider getJwtTokenProvider() {
        return this.jwtTokenProvider;
    }

    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public UserService getUserService() {
        return this.userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public RefreshTokenService getRefreshTokenService() {
        return this.refreshTokenService;
    }

    public void setRefreshTokenService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

}
