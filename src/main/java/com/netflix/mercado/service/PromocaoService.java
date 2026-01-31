package com.netflix.mercado.service;

import com.netflix.mercado.entity.Promocao;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.exception.UnauthorizedException;
import com.netflix.mercado.repository.PromocaoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.promocao.CreatePromocaoRequest;
import com.netflix.mercado.dto.promocao.UpdatePromocaoRequest;
import com.netflix.mercado.dto.promocao.PromocaoResponse;
import com.netflix.mercado.dto.promocao.ValidatePromocaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

/**
 * Service responsável por gerenciar promoções de mercados.
 * Implementa lógica de criação, validação, aplicação e expiração de promoções.
 */
@Service
@Transactional
public class PromocaoService {

    private static final Logger log = Logger.getLogger(PromocaoService.class.getName());

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private MercadoService mercadoService;

    /**
     * Cria uma nova promoção para um mercado.
     *
     * @param request dados da promoção
     * @param mercadoId ID do mercado
     * @param usuario usuário criando a promoção
     * @return a promoção criada
     * @throws ValidationException se dados inválidos
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public Promocao criarPromocao(CreatePromocaoRequest request, Long mercadoId, User usuario) {
        log.info("Criando promoção para mercado ID: " + mercadoId);

        // Validar dados obrigatórios
        if (request.getCodigo() == null || request.getCodigo().isBlank()) {
            throw new ValidationException("Código da promoção é obrigatório");
        }
        if (request.getPercentualDesconto() == null || request.getPercentualDesconto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Desconto deve ser maior que zero");
        }
        if (request.getDataValidade() == null) {
            throw new ValidationException("Data de validade é obrigatória");
        }

        Mercado mercado = mercadoService.getMercadoEntityById(mercadoId);

        // Verificar autorização
        if (!isOwnerOrAdmin(usuario, mercado)) {
            log.warning("Tentativa de criação não autorizada de promoção para mercado ID: " + mercadoId + " por usuário: " + usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para criar promoções neste mercado");
        }

        // Validar data de validade
        if (request.getDataValidade().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Data de validade não pode ser no passado");
        }

        Promocao promocao = new Promocao();
        promocao.setMercado(mercado);
        promocao.setCodigo(request.getCodigo());
        promocao.setDescricao(request.getDescricao());
        promocao.setPercentualDesconto(request.getPercentualDesconto());
        promocao.setValorDescontoMaximo(request.getValorDescontoMaximo());
        promocao.setValorMinimoCompra(request.getValorMinimoCompra());
        promocao.setDataInicio(request.getDataInicio());
        promocao.setDataValidade(request.getDataValidade());
        promocao.setAtiva(true);

        promocao = promocaoRepository.save(promocao);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                usuario,
                AuditLog.TipoAcao.CRIACAO,
                "PROMOCAO",
                promocao.getId(),
                "Promoção criada: " + request.getCodigo(),
                null,
                null,
                null,
                null,
                200
        ));

        log.info("Promoção criada com sucesso. ID: " + promocao.getId());
        return promocao;
    }

    /**
     * Atualiza uma promoção existente.
     *
     * @param id ID da promoção
     * @param request dados a atualizar
     * @param usuario usuário realizando a atualização
     * @return a promoção atualizada
     * @throws ResourceNotFoundException se promoção não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public Promocao atualizarPromocao(Long id, UpdatePromocaoRequest request, User usuario) {
        log.info("Atualizando promoção com ID: " + id);

        Promocao promocao = obterPromocaoPorId(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(usuario, promocao.getMercado())) {
            log.warning("Tentativa de atualização não autorizada da promoção ID: " + id + " por usuário: " + usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para atualizar esta promoção");
        }

        String valoresAnteriores = String.format("codigo=%s, desconto=%s", promocao.getCodigo(), promocao.getPercentualDesconto());

        // Atualizar campos
        if (request.getDescricao() != null) {
            promocao.setDescricao(request.getDescricao());
        }
        if (request.getPercentualDesconto() != null && request.getPercentualDesconto().compareTo(BigDecimal.ZERO) > 0) {
            promocao.setPercentualDesconto(request.getPercentualDesconto());
        }
        if (request.getDataValidade() != null && request.getDataValidade().isAfter(LocalDateTime.now())) {
            promocao.setDataValidade(request.getDataValidade());
        }
        if (request.getValorDescontoMaximo() != null) {
            promocao.setValorDescontoMaximo(request.getValorDescontoMaximo());
        }
        if (request.getValorMinimoCompra() != null) {
            promocao.setValorMinimoCompra(request.getValorMinimoCompra());
        }

        promocao = promocaoRepository.save(promocao);

        String valoresNovos = String.format("codigo=%s, desconto=%s", promocao.getCodigo(), promocao.getPercentualDesconto());

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                usuario,
                AuditLog.TipoAcao.ATUALIZACAO,
                "PROMOCAO",
                id,
                "Alterações: " + valoresAnteriores + " -> " + valoresNovos,
                valoresAnteriores,
                valoresNovos,
                null,
                null,
                200
        ));

        log.info("Promoção atualizada com sucesso. ID: " + id);
        return promocao;
    }

    /**
     * Deleta uma promoção.
     *
     * @param id ID da promoção
     * @param usuario usuário realizando a deleção
     * @throws ResourceNotFoundException se promoção não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public void deletarPromocao(Long id, User usuario) {
        log.info("Deletando promoção com ID: " + id);

        Promocao promocao = obterPromocaoPorId(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(usuario, promocao.getMercado())) {
            log.warning("Tentativa de deleção não autorizada da promoção ID: " + id + " por usuário: " + usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para deletar esta promoção");
        }

        promocaoRepository.delete(promocao);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                usuario,
                AuditLog.TipoAcao.DELECAO,
                "PROMOCAO",
                id,
                "Promoção deletada: " + promocao.getCodigo(),
                null,
                null,
                null,
                null,
                200
        ));

        log.info("Promoção deletada com sucesso. ID: " + id);
    }

    /**
     * Obtém uma promoção pelo ID.
     *
     * @param id ID da promoção
     * @return a promoção encontrada
     * @throws ResourceNotFoundException se promoção não existe
     */
    @Transactional(readOnly = true)
    public Promocao obterPromocaoPorId(Long id) {
        log.fine("Buscando promoção com ID: " + id);
        return promocaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Promoção não encontrada com ID: " + id);
                    return new ResourceNotFoundException("Promoção não encontrada com ID: " + id);
                });
    }

    /**
     * Obtém todas as promoções de um mercado com paginação.
     *
     * @param mercadoId ID do mercado
     * @param pageable informações de paginação
     * @return página de promoções
     */
    @Transactional(readOnly = true)
    public Page<Promocao> obterPromocoesDoMercado(Long mercadoId, Pageable pageable) {
        log.fine("Buscando promoções do mercado ID: " + mercadoId);
        mercadoService.getMercadoEntityById(mercadoId); // Validar que mercado existe
        return promocaoRepository.findByMercadoId(mercadoId, pageable);
    }

    /**
     * Obtém todas as promoções ativas com paginação.
     *
     * @param pageable informações de paginação
     * @return página de promoções ativas
     */
    @Transactional(readOnly = true)
    public Page<Promocao> obterPromocoesAtivas(Pageable pageable) {
        log.fine("Buscando promoções ativas");
        return promocaoRepository.findByAtivaTrue(pageable);
    }

    /**
     * Valida um código promocional.
     *
     * @param codigo código a validar
     * @return resposta de validação
     * @throws ValidationException se código inválido
     */
    @Transactional(readOnly = true)
    public ValidatePromocaoResponse validarCodigo(String codigo) {
        log.fine("Validando código promocional: " + codigo);

        Promocao promocao = promocaoRepository.findByCodigoPromocional(codigo)
                .orElseThrow(() -> new ValidationException("Código promocional inválido"));

        // Verificar se está expirada
        if (promocao.getDataValidade().isBefore(LocalDateTime.now())) {
            log.warning("Código promocional expirado: " + codigo);
            throw new ValidationException("Código promocional expirado");
        }

        // Verificar se já foi esgotado
        if (promocao.getMaxUtilizacoes() != null && promocao.getUtilizacoesAtuais() >= promocao.getMaxUtilizacoes()) {
            log.warning("Código promocional esgotado: " + codigo);
            throw new ValidationException("Código promocional esgotado");
        }

        // Verificar se está ativo
        if (!promocao.getAtiva()) {
            log.warning("Código promocional inativo: " + codigo);
            throw new ValidationException("Código promocional inativo");
        }

        return ValidatePromocaoResponse.builder()
                .valida(true)
                .desconto(promocao.getPercentualDesconto())
                .mensagem("Promoção válida")
                .utilizacoesRestantes(promocao.getMaxUtilizacoes() - promocao.getUtilizacoesAtuais())
                .build();
    }

    /**
     * Aplica um desconto de promoção a um valor de compra.
     *
     * @param promocaoId ID da promoção
     * @param valorCompra valor da compra
     * @return valor do desconto
     * @throws ResourceNotFoundException se promoção não existe
     * @throws ValidationException se promoção não pode ser aplicada
     */
    public BigDecimal aplicarPromocao(Long promocaoId, BigDecimal valorCompra) {
        log.fine("Aplicando promoção ID: " + promocaoId + " ao valor: " + valorCompra);

        Promocao promocao = obterPromocaoPorId(promocaoId);

        // Verificar disponibilidade
        verificarDisponibilidade(promocaoId);

        // Calcular desconto percentual
        BigDecimal desconto = valorCompra.multiply(promocao.getPercentualDesconto()).divide(new BigDecimal(100));

        // Incrementar uso
        promocao.setUtilizacoesAtuais(promocao.getUtilizacoesAtuais() + 1);
        promocaoRepository.save(promocao);

        log.fine("Promoção aplicada. Desconto: " + desconto);
        return desconto;
    }

    /**
     * Verifica se uma promoção está disponível para uso.
     *
     * @param promocaoId ID da promoção
     * @throws ValidationException se promoção expirou ou foi esgotada
     */
    public void verificarDisponibilidade(Long promocaoId) {
        log.fine("Verificando disponibilidade da promoção ID: " + promocaoId);

        Promocao promocao = obterPromocaoPorId(promocaoId);

        // Verificar expiração
        if (promocao.getDataValidade().isBefore(LocalDateTime.now())) {
            log.warning("Promoção ID: " + promocaoId + " expirada");
            throw new ValidationException("Promoção expirou");
        }

        // Verificar disponibilidade de usos
        if (promocao.getMaxUtilizacoes() != null && promocao.getUtilizacoesAtuais() >= promocao.getMaxUtilizacoes()) {
            log.warning("Promoção ID: " + promocaoId + " esgotada");
            throw new ValidationException("Promoção foi esgotada");
        }

        // Verificar se está ativa
        if (!promocao.getAtiva()) {
            log.warning("Promoção ID: " + promocaoId + " inativa");
            throw new ValidationException("Promoção não está ativa");
        }
    }

    /**
     * Desativa promoções expiradas (executado periodicamente).
     */
    @Scheduled(cron = "0 30 2 * * *") // Executar diariamente às 2:30 da manhã
    @Transactional
    public void desativarPromocoesExpiradas() {
        log.info("Iniciando desativação de promoções expiradas");

        long desativadas = promocaoRepository.desativarPromocoesExpiradas(LocalDateTime.now());

        log.info("Desativação de promoções concluída. " + desativadas + " promoções desativadas");
    }

    /**
     * Verifica se um usuário é proprietário ou admin.
     *
     * @param usuario usuário a verificar
     * @param mercado mercado a verificar
     * @return true se é proprietário ou admin
     */
    private boolean isOwnerOrAdmin(User usuario, Mercado mercado) {
        // TODO: Mercado não tem owner - implementar validação correta
        return usuario.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
    }

    // Aliases em inglês para compatibilidade com Controllers
    public Promocao createPromocao(Long mercadoId, CreatePromocaoRequest request, User usuario) {
        return criarPromocao(request, mercadoId, usuario);
    }

    public Promocao updatePromocao(Long id, UpdatePromocaoRequest request, User usuario) {
        return atualizarPromocao(id, request, usuario);
    }

    public void deletePromocao(Long id, User usuario) {
        deletarPromocao(id, usuario);
    }

    public Promocao getPromocaoById(Long id) {
        return obterPromocaoPorId(id);
    }

    public Page<Promocao> listPromocoesByMercado(Long mercadoId, Boolean ativa, Pageable pageable) {
        if (ativa != null && ativa) {
            return obterPromocoesAtivas(pageable);
        }
        return obterPromocoesDoMercado(mercadoId, pageable);
    }

    public ValidatePromocaoResponse validatePromoCode(String codigo) {
        return validarCodigo(codigo);
    }

    public void applyPromocao(Long promocaoId, User usuario) {
        Promocao promocao = obterPromocaoPorId(promocaoId);
        promocao.setUtilizacoesAtuais(promocao.getUtilizacoesAtuais() + 1);
        promocaoRepository.save(promocao);
    }

    public PromocaoService() {
    }

    public PromocaoService(PromocaoRepository promocaoRepository, AuditLogRepository auditLogRepository, MercadoService mercadoService) {
        this.promocaoRepository = promocaoRepository;
        this.auditLogRepository = auditLogRepository;
        this.mercadoService = mercadoService;
    }

    public PromocaoRepository getPromocaoRepository() {
        return this.promocaoRepository;
    }

    public void setPromocaoRepository(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = promocaoRepository;
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
