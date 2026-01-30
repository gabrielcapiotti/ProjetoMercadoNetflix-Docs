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
import com.netflix.mercado.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Service responsável por gerenciar promoções de mercados.
 * Implementa lógica de criação, validação, aplicação e expiração de promoções.
 */
@Slf4j
@Service
@Transactional
public class PromocaoService {

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
        log.info("Criando promoção para mercado ID: {}", mercadoId);

        // Validar dados obrigatórios
        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            throw new ValidationException("Título da promoção é obrigatório");
        }
        if (request.getDesconto() == null || request.getDesconto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Desconto deve ser maior que zero");
        }
        if (request.getDataExpiracao() == null) {
            throw new ValidationException("Data de expiração é obrigatória");
        }

        Mercado mercado = mercadoService.getMercadoById(mercadoId);

        // Verificar autorização
        if (!isOwnerOrAdmin(usuario, mercado)) {
            log.warn("Tentativa de criação não autorizada de promoção para mercado ID: {} por usuário: {}", mercadoId, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para criar promoções neste mercado");
        }

        // Validar data de expiração
        if (request.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Data de expiração não pode ser no passado");
        }

        Promocao promocao = new Promocao();
        promocao.setMercado(mercado);
        promocao.setTitulo(request.getTitulo());
        promocao.setDescricao(request.getDescricao());
        promocao.setDesconto(request.getDesconto());
        promocao.setTipoDesconto(request.getTipoDesconto()); // PERCENTUAL ou FIXO
        promocao.setCodigoPromocional(request.getCodigoPromocional());
        promocao.setDataInicio(LocalDateTime.now());
        promocao.setDataExpiracao(request.getDataExpiracao());
        promocao.setUsosDisponiveis(request.getUsosDisponiveis());
        promocao.setUsosRealizados(0L);
        promocao.setAtiva(true);

        promocao = promocaoRepository.save(promocao);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "CREATE",
                "PROMOCAO",
                promocao.getId(),
                "Promoção criada: " + request.getTitulo(),
                LocalDateTime.now()
        ));

        log.info("Promoção criada com sucesso. ID: {}", promocao.getId());
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
        log.info("Atualizando promoção com ID: {}", id);

        Promocao promocao = obterPromocaoPorId(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(usuario, promocao.getMercado())) {
            log.warn("Tentativa de atualização não autorizada da promoção ID: {} por usuário: {}", id, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para atualizar esta promoção");
        }

        String valoresAnteriores = String.format("titulo=%s, desconto=%s", promocao.getTitulo(), promocao.getDesconto());

        // Atualizar campos
        if (request.getTitulo() != null && !request.getTitulo().isBlank()) {
            promocao.setTitulo(request.getTitulo());
        }
        if (request.getDescricao() != null) {
            promocao.setDescricao(request.getDescricao());
        }
        if (request.getDesconto() != null && request.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
            promocao.setDesconto(request.getDesconto());
        }
        if (request.getDataExpiracao() != null && request.getDataExpiracao().isAfter(LocalDateTime.now())) {
            promocao.setDataExpiracao(request.getDataExpiracao());
        }
        if (request.getAtiva() != null) {
            promocao.setAtiva(request.getAtiva());
        }

        promocao = promocaoRepository.save(promocao);

        String valoresNovos = String.format("titulo=%s, desconto=%s", promocao.getTitulo(), promocao.getDesconto());

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "UPDATE",
                "PROMOCAO",
                id,
                "Alterações: " + valoresAnteriores + " -> " + valoresNovos,
                LocalDateTime.now()
        ));

        log.info("Promoção atualizada com sucesso. ID: {}", id);
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
        log.info("Deletando promoção com ID: {}", id);

        Promocao promocao = obterPromocaoPorId(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(usuario, promocao.getMercado())) {
            log.warn("Tentativa de deleção não autorizada da promoção ID: {} por usuário: {}", id, usuario.getEmail());
            throw new UnauthorizedException("Você não tem permissão para deletar esta promoção");
        }

        promocaoRepository.delete(promocao);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "DELETE",
                "PROMOCAO",
                id,
                "Promoção deletada: " + promocao.getTitulo(),
                LocalDateTime.now()
        ));

        log.info("Promoção deletada com sucesso. ID: {}", id);
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
        log.debug("Buscando promoção com ID: {}", id);
        return promocaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Promoção não encontrada com ID: {}", id);
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
        log.debug("Buscando promoções do mercado ID: {}", mercadoId);
        mercadoService.getMercadoById(mercadoId); // Validar que mercado existe
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
        log.debug("Buscando promoções ativas");
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
        log.debug("Validando código promocional: {}", codigo);

        Promocao promocao = promocaoRepository.findByCodigoPromocional(codigo)
                .orElseThrow(() -> new ValidationException("Código promocional inválido"));

        // Verificar se está expirada
        if (promocao.getDataExpiracao().isBefore(LocalDateTime.now())) {
            log.warn("Código promocional expirado: {}", codigo);
            throw new ValidationException("Código promocional expirado");
        }

        // Verificar se já foi esgotado
        if (promocao.getUsosDisponiveis() != null && promocao.getUsosRealizados() >= promocao.getUsosDisponiveis()) {
            log.warn("Código promocional esgotado: {}", codigo);
            throw new ValidationException("Código promocional esgotado");
        }

        // Verificar se está ativo
        if (!promocao.isAtiva()) {
            log.warn("Código promocional inativo: {}", codigo);
            throw new ValidationException("Código promocional inativo");
        }

        return ValidatePromocaoResponse.builder()
                .valido(true)
                .promocaoId(promocao.getId())
                .desconto(promocao.getDesconto())
                .tipoDesconto(promocao.getTipoDesconto())
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
        log.debug("Aplicando promoção ID: {} ao valor: {}", promocaoId, valorCompra);

        Promocao promocao = obterPromocaoPorId(promocaoId);

        // Verificar disponibilidade
        verificarDisponibilidade(promocaoId);

        // Calcular desconto
        BigDecimal desconto;
        if ("PERCENTUAL".equals(promocao.getTipoDesconto())) {
            // Desconto percentual
            desconto = valorCompra.multiply(promocao.getDesconto()).divide(new BigDecimal(100));
        } else {
            // Desconto fixo
            desconto = promocao.getDesconto();
        }

        // Incrementar uso
        promocao.setUsosRealizados(promocao.getUsosRealizados() + 1);
        promocaoRepository.save(promocao);

        log.debug("Promoção aplicada. Desconto: {}", desconto);
        return desconto;
    }

    /**
     * Verifica se uma promoção está disponível para uso.
     *
     * @param promocaoId ID da promoção
     * @throws ValidationException se promoção expirou ou foi esgotada
     */
    public void verificarDisponibilidade(Long promocaoId) {
        log.debug("Verificando disponibilidade da promoção ID: {}", promocaoId);

        Promocao promocao = obterPromocaoPorId(promocaoId);

        // Verificar expiração
        if (promocao.getDataExpiracao().isBefore(LocalDateTime.now())) {
            log.warn("Promoção ID: {} expirada", promocaoId);
            throw new ValidationException("Promoção expirou");
        }

        // Verificar disponibilidade de usos
        if (promocao.getUsosDisponiveis() != null && promocao.getUsosRealizados() >= promocao.getUsosDisponiveis()) {
            log.warn("Promoção ID: {} esgotada", promocaoId);
            throw new ValidationException("Promoção foi esgotada");
        }

        // Verificar se está ativa
        if (!promocao.isAtiva()) {
            log.warn("Promoção ID: {} inativa", promocaoId);
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

        log.info("Desativação de promoções concluída. {} promoções desativadas", desativadas);
    }

    /**
     * Verifica se um usuário é proprietário ou admin.
     *
     * @param usuario usuário a verificar
     * @param mercado mercado a verificar
     * @return true se é proprietário ou admin
     */
    private boolean isOwnerOrAdmin(User usuario, Mercado mercado) {
        return usuario.getId().equals(mercado.getOwner().getId()) ||
                usuario.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
    }
}
