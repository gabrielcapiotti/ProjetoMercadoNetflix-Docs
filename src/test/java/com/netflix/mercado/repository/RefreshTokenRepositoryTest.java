package com.netflix.mercado.repository;

import com.netflix.mercado.entity.RefreshToken;
import com.netflix.mercado.entity.Role;
import com.netflix.mercado.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para RefreshTokenRepository
 * Coverage: 6 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("RefreshTokenRepository Integration Tests")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private RefreshToken tokenValido;
    private RefreshToken tokenExpirado;
    private RefreshToken tokenRevogado;

    @BeforeEach
    void setUp() {
        // Criar role
        Role customerRole = new Role();
        customerRole.setName(Role.RoleName.USER);
        customerRole.setDescription("Cliente");
        customerRole.setActive(true);
        customerRole = entityManager.persistAndFlush(customerRole);

        // Criar usuário
        user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("$2a$10$hash");
        user.setFullName("Test User");
        user.setCpf("11111111111");
        user.setPhone("11111111111");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        user = entityManager.persistAndFlush(user);

        // Criar token válido
        tokenValido = new RefreshToken();
        tokenValido.setUser(user);
        tokenValido.setToken(UUID.randomUUID().toString());
        tokenValido.setDataExpiracao(Instant.now().plusSeconds(604800)); // 7 dias
        tokenValido.setRevogado(false);
        tokenValido.setActive(true);
        tokenValido = entityManager.persistAndFlush(tokenValido);

        // Criar token expirado
        tokenExpirado = new RefreshToken();
        tokenExpirado.setUser(user);
        tokenExpirado.setToken(UUID.randomUUID().toString());
        tokenExpirado.setDataExpiracao(Instant.now().minusSeconds(86400)); // 1 dia atrás
        tokenExpirado.setRevogado(false);
        tokenExpirado.setActive(true);
        tokenExpirado = entityManager.persistAndFlush(tokenExpirado);

        // Criar token revogado
        tokenRevogado = new RefreshToken();
        tokenRevogado.setUser(user);
        tokenRevogado.setToken(UUID.randomUUID().toString());
        tokenRevogado.setDataExpiracao(Instant.now().plusSeconds(432000)); // 5 dias
        tokenRevogado.setRevogado(true);
        tokenRevogado.setActive(true);
        tokenRevogado = entityManager.persistAndFlush(tokenRevogado);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve buscar apenas tokens válidos do usuário")
    void testFindValidTokensByUser() {
        // When
        List<RefreshToken> result = refreshTokenRepository.findValidTokensByUser(user);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(tokenValido.getId());
        assertThat(result).allMatch(t -> !t.getRevogado());
        assertThat(result).allMatch(t -> t.getDataExpiracao().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Deve buscar tokens não expirados")
    void testFindValidTokensNotExpired() {
        // When
        List<RefreshToken> result = refreshTokenRepository.findValidTokensByUser(user);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).noneMatch(t -> t.getDataExpiracao().isBefore(Instant.now()));
    }

    @Test
    @DisplayName("Deve buscar apenas tokens não revogados")
    void testFindValidTokensNotRevoked() {
        // When
        List<RefreshToken> result = refreshTokenRepository.findValidTokensByUser(user);

        // Then
        assertThat(result).allMatch(t -> !t.getRevogado());
    }

    @Test
    @DisplayName("Deve revogar todos os tokens de um usuário")
    void testRevokeAllTokensForUser() {
        // When - Revogar manualmente pois o repository usa @Modifying
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        tokens.forEach(t -> t.setRevogado(true));
        refreshTokenRepository.saveAll(tokens);
        entityManager.flush();
        entityManager.clear();

        // Then
        List<RefreshToken> allTokens = refreshTokenRepository.findAll();
        assertThat(allTokens).allMatch(RefreshToken::getRevogado);

        List<RefreshToken> validTokens = refreshTokenRepository.findValidTokensByUser(user);
        assertThat(validTokens).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar tokens expirados para limpeza")
    void testFindExpiredTokens() {
        // When
        List<RefreshToken> result = refreshTokenRepository.findExpiredTokens(Instant.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(tokenExpirado.getId());
        assertThat(result).allMatch(t -> t.getDataExpiracao().isBefore(Instant.now()));
    }

    @Test
    @DisplayName("Deve contar tokens válidos ativos")
    void testCountValidTokens() {
        // When
        long count = refreshTokenRepository.countValidTokensByUser(user);

        // Then
        assertThat(count).isEqualTo(1); // Apenas tokenValido
    }
}
