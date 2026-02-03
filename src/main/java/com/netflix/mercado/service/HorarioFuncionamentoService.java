package com.netflix.mercado.service;

import com.netflix.mercado.entity.HorarioFuncionamento;
import com.netflix.mercado.entity.HorarioFuncionamento.DiaSemana;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.HorarioFuncionamentoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.horario.CreateHorarioRequest;
import com.netflix.mercado.dto.horario.UpdateHorarioRequest;
import com.netflix.mercado.dto.horario.HorarioResponse;
import com.netflix.mercado.dto.horario.MercadoStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service responsável por gerenciar horários de funcionamento de mercados.
 * Implementa lógica de criação, atualização e consulta de horários.
 */
@Service
@Transactional
public class HorarioFuncionamentoService {

    private static final Logger log = Logger.getLogger(HorarioFuncionamentoService.class.getName());

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
        log.info("Criando horário de funcionamento para mercado ID: " + mercadoId + "");

        // Buscar mercado
        Mercado mercado = mercadoService.getMercadoEntityById(mercadoId);

        // Validar dados
        validarHorarios(request);

        HorarioFuncionamento horario = new HorarioFuncionamento();
        horario.setMercado(mercado);
        horario.setDiaSemana(DiaSemana.valueOf(request.getDiaSemana()));
        horario.setHoraAbertura(LocalTime.parse(request.getHoraAbertura()));
        horario.setHoraFechamento(LocalTime.parse(request.getHoraFechamento()));
        horario.setAberto(true);

        horario = horarioRepository.save(horario);

        log.info("Horário de funcionamento criado com sucesso. ID: " + horario.getId());
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
        log.info("Atualizando horário com ID: " + id + "");

        HorarioFuncionamento horario = horarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Horário não encontrado com ID: " + id + "");
                    return new ResourceNotFoundException("Horário não encontrado com ID: " + id);
                });

        // Validar dados se fornecidos
        if (request.getHoraAbertura() != null || request.getHoraFechamento() != null) {
            CreateHorarioRequest validationRequest = new CreateHorarioRequest();
            validationRequest.setDiaSemana(horario.getDiaSemana().name());
            validationRequest.setHoraAbertura(request.getHoraAbertura() != null ? request.getHoraAbertura() : horario.getHoraAbertura().toString());
            validationRequest.setHoraFechamento(request.getHoraFechamento() != null ? request.getHoraFechamento() : horario.getHoraFechamento().toString());
            validarHorarios(validationRequest);
        }

        // Atualizar campos (diaSemana não é alterável no update)
        if (request.getHoraAbertura() != null) {
            horario.setHoraAbertura(LocalTime.parse(request.getHoraAbertura()));
        }
        if (request.getHoraFechamento() != null) {
            horario.setHoraFechamento(LocalTime.parse(request.getHoraFechamento()));
        }
        if (request.getAberto() != null) {
            horario.setAberto(request.getAberto());
        }

        horario = horarioRepository.save(horario);

        log.info("Horário atualizado com sucesso. ID: " + id + "");
        return horario;
    }

    /**
     * Deleta um horário de funcionamento.
     *
     * @param id ID do horário
     * @throws ResourceNotFoundException se horário não existe
     */
    public void deletarHorario(Long id) {
        log.info("Deletando horário com ID: " + id + "");

        HorarioFuncionamento horario = horarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Horário não encontrado com ID: " + id + "");
                    return new ResourceNotFoundException("Horário não encontrado com ID: " + id);
                });

        horarioRepository.delete(horario);

        log.info("Horário deletado com sucesso. ID: " + id + "");
    }

    /**
     * Obtém todos os horários de um mercado.
     *
     * @param mercadoId ID do mercado
     * @return lista de horários
     */
    @Transactional(readOnly = true)
    public List<HorarioResponse> obterHorariosPorMercado(Long mercadoId) {
        log.fine("Buscando horários do mercado ID: " + mercadoId + "");

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
        log.fine("Verificando se mercado ID: " + mercadoId + " está aberto");

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek diaAtual = agora.getDayOfWeek();
        LocalTime horaAtual = agora.toLocalTime();
        HorarioFuncionamento.DiaSemana diaSemanaAtual = converterDayOfWeekParaDiaSemana(diaAtual);

        HorarioFuncionamento horario = horarioRepository.findByMercadoIdAndDiaSemana(mercadoId, diaSemanaAtual)
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
        log.fine("Buscando próxima abertura do mercado ID: " + mercadoId + "");

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximaAbertura = agora;

        // Procurar nos próximos 7 dias
        for (int i = 0; i < 7; i++) {
            DayOfWeek dia = agora.plusDays(i).getDayOfWeek();
            HorarioFuncionamento.DiaSemana diaSemana = converterDayOfWeekParaDiaSemana(dia);

            HorarioFuncionamento horario = horarioRepository.findByMercadoIdAndDiaSemana(mercadoId, diaSemana)
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
        log.fine("Buscando horários do mercado ID: " + mercadoId + " para o dia: " + diaSemana);

        // Validar que mercado existe
        mercadoService.getMercadoById(mercadoId);

        HorarioFuncionamento.DiaSemana dia = HorarioFuncionamento.DiaSemana.valueOf(diaSemana.toUpperCase());
        return horarioRepository.findByMercadoIdAndDiaSemana(mercadoId, dia)
                .map(List::of)
                .orElse(List.of());
    }

    /**
     * Valida os dados de um horário de funcionamento.
     *
     * @param request dados a validar
     * @throws ValidationException se dados inválidos
     */
    public void validarHorarios(CreateHorarioRequest request) {
        log.fine("Validando dados de horário");

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
        if (request.getHoraAbertura().compareTo(request.getHoraFechamento()) >= 0) {
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
        return HorarioResponse.from(horario);
    }
    public HorarioFuncionamentoService() {
    }

    // Aliases em inglês para compatibilidade com Controllers
    public HorarioResponse createHorario(Long mercadoId, CreateHorarioRequest request, User usuario) {
        HorarioFuncionamento horario = criarHorario(mercadoId, request);
        return HorarioResponse.from(horario);
    }

    public HorarioResponse updateHorario(Long id, UpdateHorarioRequest request, User usuario) {
        HorarioFuncionamento horario = atualizarHorario(id, request);
        return HorarioResponse.from(horario);
    }

    public void deleteHorario(Long id, User usuario) {
        deletarHorario(id);
    }

    public List<HorarioResponse> listHorarios(Long mercadoId) {
        return obterHorariosPorMercado(mercadoId);
    }

    /**
     * Converte DayOfWeek do Java para DiaSemana do sistema
     */
    private HorarioFuncionamento.DiaSemana converterDayOfWeekParaDiaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> HorarioFuncionamento.DiaSemana.SEGUNDA;
            case TUESDAY -> HorarioFuncionamento.DiaSemana.TERCA;
            case WEDNESDAY -> HorarioFuncionamento.DiaSemana.QUARTA;
            case THURSDAY -> HorarioFuncionamento.DiaSemana.QUINTA;
            case FRIDAY -> HorarioFuncionamento.DiaSemana.SEXTA;
            case SATURDAY -> HorarioFuncionamento.DiaSemana.SABADO;
            case SUNDAY -> HorarioFuncionamento.DiaSemana.DOMINGO;
        };
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

    /**
     * Obtém o status da loja (aberta/fechada)
     */
    public MercadoStatusResponse getLojaStatus(Long mercadoId) {
        Mercado mercado = mercadoService.getMercadoEntityById(mercadoId);
        
        LocalDateTime agora = LocalDateTime.now();
        LocalTime horaAtual = agora.toLocalTime();
        DayOfWeek diaAtual = agora.getDayOfWeek();
        HorarioFuncionamento.DiaSemana diaSemanaAtual = converterDayOfWeekParaDiaSemana(diaAtual);
        
        boolean isOpen = horarioRepository
                .findByMercadoIdAndDiaSemana(mercadoId, diaSemanaAtual)
                .stream()
                .anyMatch(h -> horaAtual.isAfter(h.getHoraAbertura()) && horaAtual.isBefore(h.getHoraFechamento()));
        
        List<HorarioResponse> horarios = horarioRepository
                .findByMercadoIdAndDiaSemana(mercadoId, diaSemanaAtual)
                .stream()
                .map(HorarioResponse::from)
                .toList();
        
        String mensagem = isOpen ? "Aberto - Fecha às " + (horarios.isEmpty() ? "N/A" : horarios.get(0).getHoraFechamento()) 
                                 : "Fechado";
        
        MercadoStatusResponse response = new MercadoStatusResponse();
        response.setAberto(isOpen);
        response.setHorariosHoje(horarios);
        response.setMensagem(mensagem);
        return response;
    }

    /**
     * Verifica se a loja está aberta no momento
     */
    public boolean isOpen(Long mercadoId) {
        LocalDateTime agora = LocalDateTime.now();
        LocalTime horaAtual = agora.toLocalTime();
        DayOfWeek diaAtual = agora.getDayOfWeek();
        HorarioFuncionamento.DiaSemana diaSemanaAtual = converterDayOfWeekParaDiaSemana(diaAtual);
        
        return horarioRepository
                .findByMercadoIdAndDiaSemana(mercadoId, diaSemanaAtual)
                .stream()
                .anyMatch(h -> horaAtual.isAfter(h.getHoraAbertura()) && horaAtual.isBefore(h.getHoraFechamento()));
    }

}
