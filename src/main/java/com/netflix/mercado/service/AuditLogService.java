package com.netflix.mercado.service;

import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.entity.AuditLog.TipoAcao;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsável por gerenciar logs de auditoria.
 * Implementa lógica de registro, consulta e análise de ações de usuários no sistema.
 */
@Service
@Transactional
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Registra uma ação no log de auditoria.
     *
     * @param usuario usuário que realizou a ação (pode ser null para ações do sistema)
     * @param tipoAcao tipo de ação (CREATE, UPDATE, DELETE, LOGIN, etc)
     * @param tipoEntidade tipo da entidade afetada (USER, MERCADO, etc)
     * @param idEntidade ID da entidade afetada
     * @param descricao descrição da ação
     * @return o log de auditoria criado
     */
    public AuditLog registrarAcao(User usuario, String tipoAcao, String tipoEntidade, 
                                  Long idEntidade, String descricao) {
        log.debug("Registrando ação no audit log - Tipo: {}, Entidade: {}, ID: {}", 
                  tipoAcao, tipoEntidade, idEntidade);

        // Validar dados obrigatórios
        if (tipoAcao == null || tipoAcao.isBlank()) {
            throw new ValidationException("Tipo de ação é obrigatório");
        }
        if (tipoEntidade == null || tipoEntidade.isBlank()) {
            throw new ValidationException("Tipo de entidade é obrigatório");
        }
        if (idEntidade == null) {
            throw new ValidationException("ID da entidade é obrigatório");
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(usuario);
        auditLog.setAcao(tipoAcao == null ? null : TipoAcao.valueOf(tipoAcao));
        auditLog.setTipoEntidade(tipoEntidade);
        auditLog.setIdEntidade(idEntidade);
        auditLog.setDescricao(descricao);

        auditLog = auditLogRepository.save(auditLog);

        log.debug("Ação registrada no audit log. ID: {}", auditLog.getId());
        return auditLog;
    }

    /**
     * Registra uma ação com valores anteriores e novos.
     *
     * @param usuario usuário que realizou a ação
     * @param tipoAcao tipo de ação (geralmente UPDATE)
     * @param tipoEntidade tipo da entidade afetada
     * @param idEntidade ID da entidade afetada
     * @param valoresAnteriores valores anteriores (formato: campo1=valor1, campo2=valor2)
     * @param valoresNovos valores novos (formato: campo1=valor1, campo2=valor2)
     * @return o log de auditoria criado
     */
    public AuditLog registrarAcaoComValores(User usuario, String tipoAcao, String tipoEntidade,
                                            Long idEntidade, String valoresAnteriores, String valoresNovos) {
        log.debug("Registrando ação com valores no audit log - Tipo: {}, Entidade: {}", tipoAcao, tipoEntidade);

        String descricao = String.format("Valores anteriores: %s | Valores novos: %s", 
                                         valoresAnteriores, valoresNovos);

        return registrarAcao(usuario, tipoAcao, tipoEntidade, idEntidade, descricao);
    }

    /**
     * Obtém o histórico de auditoria de um usuário com paginação.
     *
     * @param usuarioId ID do usuário
     * @param pageable informações de paginação
     * @return página de logs de auditoria
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> obterAuditoriaDoUsuario(User usuario, Pageable pageable) {
        log.debug("Buscando auditoria do usuário: {}", usuario.getEmail());

        if (usuario == null) {
            throw new ValidationException("Usuário é obrigatório");
        }

        return auditLogRepository.findByUser(usuario, pageable);
    }

    /**
     * Obtém o histórico de auditoria de uma entidade.
     *
     * @param tipoEntidade tipo da entidade
     * @param idEntidade ID da entidade
     * @return lista de logs de auditoria
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obterAuditoriaEntidade(String tipoEntidade, Long idEntidade) {
        log.debug("Buscando auditoria da entidade - Tipo: {}, ID: {}", tipoEntidade, idEntidade);

        if (tipoEntidade == null || tipoEntidade.isBlank()) {
            throw new ValidationException("Tipo de entidade é obrigatório");
        }
        if (idEntidade == null) {
            throw new ValidationException("ID da entidade é obrigatório");
        }

        return auditLogRepository.findHistoricoEntidade(tipoEntidade, idEntidade);
    }

    /**
     * Obtém logs de auditoria dentro de um período de tempo com paginação.
     *
     * @param dataInicio data e hora inicial
     * @param dataFim data e hora final
     * @param pageable informações de paginação
     * @return página de logs de auditoria
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> obterAuditoriaEntreData(LocalDateTime dataInicio, LocalDateTime dataFim, 
                                                   Pageable pageable) {
        log.debug("Buscando auditoria entre {} e {}", dataInicio, dataFim);

        if (dataInicio == null) {
            throw new ValidationException("Data inicial é obrigatória");
        }
        if (dataFim == null) {
            throw new ValidationException("Data final é obrigatória");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new ValidationException("Data inicial não pode ser após a data final");
        }

        List<AuditLog> logs = auditLogRepository.findByDataRange(dataInicio, dataFim);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), logs.size());
        List<AuditLog> pageContent = logs.subList(start, end);
        return new PageImpl<>(pageContent, pageable, logs.size());
    }

    /**
     * Obtém logs de auditoria por tipo de ação.
     *
     * @param tipoAcao tipo de ação (CREATE, UPDATE, DELETE, etc)
     * @param pageable informações de paginação
     * @return página de logs de auditoria
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> obterPorTipoAcao(String tipoAcao, Pageable pageable) {
        log.debug("Buscando auditoria por tipo de ação: {}", tipoAcao);

        if (tipoAcao == null || tipoAcao.isBlank()) {
            throw new ValidationException("Tipo de ação é obrigatório");
        }

        TipoAcao acao = TipoAcao.valueOf(tipoAcao);
        return auditLogRepository.findByAcao(acao, pageable);
    }

    /**
     * Obtém logs de auditoria por tipo de entidade.
     *
     * @param tipoEntidade tipo de entidade (USER, MERCADO, etc)
     * @param pageable informações de paginação
     * @return página de logs de auditoria
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> obterPorTipoEntidade(String tipoEntidade, Pageable pageable) {
        log.debug("Buscando auditoria por tipo de entidade: {}", tipoEntidade);

        if (tipoEntidade == null || tipoEntidade.isBlank()) {
            throw new ValidationException("Tipo de entidade é obrigatório");
        }

        return auditLogRepository.findByTipoEntidade(tipoEntidade, pageable);
    }

    /**
     * Conta o número de ações de um usuário.
     *
     * @param usuarioId ID do usuário
     * @return quantidade de ações
     */
    @Transactional(readOnly = true)
    public Long contarAcoesDoUsuario(User usuario) {
        log.debug("Contando ações do usuário: {}", usuario.getEmail());

        if (usuario == null) {
            throw new ValidationException("Usuário é obrigatório");
        }

        return auditLogRepository.countByUser(usuario);
    }

    /**
     * Conta o número de ações de um tipo específico.
     *
     * @param tipoAcao tipo de ação
     * @return quantidade de ações
     */
    @Transactional(readOnly = true)
    public Long contarAcoes(String tipoAcao) {
        log.debug("Contando ações do tipo: {}", tipoAcao);

        if (tipoAcao == null || tipoAcao.isBlank()) {
            throw new ValidationException("Tipo de ação é obrigatório");
        }

        TipoAcao acao = TipoAcao.valueOf(tipoAcao);
        return auditLogRepository.findByAcao(acao, Pageable.unpaged()).getTotalElements();
    }

    /**
     * Obtém atividades suspeitas (múltiplas ações no mesmo usuário em curto período).
     *
     * @param usuarioId ID do usuário
     * @param minutosAtrás número de minutos a procurar
     * @param minimumActions número mínimo de ações para considerar suspeita
     * @return lista de logs suspeitos
     */
    @Transactional(readOnly = true)
    public List<AuditLog> obterAtividadeSuspeita(User usuario, Integer minutosAtrás, Integer minimumActions) {
        log.debug("Analisando atividade suspeita do usuário: {} nos últimos {} minutos", usuario.getEmail(), minutosAtrás);

        if (usuario == null) {
            throw new ValidationException("Usuário é obrigatório");
        }

        LocalDateTime dataLimite = LocalDateTime.now().minusMinutes(minutosAtrás);
        LocalDateTime agora = LocalDateTime.now();

        return auditLogRepository.findByDataRange(dataLimite, agora).stream()
            .filter(log -> log.getUser().getId().equals(usuario.getId()))
            .toList();
    }

    /**
     * Obtem um relatório de atividades por tipo de ação.
     *
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return lista de tipos de ação com contagem
     */
    @Transactional(readOnly = true)
    public List<Object> obterRelatorioAtividadesPorTipo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.fine("Gerando relatório de atividades por tipo entre " + dataInicio + " e " + dataFim);

        if (dataInicio == null || dataFim == null) {
            throw new ValidationException("Datas inicial e final são obrigatórias");
        }

        return auditLogRepository.findAtividadesPorTipo(dataInicio, dataFim);
    }
    public AuditLogService() {
    }

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogRepository getAuditLogRepository() {
        return this.auditLogRepository;
    }

    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

}
