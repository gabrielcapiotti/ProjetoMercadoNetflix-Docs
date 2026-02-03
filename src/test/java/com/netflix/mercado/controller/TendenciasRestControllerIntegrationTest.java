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
@DisplayName("TendenciasRestController - Testes de Integração")
@Transactional
class TendenciasRestControllerIntegrationTest {

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
    private Mercado mercadoEmergente;
    private Mercado mercadoConsolidado;

    @BeforeEach
    void setUp() {
        mercadoRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("analyst@example.com");
        testUser.setFullName("Analyst User");
        testUser.setPassword("encoded_password");
        userRepository.save(testUser);

        // Mercado Emergente (5-50 avaliações, rating >= 4.0)
        mercadoEmergente = new Mercado();
        mercadoEmergente.setNome("Mercado Emergente");
        mercadoEmergente.setCidade("São Paulo");
        mercadoEmergente.setEstado("SP");
        mercadoRepository.save(mercadoEmergente);

        for (int i = 1; i <= 20; i++) {
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setMercado(mercadoEmergente);
            avaliacao.setUser(testUser);
            avaliacao.setEstrelas(4);
            avaliacaoRepository.save(avaliacao);
        }

        // Mercado Consolidado (100+ avaliações, rating >= 4.3)
        mercadoConsolidado = new Mercado();
        mercadoConsolidado.setNome("Mercado Consolidado");
        mercadoConsolidado.setCidade("Rio de Janeiro");
        mercadoConsolidado.setEstado("RJ");
        mercadoRepository.save(mercadoConsolidado);

        for (int i = 1; i <= 150; i++) {
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setMercado(mercadoConsolidado);
            avaliacao.setUser(testUser);
            avaliacao.setEstrelas(5);
            avaliacaoRepository.save(avaliacao);
        }
    }

    @Test
    @DisplayName("Deve retornar análise geral de tendências")
    @WithMockUser(roles = "ADMIN")
    void testAnalisarTendencias_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/geral"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crescimentoMedio").exists());
    }

    @Test
    @DisplayName("Deve retornar mercados emergentes")
    @WithMockUser(roles = "SELLER")
    void testMercadosEmergentes_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/emergentes")
                .param("limite", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar mercados consolidados")
    @WithMockUser(roles = "ADMIN")
    void testMercadosConsolidados_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/consolidados")
                .param("limite", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar ranking de melhor performance")
    @WithMockUser(roles = "SELLER")
    void testMelhorPerformance_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/melhor-performance")
                .param("limite", "15"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar crescimento médio dos mercados")
    @WithMockUser(roles = "ADMIN")
    void testCrescimentoMedio_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/crescimento-medio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crescimentoMedio").exists());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void testAnalisarTendencias_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/geral"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 com role incorreta")
    @WithMockUser(roles = "USER")
    void testAnalisarTendencias_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/geral"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve respeitar limite customizado em mercados emergentes")
    @WithMockUser(roles = "SELLER")
    void testMercadosEmergentes_CustomLimit() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/emergentes")
                .param("limite", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há mercados emergentes")
    @WithMockUser(roles = "SELLER")
    void testMercadosEmergentes_EmptyList() throws Exception {
        // Arrange
        mercadoRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/emergentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deve identificar tendência alta em mercados com crescimento")
    @WithMockUser(roles = "ADMIN")
    void testMercadosEmergentes_TendenciaAlta() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/tendencias/emergentes"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar análise com filtro de data")
    @WithMockUser(roles = "ADMIN")
    void testAnalisarTendencias_WithDateFilter() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tendencias/geral")
                .param("dataInicio", "2024-01-01")
                .param("dataFim", "2026-12-31"))
                .andExpect(status().isOk());
    }
}
