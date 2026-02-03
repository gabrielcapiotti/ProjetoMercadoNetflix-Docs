package com.netflix.mercado.controller;

import com.netflix.mercado.dto.recomendacao.MercadoRecomendacaoResponse;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.RecomendacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecomendacaoRestController - Testes Unitários")
class RecomendacaoRestControllerTest {

    @Mock
    private RecomendacaoService recomendacaoService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private RecomendacaoRestController controller;

    private User testUser;
    private List<MercadoRecomendacaoResponse> testRecomendacoes;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setFullName("Test User");

        MercadoResponse mercado1 = new MercadoResponse();
        mercado1.setId(1L);
        mercado1.setNome("Mercado Premium");
        mercado1.setCidade("São Paulo");
        mercado1.setEstado("SP");
        mercado1.setAvaliacaoMedia(BigDecimal.valueOf(4.8));

        MercadoResponse mercado2 = new MercadoResponse();
        mercado2.setId(2L);
        mercado2.setNome("Supermercado Econômico");
        mercado2.setCidade("Campinas");
        mercado2.setEstado("SP");
        mercado2.setAvaliacaoMedia(BigDecimal.valueOf(4.5));

        testRecomendacoes = Arrays.asList(
                MercadoRecomendacaoResponse.builder()
                        .mercado(mercado1)
                        .pontuacao(95.0)
                        .motivo("Excelente avaliação e localização próxima")
                        .build(),
                MercadoRecomendacaoResponse.builder()
                        .mercado(mercado2)
                        .pontuacao(88.0)
                        .motivo("Boas avaliações e mesmo estado")
                        .build()
        );

        // Mock Security Context
        when(userPrincipal.getUser()).thenReturn(testUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve gerar recomendações personalizadas com sucesso")
    void testGerarRecomendacoes_Success() {
        // Arrange
        when(recomendacaoService.gerarRecomendacoes(any(User.class), eq(10)))
                .thenReturn(testRecomendacoes);

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.gerarRecomendacoes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getPontuacao()).isEqualTo(95.0);
        assertThat(response.getBody().get(0).getMercado().getNome()).isEqualTo("Mercado Premium");

        verify(recomendacaoService, times(1)).gerarRecomendacoes(any(User.class), eq(10));
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer exceção")
    void testGerarRecomendacoes_Error() {
        // Arrange
        when(recomendacaoService.gerarRecomendacoes(any(User.class), anyInt()))
                .thenThrow(new RuntimeException("Erro ao gerar recomendações"));

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.gerarRecomendacoes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve gerar recomendações por localização com sucesso")
    void testRecomendacoesPorLocalizacao_Success() {
        // Arrange
        when(recomendacaoService.recomendacoesPorLocalizacao(any(User.class), eq(10)))
                .thenReturn(testRecomendacoes);

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.recomendacoesPorLocalizacao(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getMercado().getEstado()).isEqualTo("SP");

        verify(recomendacaoService, times(1)).recomendacoesPorLocalizacao(any(User.class), eq(10));
    }

    @Test
    @DisplayName("Deve gerar recomendações de não visitados com sucesso")
    void testRecomendacoesNaoVisitados_Success() {
        // Arrange
        when(recomendacaoService.recomendacoesNaoVisitados(any(User.class), eq(10)))
                .thenReturn(testRecomendacoes);

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.recomendacoesNaoVisitados(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        verify(recomendacaoService, times(1)).recomendacoesNaoVisitados(any(User.class), eq(10));
    }

    @Test
    @DisplayName("Deve respeitar o limite de recomendações")
    void testGerarRecomendacoes_WithLimit() {
        // Arrange
        when(recomendacaoService.gerarRecomendacoes(any(User.class), eq(5)))
                .thenReturn(testRecomendacoes.subList(0, 1));

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.gerarRecomendacoes(5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);

        verify(recomendacaoService, times(1)).gerarRecomendacoes(any(User.class), eq(5));
    }

    @Test
    @DisplayName("Deve usar limite padrão quando não especificado")
    void testGerarRecomendacoes_DefaultLimit() {
        // Arrange
        when(recomendacaoService.gerarRecomendacoes(any(User.class), eq(10)))
                .thenReturn(testRecomendacoes);

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.gerarRecomendacoes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(recomendacaoService, times(1)).gerarRecomendacoes(any(User.class), eq(10));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há recomendações")
    void testGerarRecomendacoes_EmptyList() {
        // Arrange
        when(recomendacaoService.gerarRecomendacoes(any(User.class), anyInt()))
                .thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.gerarRecomendacoes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Deve ordenar recomendações por pontuação")
    void testGerarRecomendacoes_OrderedByScore() {
        // Arrange
        when(recomendacaoService.gerarRecomendacoes(any(User.class), anyInt()))
                .thenReturn(testRecomendacoes);

        // Act
        ResponseEntity<List<MercadoRecomendacaoResponse>> response = controller.gerarRecomendacoes(10);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get(0).getPontuacao())
                .isGreaterThan(response.getBody().get(1).getPontuacao());
    }
}
