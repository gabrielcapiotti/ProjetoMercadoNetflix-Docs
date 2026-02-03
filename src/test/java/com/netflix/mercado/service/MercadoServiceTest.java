package com.netflix.mercado.service;

import com.netflix.mercado.dto.mercado.CreateMercadoRequest;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import com.netflix.mercado.dto.mercado.UpdateMercadoRequest;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.repository.MercadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MercadoService - Testes Unitários")
class MercadoServiceTest {

    @Mock
    private MercadoRepository mercadoRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private MercadoService mercadoService;

    private Mercado testMercado;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("owner@test.com");
        testUser.setFullName("Test Owner");

        testMercado = new Mercado();
        testMercado.setId(1L);
        testMercado.setNome("Mercado Central");
        testMercado.setCnpj("12345678000190");
        testMercado.setEmail("mercado@test.com");
        testMercado.setTelefone("1133334444");
        testMercado.setEndereco("Rua Principal, 100");
        testMercado.setCidade("São Paulo");
        testMercado.setEstado("SP");
        testMercado.setCep("01000-000");
        testMercado.setLatitude(BigDecimal.valueOf(-23.5505));
        testMercado.setLongitude(BigDecimal.valueOf(-46.6333));
        testMercado.setActive(true);
    }

    @Test
    @DisplayName("Deve criar mercado com sucesso")
    void testCreateMercado() {
        // Arrange
        CreateMercadoRequest request = new CreateMercadoRequest();
        request.setNome("Novo Mercado");
        request.setCidade("São Paulo");
        request.setEndereco("Av Test, 123");
        request.setLatitude(BigDecimal.valueOf(-23.5505));
        request.setLongitude(BigDecimal.valueOf(-46.6333));

        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any())).thenReturn(null);

        // Act
        MercadoResponse result = mercadoService.createMercado(request, testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(mercadoRepository).save(any(Mercado.class));
    }

    @Test
    @DisplayName("Deve obter mercado por ID")
    void testGetMercadoById() {
        // Arrange
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));

        // Act
        Mercado result = mercadoService.getMercadoEntityById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar mercado inexistente")
    void testGetMercadoByIdNotFound() {
        // Arrange
        when(mercadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mercadoService.getMercadoEntityById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar mercado")
    void testUpdateMercado() {
        // Arrange
        UpdateMercadoRequest request = new UpdateMercadoRequest();
        request.setNome("Mercado Atualizado");

        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any())).thenReturn(null);

        // Act
        MercadoResponse result = mercadoService.updateMercado(1L, request, testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(mercadoRepository).save(any(Mercado.class));
    }

    @Test
    @DisplayName("Deve deletar mercado")
    void testDeleteMercado() {
        // Arrange
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);
        when(auditLogRepository.save(any())).thenReturn(null);

        // Act
        mercadoService.deleteMercado(1L, testUser);

        // Assert
        verify(mercadoRepository).save(any(Mercado.class));
    }

    @Test
    @DisplayName("Deve listar todos mercados")
    void testListAllMercados() {
        // Arrange
        Mercado mercado2 = new Mercado();
        mercado2.setId(2L);
        mercado2.setNome("Mercado 2");

        Page<Mercado> page = new PageImpl<>(Arrays.asList(testMercado, mercado2));
        when(mercadoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<MercadoResponse> result = mercadoService.getAllMercados(Pageable.unpaged());

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve buscar mercados por nome")
    void testSearchMercadosByNome() {
        // Arrange
        Page<Mercado> page = new PageImpl<>(Arrays.asList(testMercado));
        when(mercadoRepository.findByNomeContainingIgnoreCase(eq("Central"), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<Mercado> result = mercadoService.buscarPorNome("Central", Pageable.unpaged());

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve buscar mercados por cidade")
    void testSearchMercadosByCidade() {
        // Arrange
        Page<Mercado> page = new PageImpl<>(Arrays.asList(testMercado));
        when(mercadoRepository.findByCidade(eq("São Paulo"), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<Mercado> result = mercadoService.buscarPorCidade("São Paulo", Pageable.unpaged());

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve buscar mercados próximos por geolocalização")
    void testBuscarMercadosProximos() {
        // Arrange
        when(mercadoRepository.findByProximidade(
                anyDouble(), 
                anyDouble(), 
                anyDouble()))
                .thenReturn(Arrays.asList(testMercado));

        // Act
        var result = mercadoService.buscarPorProximidade(-23.5505, -46.6333, 5.0);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve aprovar mercado")
    void testAprovarMercado() {
        // Arrange
        testMercado.setActive(false);
        when(mercadoRepository.findById(1L)).thenReturn(Optional.of(testMercado));
        when(mercadoRepository.save(any(Mercado.class))).thenReturn(testMercado);

        // Act
        mercadoService.aprovarMercado(1L);

        // Assert
        verify(mercadoRepository).save(any(Mercado.class));
    }
}
