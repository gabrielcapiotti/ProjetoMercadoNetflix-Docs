package com.netflix.mercado.service;

import com.netflix.mercado.entity.Comentario;
import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.exception.UnauthorizedException;
import com.netflix.mercado.repository.ComentarioRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Service responsável por gerenciar comentários em avaliações.
 * Implementa lógica de criação, resposta, moderação e gerenciamento de curtidas.
 */
@Slf4j
@Service
@Transactional
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AvaliacaoService avaliacaoService;

    /**
     * Cria um novo comentário em uma avaliação.
     *
     * @param request dados do comentário
     * @param usuario usuário criando o comentário
     * @return o comentário criado
     * @throws ValidationException se dados inválidos
     */
    public Comentario criarComentario(CreateComentarioRequest request, User usuario) {
        log.info("Criando comentário para avaliação ID: {} pelo usuário: {}", request.getAvaliacaoId(), usuario.getEmail());

        // Validar conteúdo
        if (request.getConteudo() == null || request.getConteudo().isBlank()) {
            throw new ValidationException("Conteúdo do comentário é obrigatório");
        }

        if (request.getConteudo().length() > 1000) {
            throw new ValidationException("Comentário não pode exceder 1000 caracteres");
        }

        // Buscar avaliação
        Avaliacao avaliacao = avaliacaoService.obterAvaliacaoPorId(request.getAvaliacaoId());

        Comentario comentario = new Comentario();
        comentario.setAvaliacao(avaliacao);
        comentario.setUsuario(usuario);
        comentario.setConteudo(request.getConteudo());
        comentario.setPaiComentario(null); // Comentário raiz
        comentario.setAprovado(true); // Considerar aprovado por padrão
        comentario.setCurtidas(new HashSet<>());

        comentario = comentarioRepository.save(comentario);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "CREATE",
                "COMENTARIO",
                comentario.getId(),
                "Comentário criado para avaliação " + request.getAvaliacaoId(),
                LocalDateTime.now()
        ));

        log.info("Comentário criado com sucesso. ID: {}", comentario.getId());
        return comentario;
    }

    /**
     * Atualiza um comentário existente.
     *
     * @param id ID do comentário
     * @param request dados a atualizar
     * @param usuario usuário realizando a atualização
     * @return o comentário atualizado
     * @throws ResourceNotFoundException se comentário não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public Comentario atualizarComentario(Long id, UpdateComentarioRequest request, User usuario) {
        log.info("Atualizando comentário com ID: {}", id);

        Comentario comentario = obterComentarioPorId(id);

        // Verificar autorização
        if (!comentario.getUsuario().getId().equals(usuario.getId()) && !isAdmin(usuario)) {
            log.warn("Tentativa de atualização não autorizada do comentário ID: {} por usuário: {}", id, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para atualizar este comentário");
        }

        String conteudoAntigo = comentario.getConteudo();

        // Atualizar conteúdo
        if (request.getConteudo() != null && !request.getConteudo().isBlank()) {
            if (request.getConteudo().length() > 1000) {
                throw new ValidationException("Comentário não pode exceder 1000 caracteres");
            }
            comentario.setConteudo(request.getConteudo());
        }

        comentario = comentarioRepository.save(comentario);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "UPDATE",
                "COMENTARIO",
                id,
                "Conteúdo alterado: " + conteudoAntigo + " -> " + comentario.getConteudo(),
                LocalDateTime.now()
        ));

        log.info("Comentário atualizado com sucesso. ID: {}", id);
        return comentario;
    }

    /**
     * Deleta um comentário.
     *
     * @param id ID do comentário
     * @param usuario usuário realizando a deleção
     * @throws ResourceNotFoundException se comentário não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public void deletarComentario(Long id, User usuario) {
        log.info("Deletando comentário com ID: {}", id);

        Comentario comentario = obterComentarioPorId(id);

        // Verificar autorização
        if (!comentario.getUsuario().getId().equals(usuario.getId()) && !isAdmin(usuario)) {
            log.warn("Tentativa de deleção não autorizada do comentário ID: {} por usuário: {}", id, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para deletar este comentário");
        }

        comentarioRepository.delete(comentario);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "DELETE",
                "COMENTARIO",
                id,
                "Comentário deletado",
                LocalDateTime.now()
        ));

        log.info("Comentário deletado com sucesso. ID: {}", id);
    }

    /**
     * Obtém um comentário pelo ID.
     *
     * @param id ID do comentário
     * @return o comentário encontrado
     * @throws ResourceNotFoundException se comentário não existe
     */
    @Transactional(readOnly = true)
    public Comentario obterComentarioPorId(Long id) {
        log.debug("Buscando comentário com ID: {}", id);
        return comentarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comentário não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Comentário não encontrado com ID: " + id);
                });
    }

    /**
     * Obtém todos os comentários de uma avaliação com paginação.
     *
     * @param avaliacaoId ID da avaliação
     * @param pageable informações de paginação
     * @return página de comentários
     */
    @Transactional(readOnly = true)
    public Page<Comentario> obterComentariosPorAvaliacao(Long avaliacaoId, Pageable pageable) {
        log.debug("Buscando comentários da avaliação ID: {}", avaliacaoId);
        avaliacaoService.obterAvaliacaoPorId(avaliacaoId); // Validar que avaliação existe
        return comentarioRepository.findByAvaliacaoIdAndPaiComentarioIsNull(avaliacaoId, pageable);
    }

    /**
     * Obtém respostas (comentários filhos) de um comentário com paginação.
     *
     * @param comentarioPaiId ID do comentário pai
     * @param pageable informações de paginação
     * @return página de respostas
     */
    @Transactional(readOnly = true)
    public Page<Comentario> obterRespostas(Long comentarioPaiId, Pageable pageable) {
        log.debug("Buscando respostas do comentário ID: {}", comentarioPaiId);
        obterComentarioPorId(comentarioPaiId); // Validar que comentário pai existe
        return comentarioRepository.findByPaiComentarioId(comentarioPaiId, pageable);
    }

    /**
     * Responde a um comentário existente.
     *
     * @param comentarioPaiId ID do comentário pai
     * @param request dados da resposta
     * @param usuario usuário criando a resposta
     * @return o comentário resposta criado
     * @throws ValidationException se dados inválidos
     */
    public Comentario responderComentario(Long comentarioPaiId, CreateComentarioRequest request, User usuario) {
        log.info("Respondendo ao comentário ID: {} pelo usuário: {}", comentarioPaiId, usuario.getEmail());

        // Validar conteúdo
        if (request.getConteudo() == null || request.getConteudo().isBlank()) {
            throw new ValidationException("Conteúdo da resposta é obrigatório");
        }

        if (request.getConteudo().length() > 1000) {
            throw new ValidationException("Resposta não pode exceder 1000 caracteres");
        }

        Comentario comentarioPai = obterComentarioPorId(comentarioPaiId);

        Comentario respostaComentario = new Comentario();
        respostaComentario.setAvaliacao(comentarioPai.getAvaliacao());
        respostaComentario.setUsuario(usuario);
        respostaComentario.setConteudo(request.getConteudo());
        respostaComentario.setPaiComentario(comentarioPai);
        respostaComentario.setAprovado(true);
        respostaComentario.setCurtidas(new HashSet<>());

        respostaComentario = comentarioRepository.save(respostaComentario);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "CREATE",
                "COMENTARIO",
                respostaComentario.getId(),
                "Resposta criada para comentário " + comentarioPaiId,
                LocalDateTime.now()
        ));

        log.info("Resposta criada com sucesso. ID: {}", respostaComentario.getId());
        return respostaComentario;
    }

    /**
     * Adiciona uma curtida ao comentário.
     *
     * @param id ID do comentário
     * @param usuario usuário curtindo
     */
    public void adicionarCurtida(Long id, User usuario) {
        log.debug("Adicionando curtida ao comentário ID: {} do usuário: {}", id, usuario.getEmail());

        Comentario comentario = obterComentarioPorId(id);

        if (comentario.getCurtidas().contains(usuario)) {
            throw new ValidationException("Você já curtiu este comentário");
        }

        comentario.getCurtidas().add(usuario);
        comentarioRepository.save(comentario);

        log.debug("Curtida adicionada ao comentário ID: {}", id);
    }

    /**
     * Remove uma curtida do comentário.
     *
     * @param id ID do comentário
     * @param usuario usuário removendo a curtida
     */
    public void removerCurtida(Long id, User usuario) {
        log.debug("Removendo curtida do comentário ID: {} do usuário: {}", id, usuario.getEmail());

        Comentario comentario = obterComentarioPorId(id);

        if (!comentario.getCurtidas().contains(usuario)) {
            throw new ValidationException("Você não curtiu este comentário");
        }

        comentario.getCurtidas().remove(usuario);
        comentarioRepository.save(comentario);

        log.debug("Curtida removida do comentário ID: {}", id);
    }

    /**
     * Aprova ou desaprova um comentário (apenas admin/moderator).
     *
     * @param id ID do comentário
     * @param aprovado true para aprovar, false para desaprovar
     */
    public void moderarComentario(Long id, Boolean aprovado) {
        log.info("Moderando comentário ID: {}, aprovado: {}", id, aprovado);

        Comentario comentario = obterComentarioPorId(id);
        comentario.setAprovado(aprovado);
        comentarioRepository.save(comentario);

        auditLogRepository.save(new AuditLog(
                null,
                null, // Admin action
                "UPDATE",
                "COMENTARIO",
                id,
                "Comentário " + (aprovado ? "aprovado" : "desaprovado"),
                LocalDateTime.now()
        ));

        log.info("Comentário moderado com sucesso. ID: {}", id);
    }

    /**
     * Verifica se um usuário é admin.
     *
     * @param usuario usuário a verificar
     * @return true se é admin
     */
    private boolean isAdmin(User usuario) {
        return usuario.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
    }
}
