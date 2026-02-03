package com.netflix.mercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ValidacaoRestController - Testes de Integração")
@Transactional
class ValidacaoRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve validar email válido via HTTP")
    @WithMockUser(roles = "ADMIN")
    void testValidarEmail_Valid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/email")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    @DisplayName("Deve rejeitar email inválido")
    @WithMockUser(roles = "ADMIN")
    void testValidarEmail_Invalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/email")
                .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("Deve rejeitar email muito longo")
    @WithMockUser(roles = "ADMIN")
    void testValidarEmail_TooLong() throws Exception {
        // Arrange
        String longEmail = "a".repeat(300) + "@example.com";

        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/email")
                .param("email", longEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("Deve validar diferentes formatos de email")
    @WithMockUser(roles = "ADMIN")
    void testValidarEmail_DifferentFormats() throws Exception {
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "123@example.com"
        };

        for (String email : validEmails) {
            mockMvc.perform(post("/api/v1/validacao/email")
                    .param("email", email))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Deve validar URL válida via HTTP")
    @WithMockUser(roles = "ADMIN")
    void testValidarUrl_Valid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/url")
                .param("url", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    @DisplayName("Deve rejeitar URL inválida")
    @WithMockUser(roles = "ADMIN")
    void testValidarUrl_Invalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/url")
                .param("url", "not-a-url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("Deve rejeitar URL muito longa")
    @WithMockUser(roles = "ADMIN")
    void testValidarUrl_TooLong() throws Exception {
        // Arrange
        String longUrl = "https://example.com/" + "a".repeat(2000);

        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/url")
                .param("url", longUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    @DisplayName("Deve validar URLs com diferentes protocolos")
    @WithMockUser(roles = "ADMIN")
    void testValidarUrl_DifferentProtocols() throws Exception {
        String[] validUrls = {
            "https://example.com",
            "http://example.com",
            "https://sub.example.com",
            "https://example.com:8080/path"
        };

        for (String url : validUrls) {
            mockMvc.perform(post("/api/v1/validacao/url")
                    .param("url", url))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Deve sanitizar texto removendo caracteres perigosos")
    @WithMockUser(roles = "USER")
    void testSanitizar_RemoveDangerousChars() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/validacao/sanitizar")
                .param("texto", "<script>alert('xss')</script>"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foiAlterado").value(true))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("sanitizado");
    }

    @Test
    @DisplayName("Deve não alterar texto seguro na sanitização")
    @WithMockUser(roles = "USER")
    void testSanitizar_NoChanges() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/sanitizar")
                .param("texto", "Texto seguro e normal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foiAlterado").value(false));
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação para email")
    void testValidarEmail_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/email")
                .param("email", "test@example.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação para URL")
    void testValidarUrl_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/url")
                .param("url", "https://example.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação para sanitização")
    void testSanitizar_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/sanitizar")
                .param("texto", "algum texto"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar email com role SELLER")
    @WithMockUser(roles = "SELLER")
    void testValidarEmail_WithSellerRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/email")
                .param("email", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve validar URL com role USER")
    @WithMockUser(roles = "USER")
    void testValidarUrl_WithUserRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/url")
                .param("url", "https://example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve sanitizar com diferentes roles")
    @WithMockUser(roles = "ADMIN")
    void testSanitizar_WithAdminRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/sanitizar")
                .param("texto", "texto de teste"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 quando email está vazio")
    @WithMockUser(roles = "ADMIN")
    void testValidarEmail_Empty() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/email")
                .param("email", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando URL está vazia")
    @WithMockUser(roles = "ADMIN")
    void testValidarUrl_Empty() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/url")
                .param("url", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando texto está vazio para sanitização")
    @WithMockUser(roles = "USER")
    void testSanitizar_Empty() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/validacao/sanitizar")
                .param("texto", ""))
                .andExpect(status().isBadRequest());
    }
}
