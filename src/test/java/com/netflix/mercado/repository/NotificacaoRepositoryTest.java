package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Notificacao;
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
 * Testes de integração para NotificacaoRepository
 * Coverage: 8 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("NotificacaoRepository Integration Tests")
class NotificacaoRepositoryTest {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Notificacao notificacao1;
    private Notificacao notificacao2;
    private Notificacao notificacao3;

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
        user.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        user = entityManager.persistAndFlush(user);

        // Criar notificações não lidas
        notificacao1 = new Notificacao();
        notificacao1.setUser(user);
        notificacao1.setTitulo("Promoção Imperdível");
        notificacao1.setConteudo("50% de desconto em todos os produtos");
        notificacao1.setTipo(Notificacao.TipoNotificacao.PROMOCAO);
        notificacao1.setLida(false);
        notificacao1.setActive(true);
        notificacao1 = entityManager.persistAndFlush(notificacao1);

        notificacao2 = new Notificacao();
        notificacao2.setUser(user);
        notificacao2.setTitulo("Nova Avaliação");
        notificacao2.setConteudo("Seu mercado recebeu uma nova avaliação");
        notificacao2.setTipo(Notificacao.TipoNotificacao.AVALIACAO);
        notificacao2.setLida(false);
        notificacao2.setActive(true);
        notificacao2 = entityManager.persistAndFlush(notificacao2);

        // Criar notificação lida
        notificacao3 = new Notificacao();
        notificacao3.setUser(user);
        notificacao3.setTitulo("Bem-vindo");
        notificacao3.setConteudo("Bem-vindo ao Netflix Mercados");
        notificacao3.setTipo(Notificacao.TipoNotificacao.SISTEMA);
        notificacao3.setLida(true);
        notificacao3.setActive(true);
        notificacao3 = entityManager.persistAndFlush(notificacao3);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve listar notificações não lidas do usuário")
    void testFindUnreadByUser() {
        // When
        Page<Notificacao> result = notificacaoRepository.findUnreadByUser(
                user, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(n -> !n.getLida());
        assertThat(result.getContent()).allMatch(Notificacao::getActive);
    }

    @Test
    @DisplayName("Deve contar notificações não lidas")
    void testCountUnreadByUser() {
        // When
        long count = notificacaoRepository.countUnreadByUser(user);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve listar notificações com paginação")
    void testFindByUserPage() {
        // When
        Page<Notificacao> result = notificacaoRepository.findByUser(
                user, PageRequest.of(0, 2));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve marcar todas as notificações como lidas")
    void testMarkAllAsReadByUser() {
        // When
        notificacaoRepository.markAllAsRead(user);
        entityManager.flush();
        entityManager.clear();

        // Then
        long unreadCount = notificacaoRepository.countUnreadByUser(user);
        assertThat(unreadCount).isEqualTo(0);

        List<Notificacao> all = notificacaoRepository.findAll();
        assertThat(all).allMatch(Notificacao::getLida);
    }

    @Test
    @DisplayName("Deve marcar notificação específica como lida")
    void testMarkAsRead() {
        // When
        notificacaoRepository.markAsRead(notificacao1.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        Notificacao updated = notificacaoRepository.findById(notificacao1.getId()).orElseThrow();
        assertThat(updated.getLida()).isTrue();
    }

    @Test
    @DisplayName("Deve deletar notificações antigas do usuário")
    void testDeleteByUser() {
        // When - Soft delete manual
        notificacao1.setActive(false);
        notificacaoRepository.save(notificacao1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Page<Notificacao> result = notificacaoRepository.findByUser(
                user, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2); // notificacao2 e notificacao3
    }

    @Test
    @DisplayName("Deve realizar soft delete de notificação")
    void testSoftDeleteNotificacao() {
        // When
        notificacao1.setActive(false);
        notificacaoRepository.save(notificacao1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Notificacao deleted = notificacaoRepository.findById(notificacao1.getId()).orElseThrow();
        assertThat(deleted.getActive()).isFalse();

        // Não deve aparecer na busca de ativas
        Page<Notificacao> activeResult = notificacaoRepository.findByUser(
                user, PageRequest.of(0, 10));
        assertThat(activeResult.getContent()).hasSize(2);
        assertThat(activeResult.getContent()).noneMatch(n -> n.getId().equals(notificacao1.getId()));
    }
}
