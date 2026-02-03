package com.netflix.mercado.controller;

import com.netflix.mercado.service.DataIntegrityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidacaoRestController - Testes Unitários")
class ValidacaoRestControllerTest {

    @Mock
    private DataIntegrityService dataIntegrityService;

    @InjectMocks
    private ValidacaoRestController controller;

    @BeforeEach
    void setUp() {
        // Setup padrão se necessário
    }

    @Test
    @DisplayName("Deve validar email válido com sucesso")
    void testValidarEmail_Valid() {
        // Arrange
        String validEmail = "usuario@exemplo.com";
        doNothing().when(dataIntegrityService).validarEmail(validEmail);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarEmail(validEmail);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isTrue();
        assertThat(response.getBody().getMensagem()).isEqualTo("Email válido");

        verify(dataIntegrityService, times(1)).validarEmail(validEmail);
    }

    @Test
    @DisplayName("Deve rejeitar email inválido")
    void testValidarEmail_Invalid() {
        // Arrange
        String invalidEmail = "email-invalido";
        doThrow(new IllegalArgumentException("Email inválido"))
                .when(dataIntegrityService).validarEmail(invalidEmail);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarEmail(invalidEmail);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isFalse();
        assertThat(response.getBody().getMensagem()).contains("inválido");

        verify(dataIntegrityService, times(1)).validarEmail(invalidEmail);
    }

    @Test
    @DisplayName("Deve rejeitar email muito longo")
    void testValidarEmail_TooLong() {
        // Arrange
        String longEmail = "a".repeat(160) + "@test.com";
        doThrow(new IllegalArgumentException("Email muito longo"))
                .when(dataIntegrityService).validarEmail(longEmail);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarEmail(longEmail);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isFalse();

        verify(dataIntegrityService, times(1)).validarEmail(longEmail);
    }

    @Test
    @DisplayName("Deve validar URL válida com sucesso")
    void testValidarUrl_Valid() {
        // Arrange
        String validUrl = "https://www.exemplo.com";
        doNothing().when(dataIntegrityService).validarURL(validUrl);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarUrl(validUrl);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isTrue();
        assertThat(response.getBody().getMensagem()).isEqualTo("URL válida");

        verify(dataIntegrityService, times(1)).validarURL(validUrl);
    }

    @Test
    @DisplayName("Deve rejeitar URL inválida")
    void testValidarUrl_Invalid() {
        // Arrange
        String invalidUrl = "url-invalida-sem-protocolo";
        doThrow(new IllegalArgumentException("URL inválida"))
                .when(dataIntegrityService).validarURL(invalidUrl);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarUrl(invalidUrl);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isFalse();
        assertThat(response.getBody().getMensagem()).contains("inválida");

        verify(dataIntegrityService, times(1)).validarURL(invalidUrl);
    }

    @Test
    @DisplayName("Deve rejeitar URL muito longa")
    void testValidarUrl_TooLong() {
        // Arrange
        String longUrl = "https://exemplo.com/" + "a".repeat(600);
        doThrow(new IllegalArgumentException("URL muito longa"))
                .when(dataIntegrityService).validarURL(longUrl);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarUrl(longUrl);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isFalse();

        verify(dataIntegrityService, times(1)).validarURL(longUrl);
    }

    @Test
    @DisplayName("Deve sanitizar texto removendo caracteres perigosos")
    void testSanitizar_RemovesDangerousChars() {
        // Arrange
        String textoOriginal = "<script>alert('XSS')</script>";
        String textoSanitizado = "scriptalert('XSS')script";
        when(dataIntegrityService.sanitizarString(textoOriginal)).thenReturn(textoSanitizado);

        // Act
        ResponseEntity<ValidacaoRestController.SanitizacaoResponse> response = 
                controller.sanitizar(textoOriginal);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOriginal()).isEqualTo(textoOriginal);
        assertThat(response.getBody().getSanitizado()).isEqualTo(textoSanitizado);
        assertThat(response.getBody().isFoiAlterado()).isFalse();

        verify(dataIntegrityService, times(1)).sanitizarString(textoOriginal);
    }

    @Test
    @DisplayName("Deve sanitizar sem alterações quando texto seguro")
    void testSanitizar_NoChanges() {
        // Arrange
        String textoSeguro = "Texto normal sem caracteres perigosos";
        when(dataIntegrityService.sanitizarString(textoSeguro)).thenReturn(textoSeguro);

        // Act
        ResponseEntity<ValidacaoRestController.SanitizacaoResponse> response = 
                controller.sanitizar(textoSeguro);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOriginal()).isEqualTo(textoSeguro);
        assertThat(response.getBody().getSanitizado()).isEqualTo(textoSeguro);
        assertThat(response.getBody().isFoiAlterado()).isTrue(); // Mesmo texto = não alterado

        verify(dataIntegrityService, times(1)).sanitizarString(textoSeguro);
    }

    @Test
    @DisplayName("Deve retornar inválido quando validação de email lança exceção")
    void testValidarEmail_InternalError() {
        // Arrange
        String email = "test@example.com";
        doThrow(new RuntimeException("Erro inesperado"))
                .when(dataIntegrityService).validarEmail(email);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarEmail(email);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isFalse();
        assertThat(response.getBody().getMensagem()).contains("inválido");
    }

    @Test
    @DisplayName("Deve retornar inválido quando validação de URL lança exceção")
    void testValidarUrl_InternalError() {
        // Arrange
        String url = "https://example.com";
        doThrow(new RuntimeException("Erro inesperado"))
                .when(dataIntegrityService).validarURL(url);

        // Act
        ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                controller.validarUrl(url);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando sanitização falha")
    void testSanitizar_InternalError() {
        // Arrange
        String texto = "Texto qualquer";
        when(dataIntegrityService.sanitizarString(texto))
                .thenThrow(new RuntimeException("Erro inesperado"));

        // Act
        ResponseEntity<ValidacaoRestController.SanitizacaoResponse> response = 
                controller.sanitizar(texto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Deve validar emails com diferentes formatos válidos")
    void testValidarEmail_DifferentFormats() {
        // Arrange
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user123@subdomain.example.com"
        };

        for (String email : validEmails) {
            doNothing().when(dataIntegrityService).validarEmail(email);

            // Act
            ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                    controller.validarEmail(email);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().isValido()).isTrue();
        }

        verify(dataIntegrityService, times(validEmails.length)).validarEmail(anyString());
    }

    @Test
    @DisplayName("Deve validar URLs com diferentes protocolos")
    void testValidarUrl_DifferentProtocols() {
        // Arrange
        String[] validUrls = {
            "http://example.com",
            "https://example.com",
            "https://www.example.com/path",
            "https://subdomain.example.com:8080/path"
        };

        for (String url : validUrls) {
            doNothing().when(dataIntegrityService).validarURL(url);

            // Act
            ResponseEntity<ValidacaoRestController.ValidacaoResponse> response = 
                    controller.validarUrl(url);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().isValido()).isTrue();
        }

        verify(dataIntegrityService, times(validUrls.length)).validarURL(anyString());
    }
}
