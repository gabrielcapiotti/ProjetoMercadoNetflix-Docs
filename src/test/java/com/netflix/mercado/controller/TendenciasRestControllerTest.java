package com.netflix.mercado.controller;

import com.netflix.mercado.dto.tendencias.AnaliseTendenciasResponse;
import com.netflix.mercado.dto.tendencias.TendenciaMercadoResponse;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.service.TendenciasService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TendenciasRestController - Testes Unitários")
class TendenciasRestControllerTest {

    @Mock
    private TendenciasService tendenciasService;

    @InjectMocks
    private TendenciasRestController controller;

    private AnaliseTendenciasResponse testAnaliseTendencias;
    private List<TendenciaMercadoResponse> testMercadosEmergentes;
    private List<TendenciaMercadoResponse> testMercadosConsolidados;
    private List<TendenciaMercadoResponse> testMelhorPerformance;

    @BeforeEach
    void setUp() {
        TendenciaMercadoResponse mercado1 = TendenciaMercadoResponse.builder()
                .mercadoId(1L)
                .nomeMercado("Mercado Crescente")
                .cidade("São Paulo")
                .estado("SP")
                .avaliacaoMedia(BigDecimal.valueOf(4.5))
                .totalAvaliacoes(45L)
                .crescimento(BigDecimal.valueOf(35.5))
                .tendencia("ALTA")
                .build();

        TendenciaMercadoResponse mercado2 = TendenciaMercadoResponse.builder()
                .mercadoId(2L)
                .nomeMercado("Supermercado Estabelecido")
                .cidade("Campinas")
                .estado("SP")
                .avaliacaoMedia(BigDecimal.valueOf(4.7))
                .totalAvaliacoes(250L)
                .crescimento(BigDecimal.valueOf(15.0))
                .tendencia("ESTÁVEL")
                .build();

        testMercadosEmergentes = Arrays.asList(mercado1);
        testMercadosConsolidados = Arrays.asList(mercado2);
        testMelhorPerformance = Arrays.asList(mercado2, mercado1);

        testAnaliseTendencias = AnaliseTendenciasResponse.builder()
                .dataAnalise(LocalDateTime.now())
                .crescimentoMedio(BigDecimal.valueOf(25.5))
                .mercadosEmAlta(15L)
                .totalMercados(100L)
                .topCrescimento(Arrays.asList(mercado1))
                .topDeclinio(Arrays.asList())
                .build();
    }

    @Test
    @DisplayName("Deve gerar análise de tendências com sucesso")
    void testAnalisarTendencias_Success() {
        // Arrange
        when(tendenciasService.analisarTendencias()).thenReturn(testAnaliseTendencias);

        // Act
        ResponseEntity<AnaliseTendenciasResponse> response = controller.analisarTendencias();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCrescimentoMedio()).isEqualByComparingTo(BigDecimal.valueOf(25.5));
        assertThat(response.getBody().getMercadosEmAlta()).isEqualTo(15L);
        assertThat(response.getBody().getTotalMercados()).isEqualTo(100L);

        verify(tendenciasService, times(1)).analisarTendencias();
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando falhar ao analisar tendências")
    void testAnalisarTendencias_Error() {
        // Arrange
        when(tendenciasService.analisarTendencias())
                .thenThrow(new RuntimeException("Erro ao analisar tendências"));

        // Act
        ResponseEntity<AnaliseTendenciasResponse> response = controller.analisarTendencias();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve identificar mercados emergentes com sucesso")
    void testMercadosEmergentes_Success() {
        // Arrange
        when(tendenciasService.identificarMercadosEmergentes(10)).thenReturn(testMercadosEmergentes);

        // Act
        ResponseEntity<List<TendenciaMercadoResponse>> response = controller.mercadosEmergentes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTendencia()).isEqualTo("ALTA");
        assertThat(response.getBody().get(0).getTotalAvaliacoes()).isLessThan(50);

        verify(tendenciasService, times(1)).identificarMercadosEmergentes(10);
    }

    @Test
    @DisplayName("Deve identificar mercados consolidados com sucesso")
    void testMercadosConsolidados_Success() {
        // Arrange
        when(tendenciasService.identificarMercadosConsolidados(10)).thenReturn(testMercadosConsolidados);

        // Act
        ResponseEntity<List<TendenciaMercadoResponse>> response = controller.mercadosConsolidados(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTotalAvaliacoes()).isGreaterThanOrEqualTo(100);
        assertThat(response.getBody().get(0).getAvaliacaoMedia()).isGreaterThanOrEqualTo(BigDecimal.valueOf(4.3));

        verify(tendenciasService, times(1)).identificarMercadosConsolidados(10);
    }

    @Test
    @DisplayName("Deve gerar ranking de melhor performance com sucesso")
    void testMelhorPerformance_Success() {
        // Arrange
        when(tendenciasService.mercadosMelhorPerformance(15)).thenReturn(testMelhorPerformance);

        // Act
        ResponseEntity<List<TendenciaMercadoResponse>> response = controller.melhorPerformance(15);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        verify(tendenciasService, times(1)).mercadosMelhorPerformance(15);
    }

    @Test
    @DisplayName("Deve calcular crescimento médio com sucesso")
    void testCrescimentoMedio_Success() {
        // Arrange
        List<Mercado> mockMercados = Arrays.asList(new Mercado(), new Mercado());
        when(tendenciasService.obterTodosMercados()).thenReturn(mockMercados);

        // Act
        ResponseEntity<?> response = controller.crescimentoMedio();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(tendenciasService, times(1)).obterTodosMercados();
    }

    @Test
    @DisplayName("Deve retornar erro 500 ao falhar cálculo de crescimento")
    void testCrescimentoMedio_Error() {
        // Arrange
        when(tendenciasService.obterTodosMercados())
                .thenThrow(new RuntimeException("Erro ao calcular"));

        // Act
        ResponseEntity<?> response = controller.crescimentoMedio();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve respeitar limite customizado para emergentes")
    void testMercadosEmergentes_CustomLimit() {
        // Arrange
        when(tendenciasService.identificarMercadosEmergentes(5)).thenReturn(testMercadosEmergentes);

        // Act
        ResponseEntity<List<TendenciaMercadoResponse>> response = controller.mercadosEmergentes(5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(tendenciasService, times(1)).identificarMercadosEmergentes(5);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há emergentes")
    void testMercadosEmergentes_EmptyList() {
        // Arrange
        when(tendenciasService.identificarMercadosEmergentes(10)).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<TendenciaMercadoResponse>> response = controller.mercadosEmergentes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Deve validar tendência ALTA para mercados emergentes")
    void testMercadosEmergentes_TendenciaAlta() {
        // Arrange
        when(tendenciasService.identificarMercadosEmergentes(10)).thenReturn(testMercadosEmergentes);

        // Act
        ResponseEntity<List<TendenciaMercadoResponse>> response = controller.mercadosEmergentes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0).getTendencia()).isIn("ALTA", "ESTÁVEL");
        assertThat(response.getBody().get(0).getCrescimento()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve validar análise de tendências contém datas")
    void testAnalisarTendencias_WithDataAnalise() {
        // Arrange
        when(tendenciasService.analisarTendencias()).thenReturn(testAnaliseTendencias);

        // Act
        ResponseEntity<AnaliseTendenciasResponse> response = controller.analisarTendencias();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDataAnalise()).isNotNull();
        assertThat(response.getBody().getDataAnalise()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
