package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Role;
import com.netflix.mercado.entity.TwoFactorCode;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para TwoFactorCodeRepository
 * Coverage: 5 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("TwoFactorCodeRepository Integration Tests")
class TwoFactorCodeRepositoryTest {

    @Autowired
    private TwoFactorCodeRepository twoFactorCodeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private TwoFactorCode codigoValido;
    private TwoFactorCode codigoExpirado;

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
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setTwoFactorEnabled(true);
        user.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        user = entityManager.persistAndFlush(user);

        // Criar código válido
        codigoValido = new TwoFactorCode();
        codigoValido.setUser(user);
        codigoValido.setCodigo("123456");
        codigoValido.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        codigoValido.setUtilizado(false);
        codigoValido.setTentativas(0);
        codigoValido.setActive(true);
        codigoValido = entityManager.persistAndFlush(codigoValido);

        // Criar código expirado
        codigoExpirado = new TwoFactorCode();
        codigoExpirado.setUser(user);
        codigoExpirado.setCodigo("654321");
        codigoExpirado.setExpiryDate(LocalDateTime.now().minusMinutes(10));
        codigoExpirado.setUtilizado(false);
        codigoExpirado.setTentativas(0);
        codigoExpirado.setActive(true);
        codigoExpirado = entityManager.persistAndFlush(codigoExpirado);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve encontrar código 2FA válido")
    void testFindByCodigo() {
        // When
        Optional<TwoFactorCode> result = twoFactorCodeRepository.findByCodigoAndActiveTrue("123456");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUtilizado()).isFalse();
    }

    @Test
    @DisplayName("Deve buscar códigos válidos do usuário")
    void testFindValidCodigosByUser() {
        // When
        List<TwoFactorCode> result = twoFactorCodeRepository.findValidCodesByUser(
                user.getId(), LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(codigoValido.getId());
        assertThat(result).allMatch(c -> !c.getUtilizado());
        assertThat(result).allMatch(c -> c.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Deve buscar código mais recente válido do usuário")
    void testFindLatestValidCodigoForUser() {
        // Given - Criar outro código válido mais recente
        TwoFactorCode codigoNovo = new TwoFactorCode();
        codigoNovo.setUser(user);
        codigoNovo.setCodigo("999888");
        codigoNovo.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        codigoNovo.setUtilizado(false);
        codigoNovo.setTentativas(0);
        codigoNovo.setActive(true);
        entityManager.persistAndFlush(codigoNovo);
        entityManager.clear();

        // When
        Optional<TwoFactorCode> result = twoFactorCodeRepository.findLatestValidCode(
                user.getId(), LocalDateTime.now());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCodigo()).isEqualTo("999888");
    }

    @Test
    @DisplayName("Deve buscar códigos expirados")
    void testFindExpiredCodigos() {
        // When
        List<TwoFactorCode> result = twoFactorCodeRepository.findExpiredCodes(LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(codigoExpirado.getId());
        assertThat(result).allMatch(c -> c.getExpiryDate().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Deve incrementar e contar tentativas de um código")
    void testCountTentativas() {
        // Given
        codigoValido.setTentativas(1);
        twoFactorCodeRepository.save(codigoValido);
        entityManager.flush();
        entityManager.clear();

        // When
        TwoFactorCode updated = twoFactorCodeRepository.findById(codigoValido.getId()).orElseThrow();

        // Then
        assertThat(updated.getTentativas()).isEqualTo(1);

        // Incrementar novamente
        updated.setTentativas(updated.getTentativas() + 1);
        twoFactorCodeRepository.save(updated);
        entityManager.flush();
        entityManager.clear();

        TwoFactorCode finalCode = twoFactorCodeRepository.findById(codigoValido.getId()).orElseThrow();
        assertThat(finalCode.getTentativas()).isEqualTo(2);
    }
}
