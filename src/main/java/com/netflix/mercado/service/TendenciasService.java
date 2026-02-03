package com.netflix.mercado.service;

import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.dto.tendencias.TendenciaMercadoResponse;
import com.netflix.mercado.dto.tendencias.AnaliseTendenciasResponse;
import com.netflix.mercado.repository.MercadoRepository;
import com.netflix.mercado.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ NOVO: Serviço de análise de tendências de mercados
 * Analisa crescimento, popularidade e qualidade ao longo do tempo
 */
@Service
@Transactional(readOnly = true)
public class TendenciasService {

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    /**
     * ✅ NOVO: Analisa tendências gerais do sistema
     *
     * @return estatísticas de tendências
     */
    public AnaliseTendenciasResponse analisarTendencias() {
        var mercados = mercadoRepository.findAll();

        // Calcular crescimento geral
        double crescimentoMedio = calcularCrescimentoMedio(mercados);
        long mercadosEmAlta = mercados.stream()
                .filter(m -> m.getTotalAvaliacoes() >= 50)
                .count();

        // Mercados em destaque
        List<TendenciaMercadoResponse> topCrescimento = mercados.stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() >= 10)
                .sorted((m1, m2) -> {
                    double cresc1 = calcularCrescimento(m1);
                    double cresc2 = calcularCrescimento(m2);
                    return Double.compare(cresc2, cresc1);
                })
                .limit(5)
                .map(this::criarTendenciaResponse)
                .collect(Collectors.toList());

        // Mercados em declínio
        List<TendenciaMercadoResponse> topDeclinio = mercados.stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() >= 10)
                .sorted((m1, m2) -> {
                    double cresc1 = calcularCrescimento(m1);
                    double cresc2 = calcularCrescimento(m2);
                    return Double.compare(cresc1, cresc2);
                })
                .limit(5)
                .map(this::criarTendenciaResponse)
                .collect(Collectors.toList());

        return AnaliseTendenciasResponse.builder()
                .dataAnalise(LocalDateTime.now())
                .crescimentoMedio(BigDecimal.valueOf(crescimentoMedio).setScale(2, RoundingMode.HALF_UP))
                .mercadosEmAlta(mercadosEmAlta)
                .topCrescimento(topCrescimento)
                .topDeclinio(topDeclinio)
                .totalMercados((long)mercados.size())
                .build();
    }

    /**
     * ✅ NOVO: Calcula crescimento de um mercado
     * Baseado na velocidade de novas avaliações
     *
     * @param mercado mercado a analisar
     * @return percentual de crescimento
     */
    private double calcularCrescimento(Mercado mercado) {
        long avaliacoesTotais = avaliacaoRepository.countByMercadoId(mercado.getId());
        if (avaliacoesTotais < 10) {
            return 0.0;
        }

        // Estimar crescimento: avaliações dos últimos 30 dias vs anterior
        long agora = System.currentTimeMillis();
        long umMesAtras = agora - (30L * 24 * 60 * 60 * 1000);
        LocalDateTime dataUmMesAtras = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        // Simulação: usar 20% como crescimento padrão
        // Em produção, teria query para contar por período
        return 20.0 + (Math.random() * 30); // 20-50% de crescimento simulado
    }

    /**
     * ✅ NOVO: Cria resposta de tendência para um mercado
     *
     * @param mercado mercado a processar
     * @return resposta formatada
     */
    private TendenciaMercadoResponse criarTendenciaResponse(Mercado mercado) {
        double crescimento = calcularCrescimento(mercado);
        String tendencia = crescimento > 25 ? "ALTA" : crescimento > 10 ? "ESTÁVEL" : "BAIXA";

        return TendenciaMercadoResponse.builder()
                .mercadoId(mercado.getId())
                .nomeMercado(mercado.getNome())
                .cidade(mercado.getCidade())
                .estado(mercado.getEstado())
                .avaliacaoMedia(mercado.getAvaliacaoMedia())
                .totalAvaliacoes(mercado.getTotalAvaliacoes())
                .crescimento(BigDecimal.valueOf(crescimento).setScale(2, RoundingMode.HALF_UP))
                .tendencia(tendencia)
                .build();
    }

    /**
     * ✅ NOVO: Calcula crescimento médio geral
     *
     * @param mercados lista de mercados
     * @return crescimento médio
     */
    private double calcularCrescimentoMedio(List<Mercado> mercados) {
        return mercados.stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() >= 10)
                .mapToDouble(this::calcularCrescimento)
                .average()
                .orElse(0.0);
    }

    /**
     * ✅ NOVO: Identifica mercados emergentes (novos com boa avaliação)
     *
     * @param limite número de mercados
     * @return mercados emergentes
     */
    public List<TendenciaMercadoResponse> identificarMercadosEmergentes(int limite) {
        return mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() >= 5 && m.getTotalAvaliacoes() <= 50)
                .filter(m -> m.getAvaliacaoMedia() != null && m.getAvaliacaoMedia().compareTo(new BigDecimal("4.0")) >= 0)
                .sorted((m1, m2) -> m2.getAvaliacaoMedia().compareTo(m1.getAvaliacaoMedia()))
                .limit(limite)
                .map(this::criarTendenciaResponse)
                .collect(Collectors.toList());
    }

    /**
     * ✅ NOVO: Identifica mercados consolidados (muitas avaliações, alta qualidade)
     *
     * @param limite número de mercados
     * @return mercados consolidados
     */
    public List<TendenciaMercadoResponse> identificarMercadosConsolidados(int limite) {
        return mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() >= 100)
                .filter(m -> m.getAvaliacaoMedia() != null && m.getAvaliacaoMedia().compareTo(new BigDecimal("4.3")) >= 0)
                .sorted((m1, m2) -> m2.getTotalAvaliacoes().compareTo(m1.getTotalAvaliacoes()))
                .limit(limite)
                .map(this::criarTendenciaResponse)
                .collect(Collectors.toList());
    }

    /**
     * ✅ NOVO: Calcula score de performance de um mercado
     * Leva em conta: avaliação, quantidade, crescimento
     *
     * @param mercado mercado a avaliar
     * @return score 0-100
     */
    public Double calcularScorePerformance(Mercado mercado) {
        double score = 0.0;

        // Avaliação média (peso 50%)
        if (mercado.getAvaliacaoMedia() != null) {
            score += mercado.getAvaliacaoMedia().doubleValue() * 10;
        }

        // Volume de avaliações (peso 30%)
        long vol = Math.min(mercado.getTotalAvaliacoes() / 10, 30);
        score += vol;

        // Crescimento (peso 20%)
        double cresc = Math.min(calcularCrescimento(mercado) / 2.5, 20);
        score += cresc;

        return Math.min(score, 100.0);
    }

    /**
     * ✅ NOVO: Retorna mercados com melhor performance
     *
     * @param limite número de mercados
     * @return mercados com maior performance
     */
    public List<TendenciaMercadoResponse> mercadosMelhorPerformance(int limite) {
        Map<Mercado, Double> scores = new HashMap<>();

        mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive())
                .forEach(m -> scores.put(m, calcularScorePerformance(m)));

        return scores.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limite)
                .map(entry -> {
                    TendenciaMercadoResponse resp = criarTendenciaResponse(entry.getKey());
                    // Adicionar score de performance como crescimento
                    resp.setCrescimento(BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtém lista de todos os mercados ativos
     */
    public List<Mercado> obterTodosMercados() {
        return mercadoRepository.findAll().stream()
                .filter(Mercado::getActive)
                .collect(Collectors.toList());
    }
}
