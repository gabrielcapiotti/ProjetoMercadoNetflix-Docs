package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.Promocao;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para PromocaoRepository
 * Coverage: 8 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("PromocaoRepository Integration Tests")
class PromocaoRepositoryTest {

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Mercado mercado;
    private Promocao promocaoAtiva;
    private Promocao promocaoExpirada;
    private Promocao promocaoEsgotada;

    @BeforeEach
    void setUp() {
        // Criar mercado
        mercado = new Mercado();
        mercado.setNome("Mercado Teste");
        mercado.setCnpj("11111111000111");
        mercado.setEmail("mercado@example.com");
        mercado.setTelefone("1111111111");
        mercado.setEndereco("Rua Teste, 100");
        mercado.setCidade("São Paulo");
        mercado.setEstado("SP");
        mercado.setCep("01000-000");
        mercado.setLatitude(BigDecimal.valueOf(-23.5505));
        mercado.setLongitude(BigDecimal.valueOf(-46.6333));
        mercado.setActive(true);
        mercado = entityManager.persistAndFlush(mercado);

        // Criar promoção ativa
        promocaoAtiva = new Promocao();
        promocaoAtiva.setMercado(mercado);
        promocaoAtiva.setDescricao("Super Desconto - 50% de desconto em frutas");
        promocaoAtiva.setCodigo("FRUTA50");
        promocaoAtiva.setPercentualDesconto(BigDecimal.valueOf(50));
        promocaoAtiva.setDataInicio(LocalDateTime.now().minusDays(1));
        promocaoAtiva.setDataValidade(LocalDateTime.now().plusDays(30));
        promocaoAtiva.setMaxUtilizacoes(100L);
        promocaoAtiva.setUtilizacoesAtuais(10L);
        promocaoAtiva.setAtiva(true);
        promocaoAtiva.setActive(true);
        promocaoAtiva = entityManager.persistAndFlush(promocaoAtiva);

        // Criar promoção expirada
        promocaoExpirada = new Promocao();
        promocaoExpirada.setMercado(mercado);
        promocaoExpirada.setDescricao("Promoção Antiga já expirada");
        promocaoExpirada.setCodigo("ANTIGA10");
        promocaoExpirada.setPercentualDesconto(BigDecimal.valueOf(10));
        promocaoExpirada.setDataInicio(LocalDateTime.now().minusDays(60));
        promocaoExpirada.setDataValidade(LocalDateTime.now().minusDays(30));
        promocaoExpirada.setMaxUtilizacoes(50L);
        promocaoExpirada.setUtilizacoesAtuais(5L);
        promocaoExpirada.setAtiva(false);
        promocaoExpirada.setActive(true);
        promocaoExpirada = entityManager.persistAndFlush(promocaoExpirada);

        // Criar promoção esgotada
        promocaoEsgotada = new Promocao();
        promocaoEsgotada.setMercado(mercado);
        promocaoEsgotada.setDescricao("Promoção Esgotada - Limite de uso atingido");
        promocaoEsgotada.setCodigo("ESGOTOU");
        promocaoEsgotada.setPercentualDesconto(BigDecimal.valueOf(30));
        promocaoEsgotada.setDataInicio(LocalDateTime.now().minusDays(5));
        promocaoEsgotada.setDataValidade(LocalDateTime.now().plusDays(5));
        promocaoEsgotada.setMaxUtilizacoes(20L);
        promocaoEsgotada.setUtilizacoesAtuais(20L); // Esgotada
        promocaoEsgotada.setAtiva(false);
        promocaoEsgotada.setActive(true);
        promocaoEsgotada = entityManager.persistAndFlush(promocaoEsgotada);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve listar promoções de um mercado")
    void testFindByMercado() {
        // When
        Page<Promocao> result = promocaoRepository.findByMercado(
                mercado, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).allMatch(p -> p.getMercado().getId().equals(mercado.getId()));
    }

    @Test
    @DisplayName("Deve encontrar promoção por código único")
    void testFindByCodigoSuccess() {
        // When
        Optional<Promocao> result = promocaoRepository.findByCodigo("FRUTA50");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDescricao()).contains("Super Desconto");
        assertThat(result.get().getPercentualDesconto()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    @DisplayName("Não deve encontrar promoção por código inexistente")
    void testFindByCodigoNotFound() {
        // When
        Optional<Promocao> result = promocaoRepository.findByCodigo("NAOEXISTE");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar apenas promoções ativas e válidas")
    void testFindActivePromocoes() {
        // When
        LocalDateTime agora = LocalDateTime.now();
        List<Promocao> result = promocaoRepository.findActivePromocoes(agora);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1); // Apenas promocaoAtiva
        assertThat(result.get(0).getCodigo()).isEqualTo("FRUTA50");
        assertThat(result).allMatch(p -> 
                p.getAtiva() && p.getDataValidade().isAfter(agora));
    }

    @Test
    @DisplayName("Deve buscar promoções esgotadas")
    void testFindExhaustedPromocoes() {
        // When
        List<Promocao> result = promocaoRepository.findExhaustedPromocoes();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("ESGOTOU");
        assertThat(result).allMatch(p -> 
                p.getUtilizacoesAtuais() >= p.getMaxUtilizacoes());
    }

    @Test
    @DisplayName("Deve contar utilizações de uma promoção")
    void testCountUtilizacoes() {
        // When
        Promocao promocao = promocaoRepository.findById(promocaoAtiva.getId()).orElseThrow();

        // Then
        assertThat(promocao.getUtilizacoesAtuais()).isEqualTo(10);
        assertThat(promocao.getUtilizacoesAtuais()).isLessThan(promocao.getMaxUtilizacoes());
    }

    @Test
    @DisplayName("Deve buscar promoções válidas até uma data")
    void testFindByDataValidadeGreater() {
        // When - Buscar promoções ativas válidas
        LocalDateTime agora = LocalDateTime.now();
        List<Promocao> result = promocaoRepository.findActivePromocoes(agora);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(p -> 
                p.getDataValidade().isAfter(agora));
    }

    @Test
    @DisplayName("Deve realizar soft delete de promoção")
    void testSoftDeletePromocao() {
        // When
        promocaoAtiva.setActive(false);
        promocaoRepository.save(promocaoAtiva);
        entityManager.flush();
        entityManager.clear();

        // Then
        Promocao deleted = promocaoRepository.findById(promocaoAtiva.getId()).orElseThrow();
        assertThat(deleted.getActive()).isFalse();

        // Não deve aparecer na busca de ativas
        Optional<Promocao> activeResult = promocaoRepository.findByCodigo("FRUTA50");
        assertThat(activeResult).isEmpty();
    }
}
