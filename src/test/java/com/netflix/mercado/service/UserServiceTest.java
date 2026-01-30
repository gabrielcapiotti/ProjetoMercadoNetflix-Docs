package com.netflix.mercado.service;

import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.Role;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.UserRepository;
import com.netflix.mercado.repository.RoleRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.RegisterRequest;
import com.netflix.mercado.dto.UserUpdateRequest;
import com.netflix.mercado.dto.ChangePasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserService
 * Valida operações de criação, busca, atualização e gerenciamento de usuários
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Configurar usuário de teste
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setCpf("12345678900");
        testUser.setPhoneNumber("11999999999");
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
        registerRequest.setPhoneNumber("11999999999");

        // Configurar role padrão
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
    }

    /**
     * Teste: Criar usuário com sucesso
     */
    @Test
    void testCreateUserSuccess() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        User createdUser = userService.createUser(registerRequest);

        // Assert
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        assertThat(createdUser.getFullName()).isEqualTo("Test User");
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Validar erro ao criar usuário com email duplicado
     */
    @Test
    void testCreateUserEmailDuplicate() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(registerRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Teste: Buscar usuário por ID com sucesso
     */
    @Test
    void testFindUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findUserById(1L);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Teste: Validar erro ao buscar usuário por ID que não existe
     */
    @Test
    void testFindUserByIdNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(userRepository, times(1)).findById(999L);
    }

    /**
     * Teste: Buscar usuário por email com sucesso
     */
    @Test
    void testFindUserByEmail() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findUserByEmail("test@example.com");

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    /**
     * Teste: Alterar senha com sucesso
     */
    @Test
    void testChangePasswordSuccess() {
        // Arrange
        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setCurrentPassword("password123");
        changeRequest.setNewPassword("newPassword456");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        userService.changePassword(1L, changeRequest);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
        assertThat(testUser.getPasswordHash()).isEqualTo("new_encoded_password");
    }

    /**
     * Teste: Validar erro ao alterar senha com senha atual incorreta
     */
    @Test
    void testChangePasswordWrongOldPassword() {
        // Arrange
        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setCurrentPassword("wrongPassword");
        changeRequest.setNewPassword("newPassword456");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encoded_password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.changePassword(1L, changeRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Senha atual está incorreta");

        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Teste: Habilitar autenticação de dois fatores
     */
    @Test
    void testEnableTwoFactor() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        userService.enableTwoFactor(1L);

        // Assert
        assertThat(testUser.isTwoFactorEnabled()).isTrue();
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Verificar email do usuário
     */
    @Test
    void testVerifyEmail() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        userService.verifyEmail(1L);

        // Assert
        assertThat(testUser.isEmailVerified()).isTrue();
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Soft delete de usuário
     */
    @Test
    void testSoftDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        userService.softDeleteUser(1L);

        // Assert
        assertThat(testUser.isActive()).isFalse();
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

}
