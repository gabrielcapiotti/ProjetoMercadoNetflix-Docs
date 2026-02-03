package com.netflix.mercado.controller;

import com.netflix.mercado.dto.promocao.AplicarPromocaoRequest;
import com.netflix.mercado.dto.promocao.AplicarPromocaoResponse;
import com.netflix.mercado.entity.Promocao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.repository.PromocaoRepository;
import com.netflix.mercado.security.UserPrincipal;
import com.netflix.mercado.service.AplicacaoPromocaoService;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AplicacaoPromocaoRestController - Testes Unitários")
class AplicacaoPromocaoRestControllerTest {

    @Mock
    private AplicacaoPromocaoService aplicacaoPromocaoService;

    @Mock
    private PromocaoRepository promocaoRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private AplicacaoPromocaoRestController controller;

    private User testUser;
    private Promocao testPromocao;
    private AplicarPromocaoRequest testRequest;
    private AplicarPromocaoResponse testResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setFullName("Test User");

        testPromocao = new Promocao();
        testPromocao.setId(1L);
        testPromocao.setCodigo("PROMO10");
        testPromocao.setPercentualDesconto(BigDecimal.TEN);
        testPromocao.setDataValidade(LocalDateTime.now().plusDays(30));
        testPromocao.setAtiva(true);

        testRequest = new AplicarPromocaoRequest();
        testRequest.setCodigoPromocao("PROMO10");
        testRequest.setValorCompra(BigDecimal.valueOf(100.00));

        testResponse = AplicarPromocaoResponse.builder()
                .promocaoId(1L)
                .codigoPromocao("PROMO10")
                .valorOriginal(BigDecimal.valueOf(100.00))
                .desconto(BigDecimal.TEN)
                .percentualDesconto(BigDecimal.TEN)
                .valorFinal(BigDecimal.valueOf(90.00))
                .economia(BigDecimal.TEN)
                .dataExpiracao(LocalDateTime.now().plusDays(30))
                .utilizacaoRestante(5L)
                .build();
    }

    private void setupSecurityContext() {
        when(userPrincipal.getUser()).thenReturn(testUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve aplicar promoção com sucesso")
    void testAplicarPromocao_Success() {
        // Arrange
        setupSecurityContext();
        when(aplicacaoPromocaoService.aplicarPromocao(any(AplicarPromocaoRequest.class), any(User.class)))
                .thenReturn(testResponse);

        // Act
        ResponseEntity<AplicarPromocaoResponse> response = controller.aplicarPromocao(testRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCodigoPromocao()).isEqualTo("PROMO10");
        assertThat(response.getBody().getValorFinal()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(response.getBody().getDesconto()).isEqualByComparingTo(BigDecimal.TEN);

        verify(aplicacaoPromocaoService, times(1)).aplicarPromocao(any(AplicarPromocaoRequest.class), any(User.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando dados inválidos")
    void testAplicarPromocao_InvalidData() {
        // Arrange
        setupSecurityContext();
        when(aplicacaoPromocaoService.aplicarPromocao(any(AplicarPromocaoRequest.class), any(User.class)))
                .thenThrow(new IllegalArgumentException("Código da promoção é obrigatório"));

        // Act
        ResponseEntity<AplicarPromocaoResponse> response = controller.aplicarPromocao(testRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer exceção inesperada")
    void testAplicarPromocao_InternalError() {
        // Arrange
        setupSecurityContext();
        when(aplicacaoPromocaoService.aplicarPromocao(any(AplicarPromocaoRequest.class), any(User.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));

        // Act
        ResponseEntity<AplicarPromocaoResponse> response = controller.aplicarPromocao(testRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve validar promoção válida com sucesso")
    void testValidarPromocao_Valid() {
        // Arrange
        when(promocaoRepository.findByCodigo("PROMO10")).thenReturn(Optional.of(testPromocao));

        // Act
        ResponseEntity<?> response = controller.validarPromocao("PROMO10");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(promocaoRepository, times(1)).findByCodigo("PROMO10");
    }

    @Test
    @DisplayName("Deve retornar inválido quando promoção não encontrada")
    void testValidarPromocao_NotFound() {
        // Arrange
        when(promocaoRepository.findByCodigo("INVALIDA")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = controller.validarPromocao("INVALIDA");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(promocaoRepository, times(1)).findByCodigo("INVALIDA");
        verifyNoInteractions(aplicacaoPromocaoService);
    }

    @Test
    @DisplayName("Deve retornar inválido quando promoção expirada")
    void testValidarPromocao_Expired() {
        // Arrange
        Promocao promocaoExpirada = new Promocao();
        promocaoExpirada.setId(1L);
        promocaoExpirada.setCodigo("PROMO10");
        promocaoExpirada.setDataValidade(LocalDateTime.now().minusDays(1)); // Expirada
        promocaoExpirada.setAtiva(false);

        when(promocaoRepository.findByCodigo("PROMO10")).thenReturn(Optional.of(promocaoExpirada));

        // Act
        ResponseEntity<?> response = controller.validarPromocao("PROMO10");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(promocaoRepository, times(1)).findByCodigo("PROMO10");
    }

    @Test
    @DisplayName("Deve aplicar desconto com valor alto")
    void testAplicarPromocao_HighValue() {
        // Arrange
        setupSecurityContext();
        AplicarPromocaoRequest highValueRequest = new AplicarPromocaoRequest();
        highValueRequest.setCodigoPromocao("PROMO10");
        highValueRequest.setValorCompra(BigDecimal.valueOf(1000.00));

        AplicarPromocaoResponse highValueResponse = AplicarPromocaoResponse.builder()
                .promocaoId(1L)
                .codigoPromocao("PROMO10")
                .valorOriginal(BigDecimal.valueOf(1000.00))
                .desconto(BigDecimal.valueOf(100.00))
                .percentualDesconto(BigDecimal.TEN)
                .valorFinal(BigDecimal.valueOf(900.00))
                .economia(BigDecimal.TEN)
                .dataExpiracao(LocalDateTime.now().plusDays(30))
                .utilizacaoRestante(5L)
                .build();

        when(aplicacaoPromocaoService.aplicarPromocao(any(AplicarPromocaoRequest.class), any(User.class)))
                .thenReturn(highValueResponse);

        // Act
        ResponseEntity<AplicarPromocaoResponse> response = controller.aplicarPromocao(highValueRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDesconto()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(response.getBody().getValorFinal()).isEqualByComparingTo(BigDecimal.valueOf(900.00));
    }
}
