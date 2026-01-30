package com.netflix.mercado.service;

import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.exception.UnauthorizedException;
import com.netflix.mercado.repository.MercadoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.CreateMercadoRequest;
import com.netflix.mercado.dto.UpdateMercadoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para MercadoService
 * Valida operações de criação, atualização, busca e gerenciamento de mercados
 */
@ExtendWith(MockitoExtension.class)
class MercadoServiceTest {

    @Mock
    private MercadoRepository mercadoRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private MercadoService mercadoService;

    private Mercado testMercado;
    private User testOwner;
    private User testAdmin;
    private CreateMercadoRequest createRequest;
    private UpdateMercadoRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Configurar usuário proprietário
        testOwner = new User();
        testOwner.setId(1L);
        testOwner.setEmail("owner@example.com");
        testOwner.setFullName("Market Owner");

        // Configurar usuário admin
        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setEmail("admin@example.com");
        testAdmin.setFullName("Admin User");

        // Configurar mercado de teste
        testMercado = new Mercado();
        testMercado.setId(1L);
        testMercado.setNome("Mercado Central");
        testMercado.setDescricao("Mercado central da cidade");
        testMercado.setCidade("São Paulo");
        testMercado.setBairro("Centro");
        testMercado.setRua("Rua Principal");
        testMercado.setNumero("123");
        testMercado.setComplemento("Apto 1");
        testMercado.setCep("01310-100");
        testMercado.setLatitude(-23.550520);
        testMercado.setLongitude(-46.633308);
        testMercado.setTelefone("1133334444");
        testMercado.setEmail("mercado@example.com");
        testMercado.setOwner(testOwner);
        testMercado.setAprovado(false);
        testMercado.setAvaliacaoMedia(0.0);
        testMercado.setTotalAvaliacoes(0L);

        // Configurar request de criação
        createRequest = new CreateMercadoRequest();
        createRequest.setNome("Mercado Central");
        createRequest.setDescricao("Mercado central da cidade");
        createRequest.setCidade("São Paulo");
        createRequest.setBairro("Centro");
        createRequest.setRua("Rua Principal");
        createRequest.setNumero("123");
        createRequest.setComplemento("Apto 1");
        createRequest.setCep("01310-100");
        createRequest.setLatitude(-23.550520);
        createRequest.setLongitude(-46.633308);
        createRequest.setTelefone("1133334444");
        createRequest.setEmail("mercado@example.com");

        // Configurar request de atualização
        updateRequest = new UpdateMercadoRequest();
        updateRequest.setNome("Mercado Central Atualizado");
        updateRequest.setDescricao("Descrição atualizada");
        updateRequest.setCidade("Rio de Janeiro");
        updateRequest.setTelefone("2133334444");
    }

    /**
     * Teste: Criar mercado com sucesso
     */
    @Test
    void testCreateMercadoSuccess() {
        // Arrange
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Mercado createdMercado = mercadoService.createMercado(createRequest, testOwner);

        // Assert
        assertThat(createdMercado).isNotNull();
        assertThat(createdMercado.getNome()).isEqualTo("Mercado Central");
        assertThat(createdMercado.getCidade()).isEqualTo("São Paulo");
        assertThat(createdMercado.isAprovado()).isFalse();
        verify(mercadoRepository, times(1)).save(any(Mercado.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Validar erro ao criar mercado com CNPJ duplicado
     */
    @Test
    void testCreateMercadoCNPJDuplicate() {
        // Arrange
        createRequest.setCnpj("12345678000100");
        when(mercadoRepository.existsByCnpj("12345678000100")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> mercadoService.createMercado(createRequest, testOwner))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("CNPJ");

        verify(mercadoRepository, never()).save(any(Mercado.class));
    }

    /**
     * Teste: Atualizar mercado com sucesso
     */
    @Test
    void testUpdateMercadoSuccess() {
        // Arrange
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Mercado updatedMercado = mercadoService.updateMercado(1L, updateRequest, testOwner);

        // Assert
        assertThat(updatedMercado).isNotNull();
        assertThat(updatedMercado.getNome()).isEqualTo("Mercado Central Atualizado");
        verify(mercadoRepository, times(1)).save(any(Mercado.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Soft delete de mercado
     */
    @Test
    void testDeleteMercadoSoftDelete() {
        // Arrange
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        mercadoService.deleteMercado(1L, testOwner);

        // Assert
        verify(mercadoRepository, times(1)).save(any(Mercado.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Buscar mercado por ID
     */
    @Test
    void testFindMercadoById() {
        // Arrange
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));

        // Act
        Mercado foundMercado = mercadoService.getMercadoById(1L);

        // Assert
        assertThat(foundMercado).isNotNull();
        assertThat(foundMercado.getId()).isEqualTo(1L);
        assertThat(foundMercado.getNome()).isEqualTo("Mercado Central");
        verify(mercadoRepository, times(1)).findById(1L);
    }

    /**
     * Teste: Buscar mercados por proximidade com sucesso
     */
    @Test
    void testBuscarPorProximidade() {
        // Arrange
        double latitude = -23.550520;
        double longitude = -46.633308;
        double raioKm = 5.0;

        List<Mercado> mercados = List.of(testMercado);
        when(mercadoRepository.buscarPorProximidade(latitude, longitude, raioKm))
                .thenReturn(mercados);

        // Act
        List<Mercado> resultado = mercadoService.buscarPorProximidade(latitude, longitude, raioKm);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Mercado Central");
        verify(mercadoRepository, times(1)).buscarPorProximidade(latitude, longitude, raioKm);
    }

    /**
     * Teste: Buscar mercados por proximidade sem resultados
     */
    @Test
    void testBuscarPorProximidadeNoResults() {
        // Arrange
        double latitude = -23.550520;
        double longitude = -46.633308;
        double raioKm = 5.0;

        when(mercadoRepository.buscarPorProximidade(latitude, longitude, raioKm))
                .thenReturn(List.of());

        // Act
        List<Mercado> resultado = mercadoService.buscarPorProximidade(latitude, longitude, raioKm);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(mercadoRepository, times(1)).buscarPorProximidade(latitude, longitude, raioKm);
    }

    /**
     * Teste: Buscar mercados por nome
     */
    @Test
    void testBuscarPorNome() {
        // Arrange
        String nome = "Mercado";
        List<Mercado> mercados = List.of(testMercado);
        when(mercadoRepository.findByNomeContainingIgnoreCase(nome))
                .thenReturn(mercados);

        // Act
        List<Mercado> resultado = mercadoService.buscarPorNome(nome);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).contains("Mercado");
        verify(mercadoRepository, times(1)).findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Teste: Aprovar mercado como admin
     */
    @Test
    void testAprovarMercadoAdmin() {
        // Arrange
        testAdmin.setRoles(java.util.Set.of(
                new com.netflix.mercado.entity.Role(1L, "ROLE_ADMIN")
        ));
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
        when(notificacaoService.notificarAprovacao(any(Mercado.class))).thenReturn(true);

        // Act
        Mercado approvedMercado = mercadoService.aprovarMercado(1L, testAdmin);

        // Assert
        assertThat(approvedMercado).isNotNull();
        assertThat(approvedMercado.isAprovado()).isTrue();
        verify(mercadoRepository, times(1)).save(any(Mercado.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    /**
     * Teste: Atualizar avaliação média do mercado
     */
    @Test
    void testAtualizarAvaliacaoMedia() {
        // Arrange
        double novaAvaliacao = 4.5;
        long totalAvaliacoes = 10L;

        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Mercado updatedMercado = mercadoService.atualizarAvaliacaoMedia(1L, novaAvaliacao, totalAvaliacoes);

        // Assert
        assertThat(updatedMercado).isNotNull();
        assertThat(updatedMercado.getAvaliacaoMedia()).isEqualTo(novaAvaliacao);
        assertThat(updatedMercado.getTotalAvaliacoes()).isEqualTo(totalAvaliacoes);
        verify(mercadoRepository, times(1)).save(any(Mercado.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

}
