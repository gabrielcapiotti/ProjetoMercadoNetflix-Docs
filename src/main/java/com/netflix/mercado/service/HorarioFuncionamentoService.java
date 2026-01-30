package com.netflix.mercado.service;

import com.netflix.mercado.entity.HorarioFuncionamento;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.HorarioFuncionamentoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Service responsável por gerenciar horários de funcionamento de mercados.
 * Implementa lógica de criação, atualização e consulta de horários.
 */
@Service
@Transactional
public class HorarioFuncionamentoService {

    @Autowired
    private HorarioFuncionamentoRepository horarioRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private MercadoService mercadoService;

    /**
     * Cria um novo horário de funcionamento para um mercado.
     *
     * @param mercadoId ID do mercado
     * @param request dados do horário
     * @return o horário criado
     * @throws ValidationException se dados inválidos
     */
    public HorarioFuncionamento criarHorario(Long mercadoId, CreateHorarioRequest request) {
        log.info("Criando horário de funcionamento para mercado ID: {}", mercadoId);

        // Buscar mercado
        Mercado mercado = mercadoService.getMercadoById(mercadoId);

        // Validar dados
        validarHorarios(request);

        HorarioFuncionamento horario = new HorarioFuncionamento();
        horario.setMercado(mercado);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setHoraAbertura(request.getHoraAbertura());
        horario.setHoraFechamento(request.getHoraFechamento());
        horario.setAberto(true);

        horario = horarioRepository.save(horario);

        log.info("Horário de funcionamento criado com sucesso. ID: {}", horario.getId());
        return horario;
    }

    /**
     * Atualiza um horário de funcionamento existente.
     *
     * @param id ID do horário
     * @param request dados a atualizar
     * @return o horário atualizado
     * @throws ResourceNotFoundException se horário não existe
     * @throws ValidationException se dados inválidos
     */
    public HorarioFuncionamento atualizarHorario(Long id, UpdateHorarioRequest request) {
        log.info("Atualizando horário com ID: {}", id);

        HorarioFuncionamento horario = horarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Horário não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Horário não encontrado com ID: " + id);
                });

        // Validar dados se fornecidos
        if (request.getHoraAbertura() != null || request.getHoraFechamento() != null) {
            CreateHorarioRequest validationRequest = new CreateHorarioRequest();
            validationRequest.setDiaSemana(request.getDiaSemana() != null ? request.getDiaSemana() : horario.getDiaSemana());
            validationRequest.setHoraAbertura(request.getHoraAbertura() != null ? request.getHoraAbertura() : horario.getHoraAbertura());
            validationRequest.setHoraFechamento(request.getHoraFechamento() != null ? request.getHoraFechamento() : horario.getHoraFechamento());
            validarHorarios(validationRequest);
        }

        // Atualizar campos
        if (request.getDiaSemana() != null) {
            horario.setDiaSemana(request.getDiaSemana());
        }
        if (request.getHoraAbertura() != null) {
            horario.setHoraAbertura(request.getHoraAbertura());
        }
        if (request.getHoraFechamento() != null) {
            horario.setHoraFechamento(request.getHoraFechamento());
        }
        if (request.getAberto() != null) {
            horario.setAberto(request.getAberto());
        }

        horario = horarioRepository.save(horario);

        log.info("Horário atualizado com sucesso. ID: {}", id);
        return horario;
    }

    /**
     * Deleta um horário de funcionamento.
     *
     * @param id ID do horário
     * @throws ResourceNotFoundException se horário não existe
     */
    public void deletarHorario(Long id) {
        log.info("Deletando horário com ID: {}", id);

        HorarioFuncionamento horario = horarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Horário não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Horário não encontrado com ID: " + id);
                });

        horarioRepository.delete(horario);

        log.info("Horário deletado com sucesso. ID: {}", id);
    }

    /**
     * Obtém todos os horários de um mercado.
     *
     * @param mercadoId ID do mercado
     * @return lista de horários
     */
    @Transactional(readOnly = true)
    public List<HorarioResponse> obterHorariosPorMercado(Long mercadoId) {
        log.debug("Buscando horários do mercado ID: {}", mercadoId);

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        List<HorarioFuncionamento> horarios = horarioRepository.findByMercadoId(mercadoId);

        return horarios.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Verifica se um mercado está aberto no momento.
     *
     * @param mercadoId ID do mercado
     * @return true se está aberto
     */
    @Transactional(readOnly = true)
    public Boolean verificarSeEstaAberto(Long mercadoId) {
        log.debug("Verificando se mercado ID: {} está aberto", mercadoId);

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek diaAtual = agora.getDayOfWeek();
        LocalTime horaAtual = agora.toLocalTime();

        HorarioFuncionamento horario = horarioRepository.findByMercadoIdAndDiaSemana(mercadoId, diaAtual.toString())
                .orElse(null);

        if (horario == null || !horario.isAberto()) {
            return false;
        }

        return horaAtual.isAfter(horario.getHoraAbertura()) &&
                horaAtual.isBefore(horario.getHoraFechamento());
    }

    /**
     * Obtém a próxima data e hora de abertura de um mercado.
     *
     * @param mercadoId ID do mercado
     * @return LocalDateTime da próxima abertura
     */
    @Transactional(readOnly = true)
    public LocalDateTime obterProximaAbertura(Long mercadoId) {
        log.debug("Buscando próxima abertura do mercado ID: {}", mercadoId);

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximaAbertura = agora;

        // Procurar nos próximos 7 dias
        for (int i = 0; i < 7; i++) {
            DayOfWeek dia = agora.plusDays(i).getDayOfWeek();

            HorarioFuncionamento horario = horarioRepository.findByMercadoIdAndDiaSemana(mercadoId, dia.toString())
                    .orElse(null);

            if (horario != null && horario.isAberto()) {
                proximaAbertura = agora.plusDays(i).with(horario.getHoraAbertura());
                break;
            }
        }

        return proximaAbertura;
    }

    /**
     * Obtém os horários de um dia específico.
     *
     * @param mercadoId ID do mercado
     * @param diaSemana dia da semana
     * @return lista de horários do dia
     */
    @Transactional(readOnly = true)
    public List<HorarioFuncionamento> obterHorariosDia(Long mercadoId, String diaSemana) {
        log.debug("Buscando horários do mercado ID: {} para o dia: {}", mercadoId, diaSemana);

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        return horarioRepository.findByMercadoIdAndDiaSemana(mercadoId, diaSemana)
                .stream()
                .toList();
    }

    /**
     * Valida os dados de um horário de funcionamento.
     *
     * @param request dados a validar
     * @throws ValidationException se dados inválidos
     */
    public void validarHorarios(CreateHorarioRequest request) {
        log.debug("Validando dados de horário");

        // Validar dia da semana
        if (request.getDiaSemana() == null || request.getDiaSemana().isBlank()) {
            throw new ValidationException("Dia da semana é obrigatório");
        }

        // Validar se dia é válido
        try {
            DayOfWeek.valueOf(request.getDiaSemana().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Dia da semana inválido: " + request.getDiaSemana());
        }

        // Validar horários
        if (request.getHoraAbertura() == null) {
            throw new ValidationException("Hora de abertura é obrigatória");
        }
        if (request.getHoraFechamento() == null) {
            throw new ValidationException("Hora de fechamento é obrigatória");
        }

        // Validar que abertura é antes do fechamento
        if (!request.getHoraAbertura().isBefore(request.getHoraFechamento())) {
            throw new ValidationException("Hora de abertura deve ser anterior à hora de fechamento");
        }
    }

    /**
     * Converte entidade HorarioFuncionamento para DTO HorarioResponse.
     *
     * @param horario entidade HorarioFuncionamento
     * @return DTO HorarioResponse
     */
    private HorarioResponse convertToResponse(HorarioFuncionamento horario) {
        return new HorarioResponse(
                horario.getId(),
                horario.getDiaSemana(),
                horario.getHoraAbertura(),
                horario.getHoraFechamento(),
                horario.isAberto()
        );
    }
    public HorarioFuncionamentoService() {
    }

    public HorarioFuncionamentoService(HorarioFuncionamentoRepository horarioRepository, AuditLogRepository auditLogRepository, MercadoService mercadoService) {
        this.horarioRepository = horarioRepository;
        this.auditLogRepository = auditLogRepository;
        this.mercadoService = mercadoService;
    }

    public HorarioFuncionamentoRepository getHorarioRepository() {
        return this.horarioRepository;
    }

    public void setHorarioRepository(HorarioFuncionamentoRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
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

}
