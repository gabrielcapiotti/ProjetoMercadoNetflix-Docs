package com.netflix.mercado.repository;

import com.netflix.mercado.entity.HorarioFuncionamento;
import com.netflix.mercado.entity.Mercado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para HorarioFuncionamentoRepository
 * Coverage: 6 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("HorarioFuncionamentoRepository Integration Tests")
class HorarioFuncionamentoRepositoryTest {

    @Autowired
    private HorarioFuncionamentoRepository horarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Mercado mercado;
    private HorarioFuncionamento horarioSegunda;
    private HorarioFuncionamento horarioTerca;
    private HorarioFuncionamento horarioDomingo;

    @BeforeEach
    void setUp() {
        // Criar mercado
        mercado = new Mercado();
        mercado.setNome("Mercado 24h");
        mercado.setCnpj("11111111000111");
        mercado.setEmail("mercado24h@example.com");
        mercado.setTelefone("1111111111");
        mercado.setEndereco("Rua Principal, 100");
        mercado.setCidade("São Paulo");
        mercado.setEstado("SP");
        mercado.setCep("01000-000");
        mercado.setLatitude(-23.5505);
        mercado.setLongitude(-46.6333);
        mercado.setHorarioAbertura(LocalTime.of(0, 0));
        mercado.setHorarioFechamento(LocalTime.of(23, 59));
        mercado.setActive(true);
        mercado = entityManager.persistAndFlush(mercado);

        // Criar horários
        horarioSegunda = new HorarioFuncionamento();
        horarioSegunda.setMercado(mercado);
        horarioSegunda.setDiaSemana(HorarioFuncionamento.DiaSemana.SEGUNDA);
        horarioSegunda.setHoraAbertura(LocalTime.of(8, 0));
        horarioSegunda.setHoraFechamento(LocalTime.of(22, 0));
        horarioSegunda.setAberto(true);
        horarioSegunda.setActive(true);
        horarioSegunda = entityManager.persistAndFlush(horarioSegunda);

        horarioTerca = new HorarioFuncionamento();
        horarioTerca.setMercado(mercado);
        horarioTerca.setDiaSemana(HorarioFuncionamento.DiaSemana.TERCA);
        horarioTerca.setHoraAbertura(LocalTime.of(8, 0));
        horarioTerca.setHoraFechamento(LocalTime.of(22, 0));
        horarioTerca.setAberto(true);
        horarioTerca.setActive(true);
        horarioTerca = entityManager.persistAndFlush(horarioTerca);

        horarioDomingo = new HorarioFuncionamento();
        horarioDomingo.setMercado(mercado);
        horarioDomingo.setDiaSemana(HorarioFuncionamento.DiaSemana.DOMINGO);
        horarioDomingo.setHoraAbertura(LocalTime.of(9, 0));
        horarioDomingo.setHoraFechamento(LocalTime.of(18, 0));
        horarioDomingo.setAberto(false); // Fechado aos domingos
        horarioDomingo.setActive(true);
        horarioDomingo = entityManager.persistAndFlush(horarioDomingo);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve listar horários de funcionamento de um mercado")
    void testFindByMercado() {
        // When
        List<HorarioFuncionamento> result = horarioRepository.findByMercado(mercado);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(h -> h.getMercado().getId().equals(mercado.getId()));
        assertThat(result).allMatch(HorarioFuncionamento::getActive);
    }

    @Test
    @DisplayName("Deve encontrar horário específico de um dia")
    void testFindByMercadoAndDia() {
        // When
        Optional<HorarioFuncionamento> result = horarioRepository.findByMercadoAndDia(
                mercado, HorarioFuncionamento.DiaSemana.SEGUNDA);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDiaSemana()).isEqualTo(HorarioFuncionamento.DiaSemana.SEGUNDA);
        assertThat(result.get().getHoraAbertura()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.get().getHoraFechamento()).isEqualTo(LocalTime.of(22, 0));
    }

    @Test
    @DisplayName("Deve buscar apenas horários de dias abertos")
    void testFindOpenHorariosByMercado() {
        // When
        List<HorarioFuncionamento> result = horarioRepository.findOpenHorariosByMercado(mercado);

        // Then
        assertThat(result).hasSize(2); // Segunda e terça
        assertThat(result).allMatch(HorarioFuncionamento::getAberto);
        assertThat(result).noneMatch(h -> h.getDiaSemana() == HorarioFuncionamento.DiaSemana.DOMINGO);
    }

    @Test
    @DisplayName("Deve contar horários por mercado")
    void testCountHorariosPorMercado() {
        // When
        long count = horarioRepository.countByMercado(mercado);

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve atualizar horário de funcionamento")
    void testUpdateHorario() {
        // Given
        LocalTime novaAbertura = LocalTime.of(7, 0);
        LocalTime novoFechamento = LocalTime.of(23, 0);

        // When
        horarioSegunda.setHoraAbertura(novaAbertura);
        horarioSegunda.setHoraFechamento(novoFechamento);
        horarioRepository.save(horarioSegunda);
        entityManager.flush();
        entityManager.clear();

        // Then
        HorarioFuncionamento updated = horarioRepository.findById(horarioSegunda.getId()).orElseThrow();
        assertThat(updated.getHoraAbertura()).isEqualTo(novaAbertura);
        assertThat(updated.getHoraFechamento()).isEqualTo(novoFechamento);
    }

    @Test
    @DisplayName("Deve buscar horário por dia da semana")
    void testFindHorarioByDiaSemana() {
        // When
        Optional<HorarioFuncionamento> result = horarioRepository.findByMercadoAndDia(
                mercado, HorarioFuncionamento.DiaSemana.DOMINGO);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDiaSemana()).isEqualTo(HorarioFuncionamento.DiaSemana.DOMINGO);
        assertThat(result.get().getAberto()).isFalse();
    }
}
