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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthService
 * Valida operações de registro, login, refresh token e validação JWT
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshToken refreshToken;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Configurar usuário de teste
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setCpf("12345678900");
        // Removido: testUser.setPhoneNumber - campo não existe mais
        testUser.setActive(true);
        testUser.setEmailVerified(false);
        testUser.setTwoFactorEnabled(false);
        testUser.setPasswordHash("encoded_password");

        // Configurar request de registro
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setCpf("12345678900");
        // Removido: registerRequest.setPhoneNumber - campo não existe mais

        // Configurar request de login
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Configurar refresh token
        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken("refresh_token_123");
        refreshToken.setUser(testUser);
        // Removido: refreshToken.setExpiryDate - campo não existe mais

        // Configurar autenticação
        authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password123"
        );
    }

    /**
     * Teste: Registrar novo usuário com sucesso
     */
    @Test
    void testRegisterSuccess() {
        // Arrange
        when(userService.createUser(any(RegisterRequest.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("access_token_123");
        when(refreshTokenService.criarRefreshToken(any(User.class))).thenReturn(refreshToken);
        when(jwtTokenProvider.getJwtExpirationTime()).thenReturn(3600000L);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        JwtAuthenticationResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token_123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token_123");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(userService, times(1)).createUser(any(RegisterRequest.class));
        verify(jwtTokenProvider, times(1)).generateToken(any(User.class));
        verify(refreshTokenService, times(1)).criarRefreshToken(any(User.class));
    }

    /**
     * Teste: Validar erro ao registrar com email existente
     */
    @Test
    void testRegisterEmailExists() {
        // Arrange
        when(userService.createUser(any(RegisterRequest.class)))
                .thenThrow(new ValidationException("Email já cadastrado no sistema"));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }

    /**
     * Teste: Login com sucesso
     */
    @Test
    void testLoginSuccess() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("access_token_123");
        when(refreshTokenService.criarRefreshToken(any(User.class))).thenReturn(refreshToken);
        when(jwtTokenProvider.getJwtExpirationTime()).thenReturn(3600000L);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        JwtAuthenticationResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token_123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token_123");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Validar erro ao fazer login com senha incorreta
     */
    @Test
    void testLoginWrongPassword() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email ou senha inválidos");

        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }

    /**
     * Teste: Renovar access token com refresh token válido
     */
    @Test
    void testRefreshTokenSuccess() {
        // Arrange
        String refreshTokenString = "refresh_token_123";
        when(refreshTokenService.validarRefreshToken(refreshTokenString)).thenReturn(true);
        when(refreshTokenRepository.findByToken(refreshTokenString)).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("new_access_token_456");
        when(jwtTokenProvider.getJwtExpirationTime()).thenReturn(3600000L);

        // Act
        JwtAuthenticationResponse response = authService.refreshToken(refreshTokenString);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new_access_token_456");
        assertThat(response.getRefreshToken()).isEqualTo(refreshTokenString);
        verify(refreshTokenService, times(1)).validarRefreshToken(refreshTokenString);
        verify(jwtTokenProvider, times(1)).generateToken(any(User.class));
    }

    /**
     * Teste: Validar erro ao renovar com refresh token expirado
     */
    @Test
    void testRefreshTokenExpired() {
        // Arrange
        String refreshTokenString = "expired_token";
        when(refreshTokenService.validarRefreshToken(refreshTokenString)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken(refreshTokenString))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Refresh token expirado ou inválido");

        verify(refreshTokenRepository, never()).findByToken(anyString());
        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }

    /**
     * Teste: Validar token JWT com sucesso
     */
    @Test
    void testValidateTokenSuccess() {
        // Arrange
        String token = "valid_jwt_token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);

        // Act
        boolean isValid = authService.validateToken(token);

        // Assert
        assertThat(isValid).isTrue();
        verify(jwtTokenProvider, times(1)).validateToken(token);
    }

    /**
     * Teste: Validar erro com token JWT inválido
     */
    @Test
    void testValidateTokenInvalid() {
        // Arrange
        String token = "invalid_jwt_token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // Act
        boolean isValid = authService.validateToken(token);

        // Assert
        assertThat(isValid).isFalse();
        verify(jwtTokenProvider, times(1)).validateToken(token);
    }

}
