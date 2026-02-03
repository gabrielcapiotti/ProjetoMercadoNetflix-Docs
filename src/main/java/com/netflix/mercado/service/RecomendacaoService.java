package com.netflix.mercado.service;

import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.Favorito;
import com.netflix.mercado.dto.recomendacao.MercadoRecomendacaoResponse;
import com.netflix.mercado.repository.MercadoRepository;
import com.netflix.mercado.repository.AvaliacaoRepository;
import com.netflix.mercado.repository.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ NOVO: Serviço de recomendações personalizadas de mercados
 * Baseia-se em favoritos, avaliações e categorias preferidas do usuário
 */
@Service
@Transactional(readOnly = true)
public class RecomendacaoService {

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private MercadoService mercadoService;

    /**
     * ✅ NOVO: Gera recomendações baseadas nos favoritos do usuário
     *
     * @param usuario usuário para gerar recomendações
     * @param limite número de recomendações
     * @return lista de mercados recomendados com pontuação
     */
    public List<MercadoRecomendacaoResponse> gerarRecomendacoes(User usuario, int limite) {
        var favoritos = favoritoRepository.findByUsuario(usuario);
        if (favoritos.isEmpty()) {
            return gerarRecomendacoesGenericasPorAvaliacao(limite);
        }

        Set<String> estadosPreferidos = new HashSet<>();

        for (Favorito fav : favoritos) {
            estadosPreferidos.add(fav.getMercado().getEstado());
        }

        var todosOsMercados = mercadoRepository.findAll();
        Map<Mercado, Double> pontuacao = new HashMap<>();

        for (Mercado mercado : todosOsMercados) {
            if (favoritos.stream().anyMatch(f -> f.getMercado().getId().equals(mercado.getId()))) {
                continue;
            }

            double score = calcularPontuacao(mercado, estadosPreferidos, usuario);
            if (score > 0) {
                pontuacao.put(mercado, score);
            }
        }

        return pontuacao.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limite)
                .map(entry -> MercadoRecomendacaoResponse.builder()
                        .mercado(mercadoService.convertToResponse(entry.getKey()))
                        .pontuacao(entry.getValue())
                        .motivo(gerarMotivo(entry.getKey(), estadosPreferidos))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ✅ NOVO: Gera recomendações genéricas por melhor avaliação
     */
    private List<MercadoRecomendacaoResponse> gerarRecomendacoesGenericasPorAvaliacao(int limite) {
        return mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() >= 10)
                .sorted((m1, m2) -> m2.getAvaliacaoMedia().compareTo(m1.getAvaliacaoMedia()))
                .limit(limite)
                .map(m -> MercadoRecomendacaoResponse.builder()
                        .mercado(mercadoService.convertToResponse(m))
                        .pontuacao(m.getAvaliacaoMedia().doubleValue())
                        .motivo("Altamente avaliado pela comunidade")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ✅ NOVO: Calcula a pontuação de um mercado para recomendação
     */
    private double calcularPontuacao(Mercado mercado, Set<String> estadosPreferidos, User usuario) {
        double score = 0.0;

        if (mercado.getAvaliacaoMedia() != null && mercado.getTotalAvaliacoes() >= 5) {
            score += mercado.getAvaliacaoMedia().doubleValue() * 8.0;
        }

        if (estadosPreferidos.contains(mercado.getEstado())) {
            score += 30.0;
        } else {
            score += 10.0;
        }

        int popularidade = Math.min((int)(mercado.getTotalAvaliacoes() / 10), 20);
        score += popularidade;

        boolean temAvaliacaoDoUsuario = avaliacaoRepository.existsByMercadoIdAndUserId(
                mercado.getId(), usuario.getId());
        if (!temAvaliacaoDoUsuario) {
            score += 10.0;
        }

        return Math.min(score, 100.0);
    }

    /**
     * ✅ NOVO: Gera motivo textual da recomendação
     */
    private String gerarMotivo(Mercado mercado, Set<String> estados) {
        List<String> motivos = new ArrayList<>();

        if (mercado.getAvaliacaoMedia() != null && mercado.getAvaliacaoMedia().compareTo(new BigDecimal("4.5")) >= 0) {
            motivos.add("Altamente avaliado (" + mercado.getAvaliacaoMedia() + "/5)");
        }

        if (estados.contains(mercado.getEstado())) {
            motivos.add("Próximo a seus favoritos");
        }

        if (mercado.getTotalAvaliacoes() >= 100) {
            motivos.add("Muito recomendado");
        }

        return motivos.isEmpty() ? "Recomendado para você" : String.join(" | ", motivos);
    }

    /**
     * ✅ NOVO: Recomendações por localização similar
     */
    public List<MercadoRecomendacaoResponse> recomendacoesPorLocalizacao(User usuario, int limite) {
        var favoritos = favoritoRepository.findByUsuario(usuario);
        if (favoritos.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> estados = favoritos.stream()
                .map(f -> f.getMercado().getEstado())
                .collect(Collectors.toSet());

        return mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive() && estados.contains(m.getEstado()))
                .filter(m -> favoritos.stream().noneMatch(f -> f.getMercado().getId().equals(m.getId())))
                .sorted((m1, m2) -> m2.getAvaliacaoMedia().compareTo(m1.getAvaliacaoMedia()))
                .limit(limite)
                .map(m -> MercadoRecomendacaoResponse.builder()
                        .mercado(mercadoService.convertToResponse(m))
                        .pontuacao(m.getAvaliacaoMedia().doubleValue())
                        .motivo("Mercado em " + m.getEstado())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ✅ NOVO: Recomendações não visitados
     */
    public List<MercadoRecomendacaoResponse> recomendacoesNaoVisitados(User usuario, int limite) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        var avaliacoesDoUsuario = avaliacaoRepository.findByUserId(usuario.getId(), pageable);
        var mercadosAvaliados = avaliacoesDoUsuario.getContent()
                .stream()
                .map(a -> a.getMercado().getId())
                .collect(Collectors.toSet());

        return mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive() && !mercadosAvaliados.contains(m.getId()))
                .filter(m -> m.getAvaliacaoMedia() != null && m.getAvaliacaoMedia().compareTo(new BigDecimal("4.0")) >= 0)
                .sorted((m1, m2) -> m2.getAvaliacaoMedia().compareTo(m1.getAvaliacaoMedia()))
                .limit(limite)
                .map(m -> MercadoRecomendacaoResponse.builder()
                        .mercado(mercadoService.convertToResponse(m))
                        .pontuacao(m.getAvaliacaoMedia().doubleValue())
                        .motivo("Você ainda não visitou - é um dos melhores!")
                        .build())
                .collect(Collectors.toList());
    }
}
