package com.netflix.mercado.repository;

import com.netflix.mercado.entity.AuditLog;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para AuditLogRepository
 * Coverage: 6 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("AuditLogRepository Integration Tests")
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private AuditLog log1;
    private AuditLog log2;
    private AuditLog log3;

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

        // Criar logs de auditoria
        log1 = new AuditLog();
        log1.setUser(user);
        log1.setTipoEntidade("Mercado");
        log1.setIdEntidade(1L);
        log1.setAcao(AuditLog.TipoAcao.CRIACAO);
        log1.setDescricao("Mercado criado");
        log1.setIpOrigem("192.168.1.1");
        log1.setUserAgent("Mozilla/5.0");
        log1.setActive(true);
        log1 = entityManager.persistAndFlush(log1);

        log2 = new AuditLog();
        log2.setUser(user);
        log2.setTipoEntidade("Mercado");
        log2.setIdEntidade(1L);
        log2.setAcao(AuditLog.TipoAcao.ATUALIZACAO);
        log2.setDescricao("Mercado atualizado");
        log2.setIpOrigem("192.168.1.1");
        log2.setUserAgent("Mozilla/5.0");
        log2.setActive(true);
        log2 = entityManager.persistAndFlush(log2);

        log3 = new AuditLog();
        log3.setUser(user);
        log3.setTipoEntidade("Avaliacao");
        log3.setIdEntidade(10L);
        log3.setAcao(AuditLog.TipoAcao.CRIACAO);
        log3.setDescricao("Avaliação criada");
        log3.setIpOrigem("192.168.1.2");
        log3.setUserAgent("Chrome/90.0");
        log3.setActive(true);
        log3 = entityManager.persistAndFlush(log3);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve buscar auditoria por usuário")
    void testFindByUser() {
        // When
        Page<AuditLog> result = auditLogRepository.findByUser(
                user, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).allMatch(log -> log.getUser().getId().equals(user.getId()));
    }

    @Test
    @DisplayName("Deve buscar auditoria por tipo de entidade")
    void testFindByTipoEntidade() {
        // When
        Page<AuditLog> result = auditLogRepository.findByTipoEntidade(
                "Mercado", PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(log -> log.getTipoEntidade().equals("Mercado"));
    }

    @Test
    @DisplayName("Deve buscar histórico completo de uma entidade")
    void testFindHistoricoEntidade() {
        // When
        List<AuditLog> result = auditLogRepository.findHistoricoEntidade(
                "Mercado", 1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(log -> log.getIdEntidade().equals(1L));
        assertThat(result).extracting(AuditLog::getAcao)
                .containsExactlyInAnyOrder(AuditLog.TipoAcao.CRIACAO, AuditLog.TipoAcao.ATUALIZACAO);
    }

    @Test
    @DisplayName("Deve buscar auditoria por intervalo de datas")
    void testFindByDataRange() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);

        // When
        List<AuditLog> result = auditLogRepository.findByDataRange(
                inicio, fim);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Deve buscar auditoria por tipo de ação")
    void testFindByTipoAcao() {
        // When
        Page<AuditLog> result = auditLogRepository.findByAcao(
                AuditLog.TipoAcao.CRIACAO, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(log -> log.getAcao() == AuditLog.TipoAcao.CRIACAO);
    }

    @Test
    @DisplayName("Deve contar ações de um usuário")
    void testCountAuditoriasByUser() {
        // When
        long count = auditLogRepository.countByUser(user);

        // Then
        assertThat(count).isEqualTo(3);
    }
}
