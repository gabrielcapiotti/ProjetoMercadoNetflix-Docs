package com.netflix.mercado.service;

import com.netflix.mercado.entity.Notificacao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.NotificacaoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.CreateNotificacaoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Service responsável por gerenciar notificações de usuários.
 * Implementa lógica de criação, envio, leitura e limpeza de notificações.
 */
@Service
@Transactional
public class NotificacaoService {

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
        log.info("Criando notificação para usuário ID: {}", request.getUsuarioId());

        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            throw new ValidationException("Título da notificação é obrigatório");
        }

        if (request.getConteudo() == null || request.getConteudo().isBlank()) {
            throw new ValidationException("Conteúdo da notificação é obrigatório");
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo(request.getTitulo());
        notificacao.setConteudo(request.getConteudo());
        notificacao.setTipoNotificacao(request.getTipo());
        notificacao.setLida(false);
        notificacao.setDataEnvio(LocalDateTime.now());

        notificacao = notificacaoRepository.save(notificacao);

        log.info("Notificação criada com sucesso. ID: {}", notificacao.getId());
        return notificacao;
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
        log.info("Enviando notificação para usuário ID: {} - Tipo: {}", usuario.getId(), tipo);

        CreateNotificacaoRequest request = new CreateNotificacaoRequest();
        request.setUsuarioId(usuario.getId());
        request.setTitulo(titulo);
        request.setConteudo(conteudo);
        request.setTipo(tipo);

        Notificacao notificacao = criarNotificacao(request);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "CREATE",
                "NOTIFICACAO",
                notificacao.getId(),
                "Notificação enviada: " + titulo,
                LocalDateTime.now()
        ));

        log.info("Notificação enviada com sucesso para usuário ID: {}", usuario.getId());
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
    public Page<Notificacao> obterNotificacionesDoUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando notificações do usuário ID: {}", usuarioId);
        return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuarioId, pageable);
    }

    /**
     * Obtém notificações não lidas de um usuário com paginação.
     *
     * @param usuarioId ID do usuário
     * @param pageable informações de paginação
     * @return página de notificações não lidas
     */
    @Transactional(readOnly = true)
    public Page<Notificacao> obterNaoLidas(Long usuarioId, Pageable pageable) {
        log.debug("Buscando notificações não lidas do usuário ID: {}", usuarioId);
        return notificacaoRepository.findByUsuarioIdAndLidaFalseOrderByDataEnvioDesc(usuarioId, pageable);
    }

    /**
     * Marca uma notificação como lida.
     *
     * @param id ID da notificação
     * @throws ResourceNotFoundException se notificação não existe
     */
    public void marcarComoLida(Long id) {
        log.debug("Marcando notificação como lida. ID: {}", id);

        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificação não encontrada com ID: {}", id);
                    return new ResourceNotFoundException("Notificação não encontrada com ID: " + id);
                });

        notificacao.setLida(true);
        notificacao.setDataLeitura(LocalDateTime.now());
        notificacaoRepository.save(notificacao);

        log.debug("Notificação marcada como lida. ID: {}", id);
    }

    /**
     * Marca todas as notificações de um usuário como lidas.
     *
     * @param usuario usuário proprietário das notificações
     */
    public void marcarTodosComoLido(User usuario) {
        log.info("Marcando todas as notificações do usuário ID: {} como lidas", usuario.getId());

        Page<Notificacao> naoLidas = obterNaoLidas(usuario.getId(), Pageable.unpaged());
        LocalDateTime agora = LocalDateTime.now();

        naoLidas.forEach(notificacao -> {
            notificacao.setLida(true);
            notificacao.setDataLeitura(agora);
        });

        notificacaoRepository.saveAll(naoLidas.getContent());

        log.info("Todas as notificações do usuário ID: {} marcadas como lidas", usuario.getId());
    }

    /**
     * Deleta uma notificação.
     *
     * @param id ID da notificação
     * @throws ResourceNotFoundException se notificação não existe
     */
    public void deletarNotificacao(Long id) {
        log.info("Deletando notificação com ID: {}", id);

        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificação não encontrada com ID: {}", id);
                    return new ResourceNotFoundException("Notificação não encontrada com ID: " + id);
                });

        notificacaoRepository.delete(notificacao);

        log.info("Notificação deletada com sucesso. ID: {}", id);
    }

    /**
     * Conta o número de notificações não lidas de um usuário.
     *
     * @param usuarioId ID do usuário
     * @return quantidade de notificações não lidas
     */
    @Transactional(readOnly = true)
    public Long contarNaoLidas(Long usuarioId) {
        log.debug("Contando notificações não lidas do usuário ID: {}", usuarioId);
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
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
        log.info("Iniciando limpeza de notificações com mais de {} dias", diasRetencao);

        LocalDateTime dataLimite = LocalDateTime.now().minus(diasRetencao, ChronoUnit.DAYS);

        long deletadas = notificacaoRepository.deleteByDataEnvioBefore(dataLimite);

        log.info("Limpeza de notificações concluída. {} notificações deletadas", deletadas);
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

        long deletadas = notificacaoRepository.deleteByDataEnvioBefore(dataLimite);

        log.info("Limpeza automática concluída. {} notificações deletadas", deletadas);
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

}
