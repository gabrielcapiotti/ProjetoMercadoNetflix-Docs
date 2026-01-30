package com.netflix.mercado.service;

import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.exception.UnauthorizedException;
import com.netflix.mercado.repository.AvaliacaoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.avaliacao.CreateAvaliacaoRequest;
import com.netflix.mercado.dto.avaliacao.UpdateAvaliacaoRequest;
import com.netflix.mercado.dto.avaliacao.RatingStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsável por gerenciar avaliações de mercados.
 * Implementa lógica de criação, atualização, exclusão e cálculo de estatísticas de avaliações.
 */
@Service
@Transactional
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private MercadoService mercadoService;

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Cria uma nova avaliação para um mercado.
     *
     * @param request dados da avaliação
     * @param usuario usuário criando a avaliação
     * @return a avaliação criada
     * @throws ValidationException se dados inválidos ou duplicata
     */
    public Avaliacao criarAvaliacao(CreateAvaliacaoRequest request, User usuario) {
        log.info("Criando avaliação para mercado ID: {} pelo usuário: {}", request.getMercadoId(), usuario.getEmail());

        // Validar rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ValidationException("Rating deve estar entre 1 e 5 estrelas");
        }

        // Buscar mercado
        Mercado mercado = mercadoService.getMercadoById(request.getMercadoId());

        // Validar duplicata
        validarDuplicata(request.getMercadoId(), usuario.getId());

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setMercado(mercado);
        avaliacao.setUsuario(usuario);
        avaliacao.setRating(request.getRating());
        avaliacao.getTitulo(request.getTitulo());
        avaliacao.setComentarioTitulo(request.getTitulo());
        avaliacao.setDescricao(request.getDescricao());
        avaliacao.setUtil(0);
        avaliacao.setInutil(0);

        avaliacao = avaliacaoRepository.save(avaliacao);

        // Atualizar avaliação média do mercado
        mercadoService.atualizarAvaliacaoMedia(request.getMercadoId());

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "CREATE",
                "AVALIACAO",
                avaliacao.getId(),
                String.format("Avaliação criada: %s estrelas para mercado %d", request.getRating(), request.getMercadoId()),
                LocalDateTime.now()
        ));

        log.info("Avaliação criada com sucesso. ID: {}", avaliacao.getId());
        return avaliacao;
    }

    /**
     * Atualiza uma avaliação existente.
     *
     * @param id ID da avaliação
     * @param request dados a atualizar
     * @param usuario usuário realizando a atualização
     * @return a avaliação atualizada
     * @throws ResourceNotFoundException se avaliação não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public Avaliacao atualizarAvaliacao(Long id, UpdateAvaliacaoRequest request, User usuario) {
        log.info("Atualizando avaliação com ID: {}", id);

        Avaliacao avaliacao = obterAvaliacaoPorId(id);

        // Verificar autorização
        if (!avaliacao.getUsuario().getId().equals(usuario.getId()) && !isAdmin(usuario)) {
            log.warn("Tentativa de atualização não autorizada da avaliação ID: {} por usuário: {}", id, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para atualizar esta avaliação");
        }

        String valoresAnteriores = String.format("rating=%d, titulo=%s", avaliacao.getRating(), avaliacao.getComentarioTitulo());

        // Atualizar campos
        if (request.getRating() != null) {
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new ValidationException("Rating deve estar entre 1 e 5 estrelas");
            }
            avaliacao.setRating(request.getRating());
        }
        if (request.getTitulo() != null && !request.getTitulo().isBlank()) {
            avaliacao.setComentarioTitulo(request.getTitulo());
        }
        if (request.getDescricao() != null) {
            avaliacao.setDescricao(request.getDescricao());
        }

        avaliacao = avaliacaoRepository.save(avaliacao);

        // Atualizar avaliação média do mercado
        mercadoService.atualizarAvaliacaoMedia(avaliacao.getMercado().getId());

        String valoresNovos = String.format("rating=%d, titulo=%s", avaliacao.getRating(), avaliacao.getComentarioTitulo());

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "UPDATE",
                "AVALIACAO",
                id,
                "Alterações: " + valoresAnteriores + " -> " + valoresNovos,
                LocalDateTime.now()
        ));

        log.info("Avaliação atualizada com sucesso. ID: {}", id);
        return avaliacao;
    }

    /**
     * Deleta uma avaliação.
     *
     * @param id ID da avaliação
     * @param usuario usuário realizando a deleção
     * @throws ResourceNotFoundException se avaliação não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public void deletarAvaliacao(Long id, User usuario) {
        log.info("Deletando avaliação com ID: {}", id);

        Avaliacao avaliacao = obterAvaliacaoPorId(id);

        // Verificar autorização
        if (!avaliacao.getUsuario().getId().equals(usuario.getId()) && !isAdmin(usuario)) {
            log.warn("Tentativa de deleção não autorizada da avaliação ID: {} por usuário: {}", id, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para deletar esta avaliação");
        }

        Long mercadoId = avaliacao.getMercado().getId();
        avaliacaoRepository.delete(avaliacao);

        // Atualizar avaliação média do mercado
        mercadoService.atualizarAvaliacaoMedia(mercadoId);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "DELETE",
                "AVALIACAO",
                id,
                "Avaliação deletada",
                LocalDateTime.now()
        ));

        log.info("Avaliação deletada com sucesso. ID: {}", id);
    }

    /**
     * Obtém uma avaliação pelo ID.
     *
     * @param id ID da avaliação
     * @return a avaliação encontrada
     * @throws ResourceNotFoundException se avaliação não existe
     */
    @Transactional(readOnly = true)
    public Avaliacao obterAvaliacaoPorId(Long id) {
        log.debug("Buscando avaliação com ID: {}", id);
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Avaliação não encontrada com ID: {}", id);
                    return new ResourceNotFoundException("Avaliação não encontrada com ID: " + id);
                });
    }

    /**
     * Obtém todas as avaliações de um mercado com paginação.
     *
     * @param mercadoId ID do mercado
     * @param pageable informações de paginação
     * @return página de avaliações
     */
    @Transactional(readOnly = true)
    public Page<Avaliacao> obterAvaliacoesPorMercado(Long mercadoId, Pageable pageable) {
        log.debug("Buscando avaliações do mercado ID: {}", mercadoId);
        mercadoService.getMercadoById(mercadoId); // Validar que mercado existe
        return avaliacaoRepository.findByMercadoId(mercadoId, pageable);
    }

    /**
     * Obtém todas as avaliações de um usuário com paginação.
     *
     * @param usuarioId ID do usuário
     * @param pageable informações de paginação
     * @return página de avaliações
     */
    @Transactional(readOnly = true)
    public Page<Avaliacao> obterAvaliacoesPorUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando avaliações do usuário ID: {}", usuarioId);
        return avaliacaoRepository.findByUsuarioId(usuarioId, pageable);
    }

    /**
     * Calcula estatísticas de avaliações de um mercado.
     *
     * @param mercadoId ID do mercado
     * @return resposta com estatísticas
     */
    @Transactional(readOnly = true)
    public RatingStatsResponse calcularEstatisticas(Long mercadoId) {
        log.debug("Calculando estatísticas de avaliações do mercado ID: {}", mercadoId);

        mercadoService.getMercadoById(mercadoId); // Validar que mercado existe

        long total = avaliacaoRepository.countByMercadoId(mercadoId);
        long cinco = avaliacaoRepository.countByMercadoIdAndRating(mercadoId, 5);
        long quatro = avaliacaoRepository.countByMercadoIdAndRating(mercadoId, 4);
        long tres = avaliacaoRepository.countByMercadoIdAndRating(mercadoId, 3);
        long dois = avaliacaoRepository.countByMercadoIdAndRating(mercadoId, 2);
        long um = avaliacaoRepository.countByMercadoIdAndRating(mercadoId, 1);

        Double media = avaliacaoRepository.findAverageRatingByMercadoId(mercadoId);

        return RatingStatsResponse.builder()
                .totalAvaliacoes(total)
                .mediaAvaliacoes(media != null ? media : 0.0)
                .cincoEstrelas(cinco)
                .quatroEstrelas(quatro)
                .tresEstrelas(tres)
                .doisEstrelas(dois)
                .umEstrela(um)
                .build();
    }

    /**
     * Marca uma avaliação como útil.
     *
     * @param id ID da avaliação
     */
    public void marcarComoUtil(Long id) {
        log.debug("Marcando avaliação como útil. ID: {}", id);

        Avaliacao avaliacao = obterAvaliacaoPorId(id);
        avaliacao.setUtil(avaliacao.getUtil() + 1);
        avaliacaoRepository.save(avaliacao);

        log.debug("Avaliação marcada como útil. ID: {}", id);
    }

    /**
     * Marca uma avaliação como inútil.
     *
     * @param id ID da avaliação
     */
    public void marcarComoInutil(Long id) {
        log.debug("Marcando avaliação como inútil. ID: {}", id);

        Avaliacao avaliacao = obterAvaliacaoPorId(id);
        avaliacao.setInutil(avaliacao.getInutil() + 1);
        avaliacaoRepository.save(avaliacao);

        log.debug("Avaliação marcada como inútil. ID: {}", id);
    }

    /**
     * Valida se já existe avaliação do usuário para o mercado.
     *
     * @param mercadoId ID do mercado
     * @param usuarioId ID do usuário
     * @throws ValidationException se já existe avaliação
     */
    public void validarDuplicata(Long mercadoId, Long usuarioId) {
        log.debug("Validando duplicata de avaliação. Mercado: {}, Usuário: {}", mercadoId, usuarioId);

        if (avaliacaoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuarioId)) {
            log.warn("Usuário {} já avaliou mercado {}", usuarioId, mercadoId);
            throw new ValidationException("Você já avaliou este mercado");
        }
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
    public AvaliacaoService() {
    }

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, AuditLogRepository auditLogRepository, MercadoService mercadoService, NotificacaoService notificacaoService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.auditLogRepository = auditLogRepository;
        this.mercadoService = mercadoService;
        this.notificacaoService = notificacaoService;
    }

    public AvaliacaoRepository getAvaliacaoRepository() {
        return this.avaliacaoRepository;
    }

    public void setAvaliacaoRepository(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public AuditLogRepository getAuditLogRepository() {
        return this.auditLogRepository;
    }

    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public MercadoService getMercadoService() {
        return this.mercadoService;
    }

    public void setMercadoService(MercadoService mercadoService) {
        this.mercadoService = mercadoService;
    }

    public NotificacaoService getNotificacaoService() {
        return this.notificacaoService;
    }

    public void setNotificacaoService(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

}
