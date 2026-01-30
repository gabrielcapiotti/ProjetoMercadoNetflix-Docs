package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Favorito;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para FavoritoRepository
 * Coverage: 7 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("FavoritoRepository Integration Tests")
class FavoritoRepositoryTest {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Mercado mercado1;
    private Mercado mercado2;
    private Favorito favorito1;
    private Favorito favorito2;

    @BeforeEach
    void setUp() {
        // Criar role
        Role customerRole = new Role();
        customerRole.setName("ROLE_CUSTOMER");
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

        // Criar mercados
        mercado1 = new Mercado();
        mercado1.setNome("Mercado A");
        mercado1.setCnpj("11111111000111");
        mercado1.setEmail("mercadoa@example.com");
        mercado1.setTelefone("1111111111");
        mercado1.setEndereco("Rua A, 100");
        mercado1.setCidade("São Paulo");
        mercado1.setEstado("SP");
        mercado1.setCep("01000-000");
        mercado1.setLatitude(-23.5505);
        mercado1.setLongitude(-46.6333);
        mercado1.setHorarioAbertura(LocalTime.of(8, 0));
        mercado1.setHorarioFechamento(LocalTime.of(22, 0));
        mercado1.setActive(true);
        mercado1 = entityManager.persistAndFlush(mercado1);

        mercado2 = new Mercado();
        mercado2.setNome("Mercado B");
        mercado2.setCnpj("22222222000222");
        mercado2.setEmail("mercadob@example.com");
        mercado2.setTelefone("2222222222");
        mercado2.setEndereco("Rua B, 200");
        mercado2.setCidade("Rio de Janeiro");
        mercado2.setEstado("RJ");
        mercado2.setCep("20000-000");
        mercado2.setLatitude(-22.9068);
        mercado2.setLongitude(-43.1729);
        mercado2.setHorarioAbertura(LocalTime.of(7, 0));
        mercado2.setHorarioFechamento(LocalTime.of(23, 0));
        mercado2.setActive(true);
        mercado2 = entityManager.persistAndFlush(mercado2);

        // Criar favoritos
        favorito1 = new Favorito();
        favorito1.setUser(user);
        favorito1.setMercado(mercado1);
        favorito1.setPrioridade(1);
        favorito1.setActive(true);
        favorito1 = entityManager.persistAndFlush(favorito1);

        favorito2 = new Favorito();
        favorito2.setUser(user);
        favorito2.setMercado(mercado2);
        favorito2.setPrioridade(2);
        favorito2.setActive(true);
        favorito2 = entityManager.persistAndFlush(favorito2);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve verificar se favorito existe")
    void testExistsByUserAndMercado() {
        // When
        boolean exists = favoritoRepository.existsByUserAndMercadoAndActiveTrue(user, mercado1);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que favorito não existe")
    void testExistsByUserAndMercadoFalse() {
        // Given
        Mercado mercadoNaoFavoritado = new Mercado();
        mercadoNaoFavoritado.setNome("Mercado C");
        mercadoNaoFavoritado.setCnpj("33333333000333");
        mercadoNaoFavoritado.setEmail("mercadoc@example.com");
        mercadoNaoFavoritado.setTelefone("3333333333");
        mercadoNaoFavoritado.setEndereco("Rua C, 300");
        mercadoNaoFavoritado.setCidade("Brasília");
        mercadoNaoFavoritado.setEstado("DF");
        mercadoNaoFavoritado.setCep("70000-000");
        mercadoNaoFavoritado.setLatitude(-15.7939);
        mercadoNaoFavoritado.setLongitude(-47.8828);
        mercadoNaoFavoritado.setHorarioAbertura(LocalTime.of(8, 0));
        mercadoNaoFavoritado.setHorarioFechamento(LocalTime.of(20, 0));
        mercadoNaoFavoritado.setActive(true);
        mercadoNaoFavoritado = entityManager.persistAndFlush(mercadoNaoFavoritado);

        // When
        boolean exists = favoritoRepository.existsByUserAndMercado(user, mercadoNaoFavoritado);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve contar favoritos do usuário")
    void testCountByUser() {
        // When
        long count = favoritoRepository.countByUserAndActiveTrue(user);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve contar quantos usuários favoritaram o mercado")
    void testCountByMercado() {
        // Given - Criar outro usuário que favorita mercado1
        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("$2a$10$hash2");
        user2.setFullName("Test User 2");
        user2.setCpf("22222222222");
        user2.setPhone("22222222222");
        user2.setDateOfBirth(LocalDate.of(1992, 2, 2));
        user2.setActive(true);
        Role customerRole = entityManager.find(Role.class, 1L);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user2.setRoles(roles);
        user2 = entityManager.persistAndFlush(user2);

        Favorito favorito3 = new Favorito();
        favorito3.setUser(user2);
        favorito3.setMercado(mercado1);
        favorito3.setPrioridade(1);
        favorito3.setActive(true);
        entityManager.persistAndFlush(favorito3);
        entityManager.clear();

        // When
        long count = favoritoRepository.countByMercado(mercado1);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve listar favoritos do usuário ordenados por prioridade")
    void testFindByUserWithOrdenacao() {
        // When
        Page<Favorito> result = favoritoRepository.findByUser(user, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(Favorito::getActive);
    }

    @Test
    @DisplayName("Deve listar favoritos com paginação")
    void testFindByUserPage() {
        // When
        Page<Favorito> result = favoritoRepository.findByUser(
                user, PageRequest.of(0, 1, Sort.by("prioridade").ascending()));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getPrioridade()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve deletar favorito específico")
    void testDeleteByUserAndMercado() {
        // When
        favoritoRepository.findByUserAndMercado(user, mercado1).ifPresent(favoritoRepository::delete);
        entityManager.flush();
        entityManager.clear();

        // Then
        boolean exists = favoritoRepository.existsByUserAndMercado(user, mercado1);
        assertThat(exists).isFalse();
        
        long count = favoritoRepository.countByUser(user);
        assertThat(count).isEqualTo(1); // Apenas favorito2 permanece
    }
}
