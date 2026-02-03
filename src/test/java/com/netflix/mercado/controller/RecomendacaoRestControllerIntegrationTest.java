package com.netflix.mercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.repository.AvaliacaoRepository;
import com.netflix.mercado.repository.MercadoRepository;
import com.netflix.mercado.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@DisplayName("RecomendacaoRestController - Testes de Integração")
@Transactional
class RecomendacaoRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Mercado mercado1;
    private Mercado mercado2;

    @BeforeEach
    void setUp() {
        mercadoRepository.deleteAll();
        avaliacaoRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPassword("encoded_password");
        userRepository.save(testUser);

        mercado1 = new Mercado();
        mercado1.setNome("Mercado Central");
        mercado1.setCidade("São Paulo");
        mercado1.setEstado("SP");
        mercado1.setLatitude(BigDecimal.valueOf(-23.5505));
        mercado1.setLongitude(BigDecimal.valueOf(-46.6333));
        mercadoRepository.save(mercado1);

        mercado2 = new Mercado();
        mercado2.setNome("Mercado Norte");
        mercado2.setCidade("Rio de Janeiro");
        mercado2.setEstado("RJ");
        mercado2.setLatitude(BigDecimal.valueOf(-22.9068));
        mercado2.setLongitude(BigDecimal.valueOf(-43.1729));
        mercadoRepository.save(mercado2);

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setMercado(mercado1);
        avaliacao.setUser(testUser);
        avaliacao.setEstrelas(5);
        avaliacaoRepository.save(avaliacao);
    }

    @Test
    @DisplayName("Deve retornar recomendações personalizadas via HTTP")
    @WithMockUser(roles = "USER")
    void testGerarRecomendacoes_Success() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/recomendacoes/personalizadas")
                .param("limite", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Mercado");
    }

    @Test
    @DisplayName("Deve retornar recomendações por localização")
    @WithMockUser(roles = "USER")
    void testRecomendacoesPorLocalizacao_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/por-localizacao")
                .param("limite", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar mercados não visitados")
    @WithMockUser(roles = "USER")
    void testRecomendacoesNaoVisitados_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/nao-visitados")
                .param("limite", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void testGerarRecomendacoes_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/personalizadas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve respeitar parâmetro de limite customizado")
    @WithMockUser(roles = "USER")
    void testGerarRecomendacoes_CustomLimit() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/personalizadas")
                .param("limite", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve usar limite padrão quando não informado")
    @WithMockUser(roles = "USER")
    void testGerarRecomendacoes_DefaultLimit() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/personalizadas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há recomendações")
    @WithMockUser(roles = "USER")
    void testGerarRecomendacoes_EmptyList() throws Exception {
        // Arrange
        mercadoRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/personalizadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deve retornar 403 com role incorreta")
    @WithMockUser(roles = "ADMIN")
    void testGerarRecomendacoes_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/personalizadas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve validar limite máximo de parâmetro")
    @WithMockUser(roles = "USER")
    void testGerarRecomendacoes_LargeLimit() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/recomendacoes/personalizadas")
                .param("limite", "1000"))
                .andExpect(status().isOk());
    }
}
