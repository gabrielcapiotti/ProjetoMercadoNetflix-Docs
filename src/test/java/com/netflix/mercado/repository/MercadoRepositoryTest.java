package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Mercado;
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
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para MercadoRepository
 * Coverage: 10 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("MercadoRepository Integration Tests")
class MercadoRepositoryTest {

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Mercado mercado1;
    private Mercado mercado2;
    private Mercado mercadoDeleted;

    @BeforeEach
    void setUp() {
        mercado1 = new Mercado();
        mercado1.setNome("Supermercado Central");
        mercado1.setCnpj("12345678000190");
        mercado1.setEmail("central@mercado.com");
        mercado1.setTelefone("1133334444");
        mercado1.setEndereco("Rua Principal, 100");
        mercado1.setCidade("São Paulo");
        mercado1.setEstado("SP");
        mercado1.setCep("01000-000");
        mercado1.setLatitude(BigDecimal.valueOf(-23.5505));
        mercado1.setLongitude(BigDecimal.valueOf(-46.6333));
        mercado1.setAvaliacaoMedia(BigDecimal.valueOf(4.5));
        mercado1.setActive(true);
        mercado1 = entityManager.persistAndFlush(mercado1);

        mercado2 = new Mercado();
        mercado2.setNome("Mercadinho Bairro");
        mercado2.setCnpj("98765432000199");
        mercado2.setEmail("bairro@mercado.com");
        mercado2.setTelefone("1144445555");
        mercado2.setEndereco("Av. Secundária, 200");
        mercado2.setCidade("São Paulo");
        mercado2.setEstado("SP");
        mercado2.setCep("02000-000");
        mercado2.setLatitude(BigDecimal.valueOf(-23.5605));
        mercado2.setLongitude(BigDecimal.valueOf(-46.6433));
        mercado2.setAvaliacaoMedia(BigDecimal.valueOf(3.8));
        mercado2.setActive(true);
        mercado2 = entityManager.persistAndFlush(mercado2);

        mercadoDeleted = new Mercado();
        mercadoDeleted.setNome("Mercado Fechado");
        mercadoDeleted.setCnpj("11122233000188");
        mercadoDeleted.setEmail("fechado@mercado.com");
        mercadoDeleted.setTelefone("1155556666");
        mercadoDeleted.setEndereco("Rua Antiga, 300");
        mercadoDeleted.setCidade("Rio de Janeiro");
        mercadoDeleted.setEstado("RJ");
        mercadoDeleted.setCep("20000-000");
        mercadoDeleted.setLatitude(-22.9068);
        mercadoDeleted.setLongitude(-43.1729);
        mercadoDeleted.setAvaliacaoMedia(BigDecimal.valueOf(2.0));
        mercadoDeleted.setActive(false); // Soft deleted
        mercadoDeleted = entityManager.persistAndFlush(mercadoDeleted);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve encontrar mercado por nome com sucesso")
    void testFindByNomeSuccess() {
        // When
        Page<Mercado> result = mercadoRepository.findByNomeContainingIgnoreCase("Central", PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNome()).contains("Central");
    }

    @Test
    @DisplayName("Deve encontrar mercados por cidade com sucesso")
    void testFindByCidadeSuccess() {
        // When
        Page<Mercado> result = mercadoRepository.findByCidade("São Paulo", PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(m -> m.getCidade().equals("São Paulo"));
    }

    @Test
    @DisplayName("Deve buscar por cidade case-insensitive")
    void testFindByCidadeCaseInsensitive() {
        // When
        Page<Mercado> result = mercadoRepository.findByCidade("são paulo", PageRequest.of(0, 10));

        // Then - H2 é case-insensitive por padrão
        assertThat(result.getContent()).allMatch(m -> m.getCidade().equalsIgnoreCase("são paulo"));
    }

    @Test
    @DisplayName("Deve filtrar mercados por avaliação mínima")
    void testFindByAvaliacaoMediaGreaterThan() {
        // When
        Page<Mercado> result = mercadoRepository.findByAvaliacaoMediaGreaterThanEqual(
                BigDecimal.valueOf(4.0), PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Supermercado Central");
        assertThat(result.getContent().get(0).getAvaliacaoMedia()).isGreaterThanOrEqualTo(BigDecimal.valueOf(4.0));
    }

    @Test
    @DisplayName("Deve buscar mercados próximos usando Haversine")
    void testFindByProximidade() {
        // Given - Centro de São Paulo
        double latitude = -23.5505;
        double longitude = -46.6333;
        double raioKm = 10.0;

        // When
        List<Mercado> result = mercadoRepository.findByProximidade(latitude, longitude, raioKm);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Não deve retornar mercados fora do raio de proximidade")
    void testFindByProximidadeNoResults() {
        // Given - Localização distante
        double latitude = -15.7939; // Brasília
        double longitude = -47.8828;
        double raioKm = 1.0;

        // When
        List<Mercado> result = mercadoRepository.findByProximidade(latitude, longitude, raioKm);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve realizar soft delete do mercado")
    void testSoftDeleteMercado() {
        // When
        mercado1.setActive(false);
        mercadoRepository.save(mercado1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Mercado deleted = mercadoRepository.findById(mercado1.getId()).orElseThrow();
        assertThat(deleted.getActive()).isFalse();

        // Não deve aparecer na busca de ativos
        Page<Mercado> activeResult = mercadoRepository.findAllActive(PageRequest.of(0, 10));
        assertThat(activeResult.getContent()).noneMatch(m -> m.getId().equals(mercado1.getId()));
    }

    @Test
    @DisplayName("Deve buscar apenas mercados ativos")
    void testFindOnlyActiveMercados() {
        // When
        Page<Mercado> result = mercadoRepository.findAllActive(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2); // mercado1 e mercado2
        assertThat(result.getContent()).allMatch(Mercado::getActive);
        assertThat(result.getContent()).noneMatch(m -> m.getId().equals(mercadoDeleted.getId()));
    }

    @Test
    @DisplayName("Deve buscar mercados com avaliações")
    void testFindMercadosComAvaliacoes() {
        // When
        Page<Mercado> result = mercadoRepository.findByAvaliacaoMedia(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).allMatch(m -> m.getAvaliacaoMedia() != null);
        // Ordenado por avaliação DESC
        assertThat(result.getContent().get(0).getAvaliacaoMedia())
                .isGreaterThanOrEqualTo(result.getContent().get(1).getAvaliacaoMedia());
    }

    @Test
    @DisplayName("Deve contar apenas mercados ativos")
    void testCountActiveMercados() {
        // When
        long count = mercadoRepository.countActiveMarkets();

        // Then
        assertThat(count).isEqualTo(2); // mercado1 e mercado2
    }
}
