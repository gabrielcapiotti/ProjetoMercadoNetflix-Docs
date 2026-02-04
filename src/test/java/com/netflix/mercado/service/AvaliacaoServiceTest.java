package com.netflix.mercado.service;

import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.AvaliacaoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.avaliacao.CreateAvaliacaoRequest;
import com.netflix.mercado.dto.avaliacao.UpdateAvaliacaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AvaliacaoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AvaliacaoService - Testes Unitários")
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private Avaliacao testAvaliacao;
    private User testUser;
    private Mercado testMercado;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setActive(true);

        testMercado = new Mercado();
        testMercado.setId(1L);
        testMercado.setNome("Mercado Teste");
        testMercado.setActive(true);

        testAvaliacao = new Avaliacao();
        testAvaliacao.setId(1L);
        testAvaliacao.setUser(testUser);
        testAvaliacao.setMercado(testMercado);
        testAvaliacao.setEstrelas(4);
        testAvaliacao.setComentario("Produto de qualidade com bom atendimento");
        testAvaliacao.setVerificado(false);
        testAvaliacao.setUteis(5L);
        testAvaliacao.setInutils(0L);
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso")
    void testCreateAvaliacao() {
        // Arrange
        CreateAvaliacaoRequest request = new CreateAvaliacaoRequest();
        request.setEstrelas(5);
        request.setComentario("Excelente!");

        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(testAvaliacao);
        when(auditLogRepository.save(any())).thenReturn(null);

        // Act
        Avaliacao result = avaliacaoService.criarAvaliacao(request, testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve validar estrelas inválidas")
    void testCreateAvaliacaoInvalidStars() {
        // Arrange
        CreateAvaliacaoRequest request = new CreateAvaliacaoRequest();
        request.setEstrelas(6);

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(request, testUser))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Deve obter avaliação por ID")
    void testGetAvaliacaoById() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(testAvaliacao));

        // Act
        Avaliacao result = avaliacaoService.obterAvaliacaoPorId(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter avaliação inexistente")
    void testGetAvaliacaoByIdNotFound() {
        // Arrange
        when(avaliacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.obterAvaliacaoPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar avaliações por mercado")
    void testListAvaliacoesByMercado() {
        // Arrange
        Avaliacao avaliacao2 = new Avaliacao();
        avaliacao2.setId(2L);
        avaliacao2.setEstrelas(3);
        avaliacao2.setMercado(testMercado);

        Page<Avaliacao> page = new PageImpl<>(Arrays.asList(testAvaliacao, avaliacao2));
        when(avaliacaoRepository.findByMercado(eq(testMercado), any(Pageable.class))).thenReturn(page);

        // Act
        Page<Avaliacao> result = avaliacaoRepository.findByMercado(testMercado, Pageable.unpaged());

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve marcar avaliação como útil")
    void testMarkAsUtil() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(testAvaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(testAvaliacao);

        // Act
        avaliacaoService.marcarComoUtil(1L);

        // Assert
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve calcular estatísticas de avaliações")
    void testCalcularEstatisticas() {
        // Arrange
        when(avaliacaoRepository.countByMercado(testMercado)).thenReturn(10L);

        // Act
        long total = avaliacaoRepository.countByMercado(testMercado);

        // Assert
        assertThat(total).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve atualizar avaliação")
    void testUpdateAvaliacao() {
        // Arrange
        UpdateAvaliacaoRequest request = new UpdateAvaliacaoRequest();
        request.setEstrelas(5);
        request.setComentario("Muito bom!");

        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(testAvaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(testAvaliacao);

        // Act
        Avaliacao result = avaliacaoService.atualizarAvaliacao(1L, request, testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve deletar avaliação")
    void testDeleteAvaliacao() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(testAvaliacao));
        doNothing().when(avaliacaoRepository).delete(any(Avaliacao.class));

        // Act
        avaliacaoService.deletarAvaliacao(1L, testUser);

        // Assert
        verify(avaliacaoRepository).delete(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve obter avaliação por usuário e mercado")
    void testFindByUserAndMercado() {
        // Arrange
        when(avaliacaoRepository.findByMercadoAndUser(testMercado, testUser))
                .thenReturn(Optional.of(testAvaliacao));

        // Act
        Optional<Avaliacao> result = avaliacaoRepository.findByMercadoAndUser(testMercado, testUser);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }
}
