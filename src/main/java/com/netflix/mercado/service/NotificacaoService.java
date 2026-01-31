package com.netflix.mercado.service;

import com.netflix.mercado.entity.Notificacao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.NotificacaoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.notificacao.CreateNotificacaoRequest;
import com.netflix.mercado.dto.notificacao.NotificacaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

/**
 * Service responsável por gerenciar notificações de usuários.
 * Implementa lógica de criação, envio, leitura e limpeza de notificações.
 */
@Service
@Transactional
public class NotificacaoService {

    private static final Logger log = Logger.getLogger(NotificacaoService.class.getName());

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Cria uma nova notificação.
     *
     * @param request dados da notificação
     * @return a notificação criada
     * @throws ValidationException se dados inválidos
     */
    public Notificacao criarNotificacao(CreateNotificacaoRequest request) {
        log.info("Criando notificação para usuário ID: " + request.getUsuarioId() + "");

        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            throw new ValidationException("Título da notificação é obrigatório");
        }

        if (request.getConteudo() == null || request.getConteudo().isBlank()) {
            throw new ValidationException("Conteúdo da notificação é obrigatório");
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo(request.getTitulo());
        notificacao.setConteudo(request.getConteudo());
        notificacao.setUser(usuario);
        notificacao.setLida(false);

        notificacao = notificacaoRepository.save(notificacao);

        log.info("Notificação criada com sucesso. ID: " + notificacao.getId() + "");
        return notificacao;
    }

    /**
     * Cria uma nova notificação a partir de um request.
     *
     * @param request dados da notificação
     * @return a notificação criada
     */
    private Notificacao criarNotificacao(CreateNotificacaoRequest request) {
        // Buscar usuário por ID
        // TODO: implementar UserService.findById ou usar repository
        throw new UnsupportedOperationException("Método não implementado - precisa buscar usuário por ID");
    }

    /**
     * Envia uma notificação para um usuário.
     *
     * @param usuario usuário destinatário
     * @param titulo título da notificação
     * @param conteudo conteúdo da notificação
     * @param tipo tipo da notificação
     * @return a notificação criada
     */
    public Notificacao enviarNotificacao(User usuario, String titulo, String conteudo, String tipo) {
        log.info("Enviando notificação para usuário ID: " + usuario.getId() + " - Tipo: " + tipo + "");

        CreateNotificacaoRequest request = new CreateNotificacaoRequest();
        request.setUsuarioId(usuario.getId());
        request.setTitulo(titulo);
        request.setConteudo(conteudo);
        request.setTipo(tipo);

        Notificacao notificacao = criarNotificacao(request);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                usuario,
                AuditLog.TipoAcao.CRIACAO,
                "NOTIFICACAO",
                notificacao.getId(),
                "Notificação enviada: " + titulo,
                null, null, null, null, null
        ));

        log.info("Notificação enviada com sucesso para usuário ID: " + usuario.getId() + "");
        return notificacao;
    }

    /**
     * Obtém notificações de um usuário com paginação.
     *
     * @param usuarioId ID do usuário
     * @param pageable informações de paginação
     * @return página de notificações
     */
    @Transactional(readOnly = true)
    public Page<Notificacao> obterNotificacionesDoUsuario(User usuario, Pageable pageable) {
        log.fine("Buscando notificações do usuário: " + usuario.getEmail() + "");
        return notificacaoRepository.findByUser(usuario, pageable);
    }

    /**
     * Obtém notificações não lidas de um usuário com paginação.
     *
     * @param usuarioId ID do usuário
     * @param pageable informações de paginação
     * @return página de notificações não lidas
     */
    @Transactional(readOnly = true)
    public Page<Notificacao> obterNaoLidas(User usuario, Pageable pageable) {
        log.fine("Buscando notificações não lidas do usuário: " + usuario.getEmail() + "");
        return notificacaoRepository.findByUserAndLidaFalseOrderByCreatedAtDesc(usuario, pageable);
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação
     * @throws ResourceNotFoundException se notificação não existe
     */
    public void marcarComoLida(Long id) {
        log.fine("Marcando notificação como lida. ID: " + id + "");

        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Notificação não encontrada com ID: " + id + "");
                    return new ResourceNotFoundException("Notificação não encontrada com ID: " + id);
                });

        notificacao.setLida(true);
        notificacao.setDataLeitura(LocalDateTime.now());
        notificacaoRepository.save(notificacao);

        log.fine("Notificação marcada como lida. ID: " + id + "");
    }

    /**
     * Marca todas as notificações de um usuário como lidas.
     *
     * @param usuario usuário proprietário das notificações
     */
    public void marcarTodosComoLido(User usuario) {
        log.info("Marcando todas as notificações do usuário ID: " + usuario.getId() + " como lidas");

        Page<Notificacao> naoLidas = obterNaoLidas(usuario.getId(), Pageable.unpaged());
        LocalDateTime agora = LocalDateTime.now();

        naoLidas.forEach(notificacao -> {
            notificacao.setLida(true);
            notificacao.setDataLeitura(agora);
        });

        notificacaoRepository.saveAll(naoLidas.getContent());

        log.info("Todas as notificações do usuário ID: " + usuario.getId() + " marcadas como lidas");
    }

    /**
     * Deleta uma notificação.
     *
     * @param id ID da notificação
     * @throws ResourceNotFoundException se notificação não existe
     */
    public void deletarNotificacao(Long id) {
        log.info("Deletando notificação com ID: " + id + "");

        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Notificação não encontrada com ID: " + id + "");
                    return new ResourceNotFoundException("Notificação não encontrada com ID: " + id);
                });

        notificacaoRepository.delete(notificacao);

        log.info("Notificação deletada com sucesso. ID: " + id + "");
    }

    /**
     * Conta o número de notificações não lidas de um usuário.
     *
     * @param usuarioId ID do usuário
     * @return quantidade de notificações não lidas
     */
    @Transactional(readOnly = true)
    public Long contarNaoLidas(User usuario) {
        log.fine("Contando notificações não lidas do usuário: " + usuario.getEmail() + "");
        return notificacaoRepository.countUnreadByUser(usuario);
    }

    /**
     * Limpa notificações antigas (executado periodicamente).
     * Remove notificações com mais de X dias de retenção.
     *
     * @param diasRetencao dias de retenção
     */
    @Scheduled(cron = "0 0 2 * * *") // Executar diariamente às 2 da manhã
    @Transactional
    public void limparNotificacoesAntigas(Long diasRetencao) {
        log.info("Iniciando limpeza de notificações com mais de " + diasRetencao + " dias");

        LocalDateTime dataLimite = LocalDateTime.now().minus(diasRetencao, ChronoUnit.DAYS);

        // TODO: implementar método deleteByCreatedAtBefore no repository
        // long deletadas = notificacaoRepository.deleteByCreatedAtBefore(dataLimite);
        long deletadas = 0;

        log.info("Limpeza de notificações concluída. " + deletadas + " notificações deletadas");
    }

    /**
     * Versão alternativa que pode ser chamada sem parâmetro.
     * Remove notificações com mais de 30 dias.
     */
    @Scheduled(cron = "0 0 2 * * *") // Executar diariamente às 2 da manhã
    @Transactional
    public void limparNotificacoesAutomatico() {
        log.info("Iniciando limpeza automática de notificações antigas (30+ dias)");

        LocalDateTime dataLimite = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        // TODO: implementar método deleteByCreatedAtBefore no repository
        // long deletadas = notificacaoRepository.deleteByCreatedAtBefore(dataLimite);
        long deletadas = 0;

        log.info("Limpeza automática concluída. " + deletadas + " notificações deletadas");
    }

    public NotificacaoService() {
    }

    public NotificacaoService(NotificacaoRepository notificacaoRepository, AuditLogRepository auditLogRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public NotificacaoRepository getNotificacaoRepository() {
        return this.notificacaoRepository;
    }

    public void setNotificacaoRepository(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public AuditLogRepository getAuditLogRepository() {
        return this.auditLogRepository;
    }

    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Métodos wrapper com nomes em inglês para controllers
    public Page<NotificacaoResponse> listNotificacoes(User usuario, Pageable pageable, Boolean lidas) {
        Page<Notificacao> notificacoes = obterNotificacionesDoUsuario(usuario, pageable);
        return notificacoes.map(NotificacaoResponse::fromEntity);
    }

    public Long countUnreadNotificacoes(User usuario) {
        return contarNaoLidas(usuario);
    }

    public NotificacaoResponse markAsRead(Long notificacaoId, User usuario) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
            .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        
        if (!notificacao.getUser().getId().equals(usuario.getId())) {
            throw new ValidationException("Você não tem permissão para acessar esta notificação");
        }
        
        notificacao.setLida(true);
        notificacao = notificacaoRepository.save(notificacao);
        return NotificacaoResponse.fromEntity(notificacao);
    }

    public void deleteNotificacao(Long notificacaoId, User usuario) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
            .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
        
        if (!notificacao.getUser().getId().equals(usuario.getId())) {
            throw new ValidationException("Você não tem permissão para deletar esta notificação");
        }
        
        notificacaoRepository.delete(notificacao);
    }

    public void markAllAsRead(User usuario) {
        Page<Notificacao> notificacoes = obterNotificacionesDoUsuario(usuario, Pageable.unpaged());
        notificacoes.forEach(n -> {
            n.setLida(true);
            notificacaoRepository.save(n);
        });
    }

    public void deleteAllNotificacoes(User usuario) {
        Page<Notificacao> notificacoes = obterNotificacionesDoUsuario(usuario, Pageable.unpaged());
        notificacoes.forEach(notificacaoRepository::delete);
    }

}
