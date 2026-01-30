package com.netflix.mercado.service;

import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsável por gerenciar logs de auditoria.
 * Implementa lógica de registro, consulta e análise de ações de usuários no sistema.
 */
@Service
@Transactional
public class AuditLogService {

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
        auditLog.setUsuario(usuario);
        auditLog.setTipoAcao(tipoAcao);
        auditLog.setTipoEntidade(tipoEntidade);
        auditLog.setIdEntidade(idEntidade);
        auditLog.setDescricao(descricao);
        auditLog.setDataHora(LocalDateTime.now());

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
    public Page<AuditLog> obterAuditoriaDoUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando auditoria do usuário ID: {}", usuarioId);

        if (usuarioId == null) {
            throw new ValidationException("ID do usuário é obrigatório");
        }

        return auditLogRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId, pageable);
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

        return auditLogRepository.findByTipoEntidadeAndIdEntidadeOrderByDataHoraDesc(tipoEntidade, idEntidade);
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

        return auditLogRepository.findByDataHoraBetweenOrderByDataHoraDesc(dataInicio, dataFim, pageable);
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

        return auditLogRepository.findByTipoAcaoOrderByDataHoraDesc(tipoAcao, pageable);
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

        return auditLogRepository.findByTipoEntidadeOrderByDataHoraDesc(tipoEntidade, pageable);
    }

    /**
     * Conta o número de ações de um usuário.
     *
     * @param usuarioId ID do usuário
     * @return quantidade de ações
     */
    @Transactional(readOnly = true)
    public Long contarAcoesDoUsuario(Long usuarioId) {
        log.debug("Contando ações do usuário ID: {}", usuarioId);

        if (usuarioId == null) {
            throw new ValidationException("ID do usuário é obrigatório");
        }

        return auditLogRepository.countByUsuarioId(usuarioId);
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

        return auditLogRepository.countByTipoAcao(tipoAcao);
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
    public List<AuditLog> obterAtividadeSuspeita(Long usuarioId, Integer minutosAtrás, Integer minimumActions) {
        log.debug("Analisando atividade suspeita do usuário ID: {} nos últimos {} minutos", usuarioId, minutosAtrás);

        if (usuarioId == null) {
            throw new ValidationException("ID do usuário é obrigatório");
        }

        LocalDateTime dataLimite = LocalDateTime.now().minusMinutes(minutosAtrás);

        return auditLogRepository.findByUsuarioIdAndDataHoraAfterOrderByDataHoraDesc(usuarioId, dataLimite);
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
        log.debug("Gerando relatório de atividades por tipo entre {} e {}", dataInicio, dataFim);

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
