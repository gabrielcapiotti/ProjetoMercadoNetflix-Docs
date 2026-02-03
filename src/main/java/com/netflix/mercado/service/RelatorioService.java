package com.netflix.mercado.service;

import com.netflix.mercado.dto.relatorio.*;
import com.netflix.mercado.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ✅ NOVO: Serviço responsável por gerar relatórios do sistema.
 * Fornece dados consolidados para análises e dashboard.
 */
@Service
@Transactional(readOnly = true)
public class RelatorioService {

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PromocaoRepository promocaoRepository;

    /**
     * ✅ NOVO: Gera relatório consolidado do sistema.
     * Retorna estatísticas gerais de todos os mercados.
     *
     * @return RelatorioGeralResponse com dados consolidados
     */
    public RelatorioGeralResponse gerarRelatorioGeral() {
        long totalMercados = mercadoRepository.count();
        long totalAvaliacoes = avaliacaoRepository.count();
        long totalComentarios = comentarioRepository.count();
        long totalPromocoes = promocaoRepository.count();

        // Calcular médias
        Double mediaAvaliacoes = avaliacaoRepository.findAll()
                .stream()
                .mapToDouble(a -> a.getEstrelas())
                .average()
                .orElse(0.0);

        // Encontrar mercado melhor avaliado
        var mercadoMelhorAvaliado = mercadoRepository.findAll()
                .stream()
                .max(Comparator.comparing(m -> m.getAvaliacaoMedia()))
                .orElse(null);

        // Encontrar mercado com mais avaliações
        var mercadoMaisAvaliado = mercadoRepository.findAll()
                .stream()
                .max(Comparator.comparing(m -> m.getTotalAvaliacoes()))
                .orElse(null);

        return RelatorioGeralResponse.builder()
                .dataGeracao(LocalDateTime.now())
                .totalMercados(totalMercados)
                .totalAvaliacoes(totalAvaliacoes)
                .totalComentarios(totalComentarios)
                .totalPromocoes(totalPromocoes)
                .mediaAvaliacoes(BigDecimal.valueOf(mediaAvaliacoes).setScale(2, RoundingMode.HALF_UP))
                .mercadoMelhorAvaliado(mercadoMelhorAvaliado != null ? mercadoMelhorAvaliado.getNome() : "N/A")
                .avaliacaoMelhorMercado(mercadoMelhorAvaliado != null ? mercadoMelhorAvaliado.getAvaliacaoMedia() : BigDecimal.ZERO)
                .mercadoMaisAvaliado(mercadoMaisAvaliado != null ? mercadoMaisAvaliado.getNome() : "N/A")
                .totalAvaliacoesMercadoMaisAvaliado(mercadoMaisAvaliado != null ? (int)(long)mercadoMaisAvaliado.getTotalAvaliacoes() : 0)
                .build();
    }

    /**
     * ✅ NOVO: Gera relatório de performance de um mercado específico.
     * Inclui: avaliações, comentários, promoções ativas.
     *
     * @param mercadoId ID do mercado
     * @return RelatorioMercadoResponse com dados do mercado
     */
    public RelatorioMercadoResponse gerarRelatorioMercado(Long mercadoId) {
        var mercado = mercadoRepository.findById(mercadoId)
                .orElseThrow(() -> new RuntimeException("Mercado não encontrado"));

        long totalAvaliacoesMercado = avaliacaoRepository.countByMercado(mercado);
        long totalComentariosMercado = comentarioRepository.countByAvaliacao_Mercado(mercado);
        long totalPromocoesAtivasMercado = promocaoRepository.countByMercadoAndAtiva(mercado, true);

        // Distribuição de avaliações por estrela
        var distribuicaoEstrelas = new HashMap<Integer, Long>();
        for (int i = 1; i <= 5; i++) {
            final int estrela = i;
            long count = avaliacaoRepository.findAll()
                    .stream()
                    .filter(a -> a.getMercado().getId().equals(mercadoId) && a.getEstrelas() == estrela)
                    .count();
            distribuicaoEstrelas.put(i, count);
        }

        return RelatorioMercadoResponse.builder()
                .mercadoId(mercado.getId())
                .nomeMercado(mercado.getNome())
                .dataGeracao(LocalDateTime.now())
                .avaliacaoMedia(mercado.getAvaliacaoMedia())
                .totalAvaliacoes(totalAvaliacoesMercado)
                .totalComentarios(totalComentariosMercado)
                .totalPromocoesAtivas(totalPromocoesAtivasMercado)
                .distribuicaoEstrelas(distribuicaoEstrelas)
                .ativo(mercado.getActive())
                .build();
    }

    /**
     * ✅ NOVO: Gera ranking dos melhores mercados.
     * Ordenado por avaliação média.
     *
     * @param limite número de mercados no ranking
     * @return Lista de mercados em ranking
     */
    public List<RankingMercadoResponse> gerarRankingMercados(int limite) {
        var mercados = mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive())
                .sorted((m1, m2) -> m2.getAvaliacaoMedia().compareTo(m1.getAvaliacaoMedia()))
                .limit(limite)
                .toList();

        List<RankingMercadoResponse> ranking = new ArrayList<>();
        int posicao = 1;

        for (var mercado : mercados) {
            ranking.add(RankingMercadoResponse.builder()
                    .posicao(posicao++)
                    .nome(mercado.getNome())
                    .cidade(mercado.getCidade())
                    .estado(mercado.getEstado())
                    .avaliacaoMedia(mercado.getAvaliacaoMedia())
                    .totalAvaliacoes(mercado.getTotalAvaliacoes())
                    .build());
        }

        return ranking;
    }

    /**
     * ✅ NOVO: Gera relatório de mercados com poucas avaliações.
     * Útil para identificar mercados que precisam de mais promoção.
     *
     * @param avaliacaoMinima número mínimo de avaliações
     * @return Lista de mercados com poucas avaliações
     */
    public List<MercadoPoucasAvaliacoesResponse> gerarRelatorioPoucasAvaliacoes(int avaliacaoMinima) {
        return mercadoRepository.findAll()
                .stream()
                .filter(m -> m.getActive() && m.getTotalAvaliacoes() < avaliacaoMinima)
                .map(m -> MercadoPoucasAvaliacoesResponse.builder()
                        .mercadoId(m.getId())
                        .nome(m.getNome())
                        .cidade(m.getCidade())
                        .estado(m.getEstado())
                        .totalAvaliacoes(m.getTotalAvaliacoes())
                        .avaliacaoMedia(m.getAvaliacaoMedia())
                        .build())
                .sorted(Comparator.comparing(MercadoPoucasAvaliacoesResponse::getTotalAvaliacoes))
                .toList();
    }

    /**
     * ✅ NOVO: Gera relatório de qualidade de comentários.
     * Inclui: total, média de curtidas, moderação.
     *
     * @return RelatorioComentariosResponse com dados de qualidade
     */
    public RelatorioComentariosResponse gerarRelatorioComentarios() {
        var comentarios = comentarioRepository.findAll();
        long totalComentarios = comentarios.size();
        long comentariosAtivos = comentarios.stream().filter(c -> c.getActive()).count();
        long comentariosInativos = totalComentarios - comentariosAtivos;
        long comentariosAguardandoModeração = comentarios.stream().filter(c -> !c.getModerado()).count();

        double mediaCurtidas = comentarios.stream()
                .mapToLong(c -> c.getCurtidas() != null ? c.getCurtidas() : 0)
                .average()
                .orElse(0.0);

        // Comentário mais curtido
        var comentarioMaisCurtido = comentarios.stream()
                .max(Comparator.comparing(c -> c.getCurtidas() != null ? c.getCurtidas() : 0L))
                .orElse(null);

        return RelatorioComentariosResponse.builder()
                .dataGeracao(LocalDateTime.now())
                .totalComentarios(totalComentarios)
                .comentariosAtivos(comentariosAtivos)
                .comentariosInativos(comentariosInativos)
                .comentariosAguardandoModeração(comentariosAguardandoModeração)
                .percentualAtivos(calcularPercentual(comentariosAtivos, totalComentarios))
                .mediaCurtidas(BigDecimal.valueOf(mediaCurtidas).setScale(2, RoundingMode.HALF_UP))
                .comentarioMaisCurtido(comentarioMaisCurtido != null ? comentarioMaisCurtido.getConteudo().substring(0, Math.min(50, comentarioMaisCurtido.getConteudo().length())) : "N/A")
                .build();
    }

    /**
     * ✅ NOVO: Calcula percentual com arredondamento.
     *
     * @param valor valor atual
     * @param total valor total
     * @return percentual arredondado a 2 casas decimais
     */
    private BigDecimal calcularPercentual(long valor, long total) {
        if (total == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf((valor * 100.0) / total)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
