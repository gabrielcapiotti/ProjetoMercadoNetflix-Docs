package com.netflix.mercado.service;

import com.netflix.mercado.dto.promocao.AplicarPromocaoRequest;
import com.netflix.mercado.dto.promocao.AplicarPromocaoResponse;
import com.netflix.mercado.entity.Promocao;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.repository.PromocaoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * ✅ NOVO: Serviço para aplicação e cálculo de promoções
 * Gerencia descontos, validações e cálculos de valores finais
 */
@Service
@Transactional
public class AplicacaoPromocaoService {

    @Autowired
    private PromocaoRepository promocaoRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * ✅ NOVO: Aplica uma promoção a um valor de compra
     * Valida a promoção e calcula o desconto
     *
     * @param request dados da aplicação (código, valor)
     * @param usuario usuário aplicando a promoção
     * @return resposta com valor final e economia
     * @throws ValidationException se promoção inválida
     */
    public AplicarPromocaoResponse aplicarPromocao(AplicarPromocaoRequest request, User usuario) {
        // Validar entrada
        if (request.getCodigoPromocao() == null || request.getCodigoPromocao().isBlank()) {
            throw new ValidationException("Código da promoção é obrigatório");
        }

        if (request.getValorCompra() == null || request.getValorCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Valor da compra deve ser maior que zero");
        }

        // Buscar promoção
        var promocao = promocaoRepository.findByCodigo(request.getCodigoPromocao())
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada: " + request.getCodigoPromocao()));

        // Validar promoção
        validarPromocaoParaAplicacao(promocao);

        // Verificar limite de utilização
        if (!podeUtilizarPromocao(promocao)) {
            throw new ValidationException("Promoção atingiu o limite de utilizações");
        }

        // Verificar compra mínima
        if (promocao.getValorMinimoCompra() != null && 
            request.getValorCompra().compareTo(promocao.getValorMinimoCompra()) < 0) {
            throw new ValidationException(
                "Valor mínimo para usar esta promoção: R$ " + promocao.getValorMinimoCompra()
            );
        }

        // Calcular desconto
        BigDecimal desconto = calcularDesconto(request.getValorCompra(), promocao);
        BigDecimal valorFinal = request.getValorCompra().subtract(desconto);

        // Validar limite de desconto máximo
        if (promocao.getValorDescontoMaximo() != null && 
            desconto.compareTo(promocao.getValorDescontoMaximo()) > 0) {
            desconto = promocao.getValorDescontoMaximo();
            valorFinal = request.getValorCompra().subtract(desconto);
        }

        // Registrar utilização (incrementa contador)
        incrementarUtilizacao(promocao);

        // Registrar na auditoria
        registrarUtilizacaoAuditoria(promocao, usuario, request.getValorCompra(), desconto);

        return AplicarPromocaoResponse.builder()
                .promocaoId(promocao.getId())
                .codigoPromocao(promocao.getCodigo())
                .valorOriginal(request.getValorCompra())
                .desconto(desconto)
                .percentualDesconto(promocao.getPercentualDesconto())
                .valorFinal(valorFinal)
                .economia(calcularPercentualEconomia(request.getValorCompra(), desconto))
                .dataExpiracao(promocao.getDataValidade())
                .utilizacaoRestante(promocao.getMaxUtilizacoes() - promocao.getUtilizacoesAtuais())
                .build();
    }

    /**
     * ✅ NOVO: Calcula o desconto com base no percentual
     *
     * @param valor valor base para desconto
     * @param promocao promoção com percentual
     * @return valor do desconto calculado
     */
    private BigDecimal calcularDesconto(BigDecimal valor, Promocao promocao) {
        if (promocao.getPercentualDesconto() == null) {
            return BigDecimal.ZERO;
        }

        // Desconto = valor * (percentual / 100)
        BigDecimal desconto = valor.multiply(promocao.getPercentualDesconto())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        return desconto;
    }

    /**
     * ✅ NOVO: Calcula o percentual de economia
     *
     * @param valorOriginal valor antes do desconto
     * @param desconto valor do desconto
     * @return percentual de economia
     */
    private BigDecimal calcularPercentualEconomia(BigDecimal valorOriginal, BigDecimal desconto) {
        if (valorOriginal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return desconto.multiply(new BigDecimal("100"))
                .divide(valorOriginal, 2, RoundingMode.HALF_UP);
    }

    /**
     * ✅ NOVO: Valida se promoção pode ser usada
     *
     * @param promocao promoção a validar
     * @throws ValidationException se promoção inválida
     */
    private void validarPromocaoParaAplicacao(Promocao promocao) {
        // Verificar se está ativa
        if (!promocao.getAtiva()) {
            throw new ValidationException("Promoção está desativada");
        }

        // Verificar se expirou
        if (promocao.getDataValidade().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Promoção expirou em " + promocao.getDataValidade());
        }

        // Verificar se já começou
        if (promocao.getDataInicio() != null && 
            promocao.getDataInicio().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Promoção começa em " + promocao.getDataInicio());
        }

        // Verificar se está ativa no banco
        if (!promocao.getActive()) {
            throw new ValidationException("Promoção foi removida do sistema");
        }
    }

    /**
     * ✅ NOVO: Verifica se a promoção ainda pode ser utilizada
     *
     * @param promocao promoção a verificar
     * @return true se pode utilizar, false caso contrário
     */
    private boolean podeUtilizarPromocao(Promocao promocao) {
        if (promocao.getMaxUtilizacoes() == null) {
            return true; // Sem limite
        }

        return promocao.getUtilizacoesAtuais() < promocao.getMaxUtilizacoes();
    }

    /**
     * ✅ NOVO: Incrementa o contador de utilizações da promoção
     *
     * @param promocao promoção a incrementar
     */
    @Transactional
    private void incrementarUtilizacao(Promocao promocao) {
        promocao.setUtilizacoesAtuais(promocao.getUtilizacoesAtuais() + 1);
        promocaoRepository.save(promocao);
    }

    /**
     * ✅ NOVO: Registra a utilização da promoção na auditoria
     *
     * @param promocao promoção utilizada
     * @param usuario usuário que utilizou
     * @param valorCompra valor original da compra
     * @param valorDesconto valor do desconto aplicado
     */
    @Transactional
    private void registrarUtilizacaoAuditoria(Promocao promocao, User usuario, BigDecimal valorCompra, BigDecimal valorDesconto) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(usuario);
        auditLog.setAcao(AuditLog.TipoAcao.CRIACAO);
        auditLog.setTipoEntidade("Promocao");
        auditLog.setIdEntidade(promocao.getId());
        auditLog.setDescricao("Promoção '" + promocao.getCodigo() + "' utilizada. " +
                "Valor compra: R$ " + valorCompra + " | Desconto: R$ " + valorDesconto);

        auditLogRepository.save(auditLog);
    }

    /**
     * ✅ NOVO: Calcula múltiplas promoções (caso o usuário tenha acesso)
     * Retorna a que oferece maior desconto
     *
     * @param codigosPromocoes lista de códigos
     * @param valorCompra valor base
     * @return resposta com melhor promoção
     */
    public AplicarPromocaoResponse compararPromocoes(java.util.List<String> codigosPromocoes, BigDecimal valorCompra) {
        AplicarPromocaoResponse melhorOpcao = null;
        BigDecimal maiorEconomia = BigDecimal.ZERO;

        for (String codigo : codigosPromocoes) {
            try {
                AplicarPromocaoRequest request = new AplicarPromocaoRequest();
                request.setCodigoPromocao(codigo);
                request.setValorCompra(valorCompra);

                // Apenas simular, sem aplicar de verdade
                var promocao = promocaoRepository.findByCodigo(codigo)
                        .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada"));

                if (!promocao.getAtiva()) continue;
                if (promocao.getDataValidade().isBefore(LocalDateTime.now())) continue;

                BigDecimal desconto = calcularDesconto(valorCompra, promocao);

                if (desconto.compareTo(maiorEconomia) > 0) {
                    maiorEconomia = desconto;
                    melhorOpcao = AplicarPromocaoResponse.builder()
                            .promocaoId(promocao.getId())
                            .codigoPromocao(promocao.getCodigo())
                            .valorOriginal(valorCompra)
                            .desconto(desconto)
                            .percentualDesconto(promocao.getPercentualDesconto())
                            .valorFinal(valorCompra.subtract(desconto))
                            .economia(calcularPercentualEconomia(valorCompra, desconto))
                            .dataExpiracao(promocao.getDataValidade())
                            .build();
                }
            } catch (Exception e) {
                // Ignorar promoções inválidas
            }
        }

        if (melhorOpcao == null) {
            throw new ValidationException("Nenhuma promoção válida encontrada");
        }

        return melhorOpcao;
    }
}
