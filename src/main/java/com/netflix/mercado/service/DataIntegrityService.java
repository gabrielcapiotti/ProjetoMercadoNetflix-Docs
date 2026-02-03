package com.netflix.mercado.service;

import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.Avaliacao;
import com.netflix.mercado.entity.Comentario;
import com.netflix.mercado.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * ✅ NOVO: Serviço responsável por validações de integridade de dados.
 * Garante consistência entre entidades relacionadas.
 */
@Service
@Transactional(readOnly = true)
public class DataIntegrityService {

    @Autowired
    private MercadoService mercadoService;

    /**
     * ✅ NOVO: Valida integridade completa de um mercado.
     * Verifica:
     * - Dados básicos obrigatórios
     * - Coordenadas válidas
     * - Campos numéricos em ranges corretos
     *
     * @param mercado mercado a validar
     * @throws ValidationException se alguma violação encontrada
     */
    public void validarIntegridadeMercado(Mercado mercado) {
        // Validar campos obrigatórios
        if (mercado.getNome() == null || mercado.getNome().isBlank()) {
            throw new ValidationException("Mercado deve ter um nome");
        }

        if (mercado.getDescricao() == null || mercado.getDescricao().isBlank()) {
            throw new ValidationException("Mercado deve ter uma descrição");
        }

        // Validar coordenadas
        if (mercado.getLatitude() == null) {
            throw new ValidationException("Latitude é obrigatória");
        }
        if (mercado.getLongitude() == null) {
            throw new ValidationException("Longitude é obrigatória");
        }

        double lat = mercado.getLatitude().doubleValue();
        double lon = mercado.getLongitude().doubleValue();

        if (lat < -90 || lat > 90) {
            throw new ValidationException("Latitude deve estar entre -90 e 90: " + lat);
        }
        if (lon < -180 || lon > 180) {
            throw new ValidationException("Longitude deve estar entre -180 e 180: " + lon);
        }

        // Validar avaliação média
        if (mercado.getAvaliacaoMedia() == null) {
            throw new ValidationException("Avaliação média não pode ser nula");
        }

        BigDecimal media = mercado.getAvaliacaoMedia();
        if (media.compareTo(BigDecimal.ZERO) < 0 || media.compareTo(new BigDecimal("5.0")) > 0) {
            throw new ValidationException("Avaliação média deve estar entre 0 e 5: " + media);
        }

        // Validar total de avaliações
        if (mercado.getTotalAvaliacoes() == null || mercado.getTotalAvaliacoes() < 0) {
            throw new ValidationException("Total de avaliações não pode ser negativo");
        }

    }

    /**
     * ✅ NOVO: Valida integridade de uma avaliação.
     * Verifica:
     * - Número de estrelas válido (1-5)
     * - Relacionamentos consistentes
     * - Campos obrigatórios
     *
     * @param avaliacao avaliação a validar
     * @throws ValidationException se alguma violação encontrada
     */
    public void validarIntegridadeAvaliacao(Avaliacao avaliacao) {

        // Validar estrelas
        if (avaliacao.getEstrelas() == null) {
            throw new ValidationException("Avaliação deve ter um número de estrelas");
        }

        if (avaliacao.getEstrelas() < 1 || avaliacao.getEstrelas() > 5) {
            throw new ValidationException("Estrelas deve estar entre 1 e 5: " + avaliacao.getEstrelas());
        }

        // Validar relacionamentos
        if (avaliacao.getMercado() == null) {
            throw new ValidationException("Avaliação deve estar associada a um mercado");
        }

        if (avaliacao.getUser() == null) {
            throw new ValidationException("Avaliação deve estar associada a um usuário");
        }

        // Validar counts
        if (avaliacao.getUteis() == null || avaliacao.getUteis() < 0) {
            throw new ValidationException("Contagem de 'úteis' não pode ser negativa");
        }

        if (avaliacao.getInutils() == null || avaliacao.getInutils() < 0) {
            throw new ValidationException("Contagem de 'inúteis' não pode ser negativa");
        }

    }

    /**
     * ✅ NOVO: Valida integridade de um comentário.
     * Verifica:
     * - Conteúdo não vazio
     * - Relacionamentos válidos
     * - Estrutura de replies (sem loops)
     *
     * @param comentario comentário a validar
     * @throws ValidationException se alguma violação encontrada
     */
    public void validarIntegridadeComentario(Comentario comentario) {

        // Validar conteúdo
        if (comentario.getConteudo() == null || comentario.getConteudo().isBlank()) {
            throw new ValidationException("Comentário deve ter conteúdo");
        }

        // Validar relacionamentos
        if (comentario.getAvaliacao() == null) {
            throw new ValidationException("Comentário deve estar associado a uma avaliação");
        }

        if (comentario.getUser() == null) {
            throw new ValidationException("Comentário deve estar associado a um usuário");
        }

        // Validar curtidas
        if (comentario.getCurtidas() == null || comentario.getCurtidas() < 0) {
            throw new ValidationException("Contagem de curtidas não pode ser negativa");
        }

        // Detectar loops em replies
        if (comentario.getComentarioPai() != null) {
            validarSemLoopEmReplies(comentario);
        }

    }

    /**
     * ✅ NOVO: Detecta loops em estrutura de replies.
     * Impede que um comentário seja seu próprio pai ou avó, etc.
     *
     * @param comentario comentário a verificar
     * @throws ValidationException se loop detectado
     */
    private void validarSemLoopEmReplies(Comentario comentario) {
        Comentario atual = comentario.getComentarioPai();
        int iteracoes = 0;
        final int MAX_PROFUNDIDADE = 100;

        while (atual != null) {
            if (atual.getId().equals(comentario.getId())) {
                throw new ValidationException("Loop detectado na estrutura de replies");
            }

            if (++iteracoes > MAX_PROFUNDIDADE) {
                throw new ValidationException("Profundidade de replies excedida");
            }

            atual = atual.getComentarioPai();
        }
    }

    /**
     * ✅ NOVO: Sanitiza strings removendo caracteres perigosos.
     * Previne XSS e injection attacks.
     *
     * @param input string a sanitizar
     * @return string sanitizada
     */
    public String sanitizarString(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;")
                .trim();
    }

    /**
     * ✅ NOVO: Valida se email está em formato correto.
     *
     * @param email email a validar
     * @throws ValidationException se email inválido
     */
    public void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email não pode estar vazio");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new ValidationException("Email inválido: " + email);
        }

        if (email.length() > 150) {
            throw new ValidationException("Email muito longo (máximo 150 caracteres)");
        }
    }

    /**
     * ✅ NOVO: Valida se URL está em formato correto.
     *
     * @param url URL a validar
     * @throws ValidationException se URL inválida
     */
    public void validarURL(String url) {
        if (url == null || url.isBlank()) {
            return; // URLs opcionais
        }

        try {
            new java.net.URL(url);
        } catch (java.net.MalformedURLException e) {
            throw new ValidationException("URL inválida: " + url);
        }

        if (url.length() > 500) {
            throw new ValidationException("URL muito longa (máximo 500 caracteres)");
        }
    }
}
