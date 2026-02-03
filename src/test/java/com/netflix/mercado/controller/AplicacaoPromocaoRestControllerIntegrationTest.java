package com.netflix.mercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.mercado.dto.promocao.AplicarPromocaoRequest;
import com.netflix.mercado.entity.Promocao;
import com.netflix.mercado.repository.PromocaoRepository;
import com.netflix.mercado.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AplicacaoPromocaoRestController - Testes de Integração")
@Transactional
class AplicacaoPromocaoRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private UserRepository userRepository;

    private Promocao testPromocao;
    private AplicarPromocaoRequest testRequest;

    @BeforeEach
    void setUp() {
        promocaoRepository.deleteAll();

        testPromocao = new Promocao();
        testPromocao.setCodigo("PROMO10");
        testPromocao.setPercentualDesconto(BigDecimal.TEN);
        testPromocao.setDataValidade(LocalDateTime.now().plusDays(30));
        testPromocao.setAtiva(true);
        testPromocao.setMaxUtilizacoes(100L);
        testPromocao.setUtilizacoesAtuais(0L);
        promocaoRepository.save(testPromocao);

        testRequest = new AplicarPromocaoRequest();
        testRequest.setCodigoPromocao("PROMO10");
        testRequest.setValorCompra(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Deve aplicar promoção com sucesso via HTTP")
    @WithMockUser(roles = "USER")
    void testAplicarPromocao_Success() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/promocoes/aplicacao/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPromocao").value("PROMO10"))
                .andExpect(jsonPath("$.percentualDesconto").value(10))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("PROMO10");
    }

    @Test
    @DisplayName("Deve retornar 400 quando código de promoção está vazio")
    @WithMockUser(roles = "USER")
    void testAplicarPromocao_EmptyCode() throws Exception {
        // Arrange
        AplicarPromocaoRequest invalidRequest = new AplicarPromocaoRequest();
        invalidRequest.setCodigoPromocao("");
        invalidRequest.setValorCompra(BigDecimal.valueOf(100.00));

        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void testAplicarPromocao_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar promoção válida via HTTP")
    @WithMockUser(roles = "USER")
    void testValidarPromocao_Valid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/validar/PROMO10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    @DisplayName("Deve retornar inválido para promoção inexistente")
    @WithMockUser(roles = "USER")
    void testValidarPromocao_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/validar/INVALIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("Deve retornar inválido para promoção expirada")
    @WithMockUser(roles = "USER")
    void testValidarPromocao_Expired() throws Exception {
        // Arrange
        Promocao promocaoExpirada = new Promocao();
        promocaoExpirada.setCodigo("EXPIRED");
        promocaoExpirada.setPercentualDesconto(BigDecimal.TEN);
        promocaoExpirada.setDataValidade(LocalDateTime.now().minusDays(1));
        promocaoExpirada.setAtiva(false);
        promocaoRepository.save(promocaoExpirada);

        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/validar/EXPIRED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("Deve retornar 403 com role incorreta")
    @WithMockUser(roles = "ADMIN")
    void testAplicarPromocao_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve validar tipo de conteúdo JSON")
    @WithMockUser(roles = "USER")
    void testAplicarPromocao_InvalidContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/aplicar")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve aplicar promoção com valor alto")
    @WithMockUser(roles = "CUSTOMER")
    void testAplicarPromocao_HighValue() throws Exception {
        // Arrange
        AplicarPromocaoRequest highValueRequest = new AplicarPromocaoRequest();
        highValueRequest.setCodigoPromocao("PROMO10");
        highValueRequest.setValorCompra(BigDecimal.valueOf(5000.00));

        // Act & Assert
        mockMvc.perform(post("/api/v1/promocoes/aplicacao/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(highValueRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorOriginal").value(5000.00))
                .andExpect(jsonPath("$.desconto").value(500.00));
    }
}
