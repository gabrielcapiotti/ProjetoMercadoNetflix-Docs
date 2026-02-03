package com.netflix.mercado.service;

import com.netflix.mercado.entity.Favorito;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.FavoritoRepository;
import com.netflix.mercado.dto.favorito.CreateFavoritoRequest;
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

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para FavoritoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FavoritoService - Testes Unitários")
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private MercadoService mercadoService;

    @InjectMocks
    private FavoritoService favoritoService;

    private Favorito testFavorito;
    private User testUser;
    private Mercado testMercado;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setActive(true);

        testMercado = new Mercado();
        testMercado.setId(1L);
        testMercado.setNome("Mercado Teste");
        testMercado.setActive(true);

        testFavorito = new Favorito();
        testFavorito.setId(1L);
        testFavorito.setUser(testUser);
        testFavorito.setMercado(testMercado);
        testFavorito.setPrioridade(1);
    }

    @Test
    @DisplayName("Deve adicionar favorito com sucesso")
    void testAddFavorito() {
        // Arrange
        when(mercadoService.getMercadoEntityById(1L)).thenReturn(testMercado);
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(testFavorito);

        // Act
        Favorito result = favoritoService.adicionarFavorito(1L, testUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMercado().getId()).isEqualTo(1L);
        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar favorito duplicado")
    void testAddFavoritoDuplicate() {
        // Arrange
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.of(testFavorito));

        // Act & Assert
        assertThatThrownBy(() -> favoritoService.adicionarFavorito(1L, testUser))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Deve remover favorito com sucesso")
    void testRemoveFavorito() {
        // Arrange
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.of(testFavorito));
        doNothing().when(favoritoRepository).delete(any(Favorito.class));

        // Act
        favoritoService.removerFavorito(1L, testUser);

        // Assert
        verify(favoritoRepository).delete(any(Favorito.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover favorito inexistente")
    void testRemoveFavoritoNotFound() {
        // Arrange
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> favoritoService.removerFavorito(1L, testUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar favoritos do usuário com sucesso")
    void testListFavoritos() {
        // Arrange
        Favorito favorito2 = new Favorito();
        favorito2.setId(2L);
        favorito2.setMercado(testMercado);
        favorito2.setPrioridade(2);

        List<Favorito> favoritos = Arrays.asList(testFavorito, favorito2);
        when(favoritoRepository.findByUserId(1L)).thenReturn(favoritos);

        // Act
        List<Favorito> result = favoritoService.obterFavoritosComPrioridade(testUser);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Deve verificar se mercado é favorito")
    void testIsFavorito() {
        // Arrange
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.of(testFavorito));

        // Act
        Boolean result = favoritoService.verificarFavorito(1L, testUser);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que mercado não é favorito")
    void testIsNotFavorito() {
        // Arrange
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act
        Boolean result = favoritoService.verificarFavorito(1L, testUser);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar prioridade do favorito")
    void testUpdatePrioridade() {
        // Arrange
        Favorito fav = new Favorito();
        fav.setId(1L);
        fav.setPrioridade(1);

        when(favoritoRepository.findById(1L)).thenReturn(Optional.of(fav));
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(fav);

        // Act
        favoritoService.definirPrioridade(1L, 5);

        // Assert
        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    @DisplayName("Deve contar favoritos do usuário")
    void testCountFavoritos() {
        // Arrange
        when(favoritoRepository.countByUserId(1L)).thenReturn(3L);

        // Act
        Long result = favoritoService.contarFavoritosDoUsuario(testUser);

        // Assert
        assertThat(result).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve fazer toggle de favorito")
    void testToggleFavorito() {
        // Arrange
        when(favoritoRepository.findByMercadoIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(mercadoService.getMercadoEntityById(1L)).thenReturn(testMercado);
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(testFavorito);

        // Act
        Boolean result = favoritoService.toggleFavorito(1L, testUser);

        // Assert
        assertThat(result).isTrue();
        verify(favoritoRepository).save(any(Favorito.class));
    }
}
