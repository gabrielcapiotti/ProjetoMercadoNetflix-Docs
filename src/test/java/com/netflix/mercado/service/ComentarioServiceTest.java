package com.netflix.mercado.service;

import com.netflix.mercado.entity.Comentario;
import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.UnauthorizedException;
import com.netflix.mercado.repository.ComentarioRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.comentario.CreateComentarioRequest;
import com.netflix.mercado.dto.comentario.UpdateComentarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ComentarioService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ComentarioService - Testes Unitários")
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    private Comentario testComentario;
    private User testUser;
    private Avaliacao testAvaliacao;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setActive(true);

        testAvaliacao = new Avaliacao();
        testAvaliacao.setId(1L);
        testAvaliacao.setUser(testUser);

        testComentario = new Comentario();
        testComentario.setId(1L);
        testComentario.setAvaliacao(testAvaliacao);
        testComentario.setUser(testUser);
        testComentario.setTexto("Comentário de teste");
        testComentario.setRespostas(new HashSet<>());
        testComentario.setCurtidas(0L);
    }

    @Test
    @DisplayName("Deve criar comentário com sucesso")
    void testCreateComentario() {
        // Arrange
        CreateComentarioRequest request = new CreateComentarioRequest();
        request.setAvaliacaoId(1L);
        request.setTexto("Novo comentário");

        when(comentarioRepository.save(any(Comentario.class))).thenReturn(testComentario);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Comentario result = comentarioService.criarComentario(request, testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Deve obter comentário por ID com sucesso")
    void testGetComentarioById() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(testComentario));

        // Act
        Comentario result = comentarioService.obterComentarioPorId(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(comentarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando comentário não encontrado")
    void testGetComentarioByIdNotFound() {
        // Arrange
        when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> comentarioService.obterComentarioPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar comentários da avaliação com sucesso")
    void testListComentariosByAvaliacao() {
        // Arrange
        Comentario comentario2 = new Comentario();
        comentario2.setId(2L);
        comentario2.setTexto("Outro comentário");

        when(comentarioRepository.findByAvaliacaoId(1L)).thenReturn(Arrays.asList(testComentario, comentario2));

        // Act
        var result = comentarioService.listarComentariosPorAvaliacao(1L);

        // Assert
        assertThat(result).isNotEmpty();
        verify(comentarioRepository).findByAvaliacaoId(1L);
    }

    @Test
    @DisplayName("Deve atualizar comentário com sucesso")
    void testUpdateComentario() {
        // Arrange
        UpdateComentarioRequest request = new UpdateComentarioRequest();
        request.setTexto("Comentário atualizado");

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(testComentario));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(testComentario);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Comentario result = comentarioService.atualizarComentario(1L, request, testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar comentário de outro usuário")
    void testUpdateComentarioUnauthorized() {
        // Arrange
        User outroUser = new User();
        outroUser.setId(999L);
        outroUser.setEmail("outro@example.com");

        UpdateComentarioRequest request = new UpdateComentarioRequest();
        request.setTexto("Tentativa de atualização");

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(testComentario));

        // Act & Assert
        assertThatThrownBy(() -> comentarioService.atualizarComentario(1L, request, outroUser))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Deve adicionar curtida ao comentário")
    void testAddCurtida() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(testComentario));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(testComentario);

        // Act
        comentarioService.adicionarCurtida(1L);

        // Assert
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Deve deletar comentário com sucesso")
    void testDeleteComentario() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(testComentario));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(testComentario);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        comentarioService.deletarComentario(1L, testUser);

        // Assert
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Deve contar comentários da avaliação")
    void testCountComentarios() {
        // Arrange
        when(comentarioRepository.countByAvaliacaoId(1L)).thenReturn(3L);

        // Act
        long result = comentarioService.contarComentariosPorAvaliacao(1L);

        // Assert
        assertThat(result).isEqualTo(3L);
        verify(comentarioRepository).countByAvaliacaoId(1L);
    }
}
