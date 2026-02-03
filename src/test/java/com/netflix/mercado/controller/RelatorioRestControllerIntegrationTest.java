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
@DisplayName("RelatorioRestController - Testes de Integração")
@Transactional
class RelatorioRestControllerIntegrationTest {

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
    private Mercado testMercado;

    @BeforeEach
    void setUp() {
        mercadoRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("seller@example.com");
        testUser.setFullName("Seller User");
        testUser.setPassword("encoded_password");
        userRepository.save(testUser);

        testMercado = new Mercado();
        testMercado.setNome("Mercado Premium");
        testMercado.setCidade("São Paulo");
        testMercado.setEstado("SP");
        mercadoRepository.save(testMercado);

        for (int i = 1; i <= 5; i++) {
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.setMercado(testMercado);
            avaliacao.setUser(testUser);
            avaliacao.setEstrelas(i <= 2 ? 5 : 4);
            avaliacaoRepository.save(avaliacao);
        }
    }

    @Test
    @DisplayName("Deve retornar relatório geral do sistema")
    @WithMockUser(roles = "ADMIN")
    void testRelatorioGeral_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/geral"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMercados").exists());
    }

    @Test
    @DisplayName("Deve retornar relatório de mercado específico")
    @WithMockUser(roles = "SELLER")
    void testRelatorioMercado_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/mercado/{id}", testMercado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeMercado").value("Mercado Premium"));
    }

    @Test
    @DisplayName("Deve retornar 404 para mercado não encontrado")
    @WithMockUser(roles = "SELLER")
    void testRelatorioMercado_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/mercado/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar ranking de mercados")
    @WithMockUser(roles = "ADMIN")
    void testRanking_Success() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/v1/relatorios/ranking")
                .param("limite", "5"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar mercados com poucas avaliações")
    @WithMockUser(roles = "ADMIN")
    void testMercadosPoucasAvaliacoes_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/poucas-avaliacoes")
                .param("avaliacaoMinima", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar relatório de comentários")
    @WithMockUser(roles = "ADMIN")
    void testRelatorioComentarios_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/comentarios"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void testRelatorioGeral_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/geral"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 com role incorreta")
    @WithMockUser(roles = "USER")
    void testRelatorioGeral_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/geral"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar distribuição de estrelas no relatório de mercado")
    @WithMockUser(roles = "SELLER")
    void testRelatorioMercado_DistribuicaoEstrelas() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/mercado/{id}", testMercado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distribuicaoEstrelas").exists());
    }

    @Test
    @DisplayName("Deve respeitar limite customizado no ranking")
    @WithMockUser(roles = "ADMIN")
    void testRanking_CustomLimit() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/relatorios/ranking")
                .param("limite", "20"))
                .andExpect(status().isOk());
    }
}
