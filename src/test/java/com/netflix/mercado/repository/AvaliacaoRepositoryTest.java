package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.Mercado;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para AvaliacaoRepository
 * Coverage: 10 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("AvaliacaoRepository Integration Tests")
class AvaliacaoRepositoryTest {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private Mercado mercado1;
    private Mercado mercado2;
    private Avaliacao avaliacao1;
    private Avaliacao avaliacao2;
    private Avaliacao avaliacaoDeleted;

    @BeforeEach
    void setUp() {
        // Criar role
        Role customerRole = new Role();
        customerRole.setName(Role.RoleName.USER);
        customerRole.setDescription("Cliente");
        customerRole.setActive(true);
        customerRole = entityManager.persistAndFlush(customerRole);

        // Criar usuários
        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPasswordHash("$2a$10$hash1");
        user1.setFullName("User One");
        user1.setCpf("11111111111");
        user1.setPhone("11111111111");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        user1.setActive(true);
        Set<Role> roles1 = new HashSet<>();
        roles1.add(customerRole);
        user1.setRoles(roles1);
        user1 = entityManager.persistAndFlush(user1);

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("$2a$10$hash2");
        user2.setFullName("User Two");
        user2.setCpf("22222222222");
        user2.setPhone("22222222222");
        user2.setBirthDate(LocalDate.of(1992, 2, 2));
        user2.setActive(true);
        Set<Role> roles2 = new HashSet<>();
        roles2.add(customerRole);
        user2.setRoles(roles2);
        user2 = entityManager.persistAndFlush(user2);

        // Criar mercados
        mercado1 = new Mercado();
        mercado1.setNome("Mercado A");
        mercado1.setDescricao("Mercado A para testes de avaliações");
        mercado1.setCnpj("11111111000111");
        mercado1.setEmail("mercadoa@example.com");
        mercado1.setTelefone("1111111111");
        mercado1.setEndereco("Rua A, 100");
        mercado1.setBairro("Centro");
        mercado1.setCidade("São Paulo");
        mercado1.setEstado("SP");
        mercado1.setCep("01000-000");
        mercado1.setLatitude(BigDecimal.valueOf(-23.5505));
        mercado1.setLongitude(BigDecimal.valueOf(-46.6333));
        mercado1.setActive(true);
        mercado1 = entityManager.persistAndFlush(mercado1);

        mercado2 = new Mercado();
        mercado2.setNome("Mercado B");
        mercado2.setDescricao("Mercado B para testes de avaliações");
        mercado2.setCnpj("22222222000222");
        mercado2.setEmail("mercadob@example.com");
        mercado2.setTelefone("2222222222");
        mercado2.setEndereco("Rua B, 200");
        mercado2.setBairro("Centro");
        mercado2.setCidade("Rio de Janeiro");
        mercado2.setEstado("RJ");
        mercado2.setCep("20000-000");
        mercado2.setLatitude(BigDecimal.valueOf(-22.9068));
        mercado2.setLongitude(BigDecimal.valueOf(-43.1729));
        mercado2.setActive(true);
        mercado2 = entityManager.persistAndFlush(mercado2);

        // Criar avaliações
        avaliacao1 = new Avaliacao();
        avaliacao1.setUser(user1);
        avaliacao1.setMercado(mercado1);
        avaliacao1.setEstrelas(5);
        avaliacao1.setComentario("Excelente mercado!");
        avaliacao1.setVerificado(true);
        avaliacao1.setActive(true);
        avaliacao1 = entityManager.persistAndFlush(avaliacao1);

        avaliacao2 = new Avaliacao();
        avaliacao2.setUser(user2);
        avaliacao2.setMercado(mercado1);
        avaliacao2.setEstrelas(4);
        avaliacao2.setComentario("Muito bom!");
        avaliacao2.setVerificado(false);
        avaliacao2.setActive(true);
        avaliacao2 = entityManager.persistAndFlush(avaliacao2);

        avaliacaoDeleted = new Avaliacao();
        avaliacaoDeleted.setUser(user1);
        avaliacaoDeleted.setMercado(mercado2);
        avaliacaoDeleted.setEstrelas(2);
        avaliacaoDeleted.setComentario("Avaliação deletada");
        avaliacaoDeleted.setVerificado(true);
        avaliacaoDeleted.setActive(false); // Soft deleted
        avaliacaoDeleted = entityManager.persistAndFlush(avaliacaoDeleted);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve listar avaliações por mercado")
    void testFindByMercadoSuccess() {
        // When
        Page<Avaliacao> result = avaliacaoRepository.findByMercado(mercado1, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(a -> a.getMercado().getId().equals(mercado1.getId()));
        assertThat(result.getContent()).allMatch(Avaliacao::getActive);
    }

    @Test
    @DisplayName("Deve listar avaliações por usuário")
    void testFindByUserSuccess() {
        // When
        Page<Avaliacao> result = avaliacaoRepository.findByUser(user1, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1); // Apenas avaliacao1 ativa
        assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(user1.getId());
        assertThat(result.getContent()).allMatch(Avaliacao::getActive);
    }

    @Test
    @DisplayName("Deve garantir unique constraint (um usuário, uma avaliação por mercado)")
    void testFindByMercadoAndUserUnique() {
        // When
        Optional<Avaliacao> result = avaliacaoRepository.findByMercadoAndUser(mercado1, user1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(avaliacao1.getId());
    }

    @Test
    @DisplayName("Não deve encontrar avaliação inexistente")
    void testFindByMercadoAndUserNotFound() {
        // When
        Optional<Avaliacao> result = avaliacaoRepository.findByMercadoAndUser(mercado2, user2);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve calcular média de avaliações do mercado")
    void testCalcularMediaAvaliacoes() {
        // When
        BigDecimal media = avaliacaoRepository.calcularMediaAvaliacoes(mercado1);

        // Then
        assertThat(media).isNotNull();
        assertThat(media).isEqualByComparingTo(BigDecimal.valueOf(4.5)); // (5 + 4) / 2 = 4.5
    }

    @Test
    @DisplayName("Deve buscar avaliações não verificadas")
    void testFindUnverifiedAvaliacoes() {
        // When
        Page<Avaliacao> result = avaliacaoRepository.findUnverifiedAvaliacoes(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(avaliacao2.getId());
        assertThat(result.getContent()).allMatch(a -> !a.getVerificado());
    }

    @Test
    @DisplayName("Deve contar avaliações por mercado")
    void testCountAvaliacoesPorMercado() {
        // When
        long count = avaliacaoRepository.countByMercado(mercado1);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve filtrar avaliações por estrelas mínimas")
    void testFindByEstrelasGreaterThan() {
        // When
        Page<Avaliacao> result = avaliacaoRepository.findByMercadoAndEstrelasGreaterThanEqual(
                mercado1, 5, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEstrelas()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve realizar soft delete de avaliação")
    void testSoftDeleteAvaliacao() {
        // When
        avaliacao1.setActive(false);
        avaliacaoRepository.save(avaliacao1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Avaliacao deleted = avaliacaoRepository.findById(avaliacao1.getId()).orElseThrow();
        assertThat(deleted.getActive()).isFalse();

        // Não deve aparecer na busca de ativas
        Page<Avaliacao> activeResult = avaliacaoRepository.findByMercado(
                mercado1, PageRequest.of(0, 10));
        assertThat(activeResult.getContent()).noneMatch(a -> a.getId().equals(avaliacao1.getId()));
    }

    @Test
    @DisplayName("Deve buscar apenas avaliações ativas")
    void testFindOnlyActiveAvaliacoes() {
        // When
        Page<Avaliacao> result = avaliacaoRepository.findByMercado(mercado1, PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).allMatch(Avaliacao::getActive);
        assertThat(result.getContent()).hasSize(2);
    }
}
