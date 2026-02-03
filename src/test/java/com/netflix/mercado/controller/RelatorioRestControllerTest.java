package com.netflix.mercado.controller;

import com.netflix.mercado.dto.relatorio.*;
import com.netflix.mercado.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelatorioRestController - Testes Unitários")
class RelatorioRestControllerTest {

    @Mock
    private RelatorioService relatorioService;

    @InjectMocks
    private RelatorioRestController controller;

    private RelatorioGeralResponse testRelatorioGeral;
    private RelatorioMercadoResponse testRelatorioMercado;
    private List<RankingMercadoResponse> testRanking;
    private List<MercadoPoucasAvaliacoesResponse> testPoucasAvaliacoes;
    private RelatorioComentariosResponse testRelatorioComentarios;

    @BeforeEach
    void setUp() {
        testRelatorioGeral = RelatorioGeralResponse.builder()
                .dataGeracao(LocalDateTime.now())
                .totalMercados(100L)
                .totalAvaliacoes(5000L)
                .totalComentarios(1500L)
                .totalPromocoes(50L)
                .mediaAvaliacoes(BigDecimal.valueOf(4.2))
                .mercadoMelhorAvaliado("Mercado Premium")
                .avaliacaoMelhorMercado(BigDecimal.valueOf(4.9))
                .mercadoMaisAvaliado("Supermercado Popular")
                .totalAvaliacoesMercadoMaisAvaliado(250)
                .build();

        Map<Integer, Long> distribuicaoEstrelas = new HashMap<>();
        distribuicaoEstrelas.put(5, 50L);
        distribuicaoEstrelas.put(4, 30L);
        distribuicaoEstrelas.put(3, 15L);
        distribuicaoEstrelas.put(2, 3L);
        distribuicaoEstrelas.put(1, 2L);

        testRelatorioMercado = RelatorioMercadoResponse.builder()
                .mercadoId(1L)
                .nomeMercado("Mercado Central")
                .dataGeracao(LocalDateTime.now())
                .avaliacaoMedia(BigDecimal.valueOf(4.5))
                .totalAvaliacoes(100L)
                .totalComentarios(50L)
                .totalPromocoesAtivas(5L)
                .distribuicaoEstrelas(distribuicaoEstrelas)
                .build();

        testRanking = Arrays.asList(
                RankingMercadoResponse.builder()
                        .posicao(1)
                        .nome("Mercado A")
                        .avaliacaoMedia(BigDecimal.valueOf(4.9))
                        .totalAvaliacoes(500L)
                        .cidade("São Paulo")
                        .build(),
                RankingMercadoResponse.builder()
                        .posicao(2)
                        .nome("Mercado B")
                        .avaliacaoMedia(BigDecimal.valueOf(4.7))
                        .totalAvaliacoes(300L)
                        .cidade("Campinas")
                        .build()
        );

        testPoucasAvaliacoes = Arrays.asList(
                MercadoPoucasAvaliacoesResponse.builder()
                        .mercadoId(10L)
                        .nome("Mercado Novo")
                        .totalAvaliacoes(5L)
                        .avaliacaoMedia(BigDecimal.valueOf(4.0))
                        .cidade("Santos")
                        .estado("SP")
                        .build()
        );

        testRelatorioComentarios = RelatorioComentariosResponse.builder()
                .dataGeracao(LocalDateTime.now())
                .totalComentarios(1000L)
                .comentariosAtivos(950L)
                .comentariosAguardandoModeração(50L)
                .percentualAtivos(BigDecimal.valueOf(95.00))
                .mediaCurtidas(BigDecimal.valueOf(5.5))
                .comentarioMaisCurtido("Excelente mercado!")
                .build();
    }

    @Test
    @DisplayName("Deve gerar relatório geral com sucesso")
    void testRelatorioGeral_Success() {
        // Arrange
        when(relatorioService.gerarRelatorioGeral()).thenReturn(testRelatorioGeral);

        // Act
        ResponseEntity<RelatorioGeralResponse> response = controller.relatorioGeral();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalMercados()).isEqualTo(100L);
        assertThat(response.getBody().getTotalAvaliacoes()).isEqualTo(5000L);
        assertThat(response.getBody().getMediaAvaliacoes()).isEqualByComparingTo(BigDecimal.valueOf(4.2));

        verify(relatorioService, times(1)).gerarRelatorioGeral();
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando falhar ao gerar relatório geral")
    void testRelatorioGeral_Error() {
        // Arrange
        when(relatorioService.gerarRelatorioGeral())
                .thenThrow(new RuntimeException("Erro ao gerar relatório"));

        // Act
        ResponseEntity<RelatorioGeralResponse> response = controller.relatorioGeral();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve gerar relatório de mercado específico com sucesso")
    void testRelatorioMercado_Success() {
        // Arrange
        when(relatorioService.gerarRelatorioMercado(1L)).thenReturn(testRelatorioMercado);

        // Act
        ResponseEntity<RelatorioMercadoResponse> response = controller.relatorioMercado(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMercadoId()).isEqualTo(1L);
        assertThat(response.getBody().getNomeMercado()).isEqualTo("Mercado Central");
        assertThat(response.getBody().getTotalAvaliacoes()).isEqualTo(100);

        verify(relatorioService, times(1)).gerarRelatorioMercado(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando mercado não encontrado")
    void testRelatorioMercado_NotFound() {
        // Arrange
        when(relatorioService.gerarRelatorioMercado(999L))
                .thenThrow(new IllegalArgumentException("Mercado não encontrado"));

        // Act
        ResponseEntity<RelatorioMercadoResponse> response = controller.relatorioMercado(999L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve gerar ranking de mercados com sucesso")
    void testRanking_Success() {
        // Arrange
        when(relatorioService.gerarRankingMercados(20)).thenReturn(testRanking);

        // Act
        ResponseEntity<List<RankingMercadoResponse>> response = controller.ranking(20);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getPosicao()).isEqualTo(1);
        assertThat(response.getBody().get(0).getAvaliacaoMedia())
                .isGreaterThan(response.getBody().get(1).getAvaliacaoMedia());

        verify(relatorioService, times(1)).gerarRankingMercados(20);
    }

    @Test
    @DisplayName("Deve listar mercados com poucas avaliações")
    void testMercadosPoucasAvaliacoes_Success() {
        // Arrange
        when(relatorioService.gerarRelatorioPoucasAvaliacoes(10)).thenReturn(testPoucasAvaliacoes);

        // Act
        ResponseEntity<List<MercadoPoucasAvaliacoesResponse>> response = 
                controller.mercadosPoucasAvaliacoes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTotalAvaliacoes()).isLessThan(10);

        verify(relatorioService, times(1)).gerarRelatorioPoucasAvaliacoes(10);
    }

    @Test
    @DisplayName("Deve gerar relatório de comentários com sucesso")
    void testRelatorioComentarios_Success() {
        // Arrange
        when(relatorioService.gerarRelatorioComentarios()).thenReturn(testRelatorioComentarios);

        // Act
        ResponseEntity<RelatorioComentariosResponse> response = controller.relatorioComentarios();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalComentarios()).isEqualTo(1000L);
        assertThat(response.getBody().getPercentualAtivos()).isEqualByComparingTo(BigDecimal.valueOf(95.00));

        verify(relatorioService, times(1)).gerarRelatorioComentarios();
    }

    @Test
    @DisplayName("Deve validar distribuição de estrelas no relatório do mercado")
    void testRelatorioMercado_DistribuicaoEstrelas() {
        // Arrange
        when(relatorioService.gerarRelatorioMercado(1L)).thenReturn(testRelatorioMercado);

        // Act
        ResponseEntity<RelatorioMercadoResponse> response = controller.relatorioMercado(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDistribuicaoEstrelas()).containsKeys(1, 2, 3, 4, 5);
        assertThat(response.getBody().getDistribuicaoEstrelas().get(5)).isEqualTo(50L);
    }

    @Test
    @DisplayName("Deve respeitar limite no ranking")
    void testRanking_WithCustomLimit() {
        // Arrange
        when(relatorioService.gerarRankingMercados(5)).thenReturn(testRanking.subList(0, 1));

        // Act
        ResponseEntity<List<RankingMercadoResponse>> response = controller.ranking(5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);

        verify(relatorioService, times(1)).gerarRankingMercados(5);
    }
}
