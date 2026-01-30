# Service Layer - Mercado e Avaliação

> Classes de serviço profissionais com lógica de negócio, transações e validações para Mercados e Avaliações

## MercadoService.java

```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.mercado.request.CreateMercadoRequest;
import com.netflix.mercados.dto.mercado.request.UpdateMercadoRequest;
import com.netflix.mercados.dto.mercado.response.MercadoResponse;
import com.netflix.mercados.dto.mercado.response.MercadoDetailResponse;
import com.netflix.mercados.dto.mercado.response.HorarioFuncionamentoResponse;
import com.netflix.mercados.dto.horario.request.CreateHorarioRequest;
import com.netflix.mercados.entity.Mercado;
import com.netflix.mercados.entity.HorarioFuncionamento;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.repository.MercadoRepository;
import com.netflix.mercados.repository.HorarioFuncionamentoRepository;
import com.netflix.mercados.repository.UserRepository;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoService {

    private final MercadoRepository mercadoRepository;
    private final HorarioFuncionamentoRepository horarioRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================

    @Transactional
    public MercadoResponse criarMercado(CreateMercadoRequest request, String usuarioId) {
        log.info("Iniciando criação de novo mercado: {}", request.getNome());

        // Validar CNPJ único
        if (mercadoRepository.existsByCnpj(request.getCnpj())) {
            log.warn("CNPJ já existe: {}", request.getCnpj());
            throw new ValidationException("CNPJ já está registrado no sistema");
        }

        // Validar email único
        if (mercadoRepository.existsByEmail(request.getEmail())) {
            log.warn("Email já existe: {}", request.getEmail());
            throw new ValidationException("Email já está registrado");
        }

        // Obter usuário criador
        User usuario = userRepository.findByUsername(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", usuarioId));

        // Criar mercado
        Mercado mercado = Mercado.builder()
            .nome(request.getNome())
            .descricao(request.getDescricao())
            .telefone(request.getTelefone())
            .email(request.getEmail())
            .cnpj(request.getCnpj())
            .tipoMercado(request.getTipoMercado())
            .rua(request.getRua())
            .numero(request.getNumero())
            .complemento(request.getComplemento())
            .cep(request.getCep())
            .cidade(request.getCidade())
            .estado(request.getEstado())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .logo(request.getLogo())
            .siteUrl(request.getSiteUrl())
            .usuario(usuario)
            .aprovado(false)
            .ratingMedio(0.0)
            .totalAvaliacoes(0L)
            .totalFavoritos(0)
            .createdBy(usuarioId)
            .build();

        Mercado mercadoSalvo = mercadoRepository.save(mercado);
        log.info("Mercado criado com sucesso. ID: {}", mercadoSalvo.getId());

        return MercadoResponse.from(mercadoSalvo);
    }

    // ==================== READ ====================

    @Transactional(readOnly = true)
    public Page<MercadoResponse> listarMercados(Pageable pageable, String nome, 
                                                 String tipoMercado, String estado, Boolean aprovado) {
        log.debug("Listando mercados com filtros. Nome: {}, Tipo: {}, Estado: {}, Aprovado: {}", 
                  nome, tipoMercado, estado, aprovado);

        Page<Mercado> mercados = mercadoRepository.findAll(
            (root, query, cb) -> {
                var predicates = new java.util.ArrayList<>();

                if (nome != null && !nome.isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
                }

                if (tipoMercado != null && !tipoMercado.isBlank()) {
                    predicates.add(cb.equal(root.get("tipoMercado"), tipoMercado));
                }

                if (estado != null && !estado.isBlank()) {
                    predicates.add(cb.equal(root.get("estado"), estado));
                }

                if (aprovado != null) {
                    predicates.add(cb.equal(root.get("aprovado"), aprovado));
                }

                predicates.add(cb.equal(root.get("deleted"), false));

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
            },
            pageable
        );

        return mercados.map(MercadoResponse::from);
    }

    @Transactional(readOnly = true)
    public MercadoDetailResponse obterDetalhes(Long id) {
        log.debug("Obtendo detalhes do mercado ID: {}", id);

        Mercado mercado = mercadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", id));

        List<HorarioFuncionamento> horarios = horarioRepository.findByMercadoId(id);

        MercadoDetailResponse response = MercadoDetailResponse.from(mercado);
        response.setHorarios(
            horarios.stream()
                .map(HorarioFuncionamentoResponse::from)
                .collect(Collectors.toList())
        );

        return response;
    }

    // ==================== UPDATE ====================

    @Transactional
    public MercadoResponse atualizarMercado(Long id, UpdateMercadoRequest request, String usuarioId) {
        log.info("Atualizando mercado ID: {}", id);

        Mercado mercado = mercadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", id));

        // Validar autorização
        if (!mercado.getUsuario().getUsername().equals(usuarioId)) {
            throw new ValidationException("Você não tem permissão para atualizar este mercado");
        }

        // Atualizar campos
        if (request.getNome() != null) {
            mercado.setNome(request.getNome());
        }
        if (request.getDescricao() != null) {
            mercado.setDescricao(request.getDescricao());
        }
        if (request.getTelefone() != null) {
            mercado.setTelefone(request.getTelefone());
        }
        if (request.getEmail() != null) {
            // Validar email único
            if (!mercado.getEmail().equals(request.getEmail()) && 
                mercadoRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException("Email já está registrado");
            }
            mercado.setEmail(request.getEmail());
        }
        if (request.getRua() != null) {
            mercado.setRua(request.getRua());
        }
        if (request.getNumero() != null) {
            mercado.setNumero(request.getNumero());
        }
        if (request.getComplemento() != null) {
            mercado.setComplemento(request.getComplemento());
        }
        if (request.getCep() != null) {
            mercado.setCep(request.getCep());
        }
        if (request.getCidade() != null) {
            mercado.setCidade(request.getCidade());
        }
        if (request.getEstado() != null) {
            mercado.setEstado(request.getEstado());
        }
        if (request.getLatitude() != null) {
            mercado.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            mercado.setLongitude(request.getLongitude());
        }
        if (request.getLogo() != null) {
            mercado.setLogo(request.getLogo());
        }
        if (request.getSiteUrl() != null) {
            mercado.setSiteUrl(request.getSiteUrl());
        }

        mercado.setUpdatedBy(usuarioId);
        Mercado mercadoAtualizado = mercadoRepository.save(mercado);

        log.info("Mercado ID: {} atualizado com sucesso", id);
        return MercadoResponse.from(mercadoAtualizado);
    }

    // ==================== DELETE ====================

    @Transactional
    public void deletarMercado(Long id, String usuarioId) {
        log.info("Deletando mercado ID: {}", id);

        Mercado mercado = mercadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", id));

        // Validar autorização
        if (!mercado.getUsuario().getUsername().equals(usuarioId)) {
            throw new ValidationException("Você não tem permissão para deletar este mercado");
        }

        mercado.markAsDeleted(usuarioId);
        mercadoRepository.save(mercado);

        log.info("Mercado ID: {} deletado com sucesso", id);
    }

    // ==================== APPROVAL ====================

    @Transactional
    public MercadoResponse aprovarMercado(Long id, String usuarioId) {
        log.info("Aprovando mercado ID: {} por usuário: {}", id, usuarioId);

        Mercado mercado = mercadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", id));

        if (mercado.getAprovado()) {
            throw new ValidationException("Este mercado já foi aprovado");
        }

        mercado.setAprovado(true);
        mercado.setMotivoRejeicao(null);
        mercado.setUpdatedBy(usuarioId);

        Mercado mercadoSalvo = mercadoRepository.save(mercado);

        log.info("Mercado ID: {} aprovado com sucesso", id);
        return MercadoResponse.from(mercadoSalvo);
    }

    @Transactional
    public MercadoResponse rejeitarMercado(Long id, String motivo, String usuarioId) {
        log.info("Rejeitando mercado ID: {} com motivo: {}", id, motivo);

        Mercado mercado = mercadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", id));

        if (mercado.getAprovado()) {
            throw new ValidationException("Não é possível rejeitar um mercado já aprovado");
        }

        mercado.setAprovado(false);
        mercado.setMotivoRejeicao(motivo);
        mercado.setUpdatedBy(usuarioId);

        Mercado mercadoSalvo = mercadoRepository.save(mercado);

        log.info("Mercado ID: {} rejeitado com sucesso", id);
        return MercadoResponse.from(mercadoSalvo);
    }

    // ==================== LOCATION ====================

    @Transactional(readOnly = true)
    public List<MercadoResponse> buscarProximos(Double latitude, Double longitude, Double raioKm) {
        log.debug("Buscando mercados próximos. Lat: {}, Lon: {}, Raio: {}km", 
                  latitude, longitude, raioKm);

        // Calcular raio em graus (aproximado)
        double raioGraus = raioKm / 111.0;

        List<Mercado> mercados = mercadoRepository.findAll((root, query, cb) -> {
            var predicates = new java.util.ArrayList<>();

            // Latitude dentro do raio
            predicates.add(cb.between(
                root.get("latitude"),
                latitude - raioGraus,
                latitude + raioGraus
            ));

            // Longitude dentro do raio
            predicates.add(cb.between(
                root.get("longitude"),
                longitude - raioGraus,
                longitude + raioGraus
            ));

            // Apenas aprovados
            predicates.add(cb.equal(root.get("aprovado"), true));
            predicates.add(cb.equal(root.get("deleted"), false));

            return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        // Filtrar por distância real (Haversine)
        return mercados.stream()
            .filter(m -> calcularDistancia(latitude, longitude, m.getLatitude(), m.getLongitude()) <= raioKm)
            .map(MercadoResponse::from)
            .collect(Collectors.toList());
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em quilômetros

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ==================== FAVORITES ====================

    @Transactional
    public void adicionarFavorito(Long mercadoId, String usuarioId) {
        log.info("Adicionando mercado {} aos favoritos do usuário {}", mercadoId, usuarioId);

        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        User usuario = userRepository.findByUsername(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", usuarioId));

        if (usuario.getFavoritosMercados().contains(mercado)) {
            throw new ValidationException("Este mercado já está nos seus favoritos");
        }

        usuario.getFavoritosMercados().add(mercado);
        mercado.setTotalFavoritos(mercado.getTotalFavoritos() + 1);

        userRepository.save(usuario);
        mercadoRepository.save(mercado);

        log.info("Mercado {} adicionado aos favoritos com sucesso", mercadoId);
    }

    @Transactional
    public void removerFavorito(Long mercadoId, String usuarioId) {
        log.info("Removendo mercado {} dos favoritos do usuário {}", mercadoId, usuarioId);

        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        User usuario = userRepository.findByUsername(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", usuarioId));

        if (!usuario.getFavoritosMercados().contains(mercado)) {
            throw new ResourceNotFoundException("Este mercado não está nos seus favoritos");
        }

        usuario.getFavoritosMercados().remove(mercado);
        mercado.setTotalFavoritos(Math.max(0, mercado.getTotalFavoritos() - 1));

        userRepository.save(usuario);
        mercadoRepository.save(mercado);

        log.info("Mercado {} removido dos favoritos com sucesso", mercadoId);
    }

    // ==================== HOURS ====================

    @Transactional(readOnly = true)
    public List<HorarioFuncionamentoResponse> obterHorarios(Long mercadoId) {
        log.debug("Obtendo horários de funcionamento do mercado ID: {}", mercadoId);

        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        return horarioRepository.findByMercadoId(mercadoId)
            .stream()
            .map(HorarioFuncionamentoResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public HorarioFuncionamentoResponse adicionarHorario(Long mercadoId, CreateHorarioRequest request, String usuarioId) {
        log.info("Adicionando horário de funcionamento ao mercado ID: {}", mercadoId);

        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        // Validar autorização
        if (!mercado.getUsuario().getUsername().equals(usuarioId)) {
            throw new ValidationException("Você não tem permissão para adicionar horários a este mercado");
        }

        // Validar horários
        if (!request.getHorarioAbertura().isBefore(request.getHorarioFechamento())) {
            throw new ValidationException("Horário de abertura deve ser anterior ao horário de fechamento");
        }

        HorarioFuncionamento horario = HorarioFuncionamento.builder()
            .mercado(mercado)
            .diaSemana(request.getDiaSemana())
            .horarioAbertura(request.getHorarioAbertura())
            .horarioFechamento(request.getHorarioFechamento())
            .aberto(request.getAberto())
            .observacoes(request.getObservacoes())
            .createdBy(usuarioId)
            .build();

        HorarioFuncionamento horarioSalvo = horarioRepository.save(horario);

        log.info("Horário adicionado ao mercado ID: {} com sucesso", mercadoId);
        return HorarioFuncionamentoResponse.from(horarioSalvo);
    }
}
```

## AvaliacaoService.java

```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.avaliacao.request.CreateAvaliacaoRequest;
import com.netflix.mercados.dto.avaliacao.request.UpdateAvaliacaoRequest;
import com.netflix.mercados.dto.avaliacao.response.AvaliacaoResponse;
import com.netflix.mercados.dto.avaliacao.response.AvaliacaoDetailResponse;
import com.netflix.mercados.dto.mercado.response.RatingStatsResponse;
import com.netflix.mercados.entity.Avaliacao;
import com.netflix.mercados.entity.Mercado;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.repository.AvaliacaoRepository;
import com.netflix.mercados.repository.MercadoRepository;
import com.netflix.mercados.repository.UserRepository;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final MercadoRepository mercadoRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================

    @Transactional
    public AvaliacaoResponse criarAvaliacao(CreateAvaliacaoRequest request, String usuarioId) {
        log.info("Iniciando criação de avaliação para mercado ID: {} por usuário: {}", 
                 request.getMercadoId(), usuarioId);

        // Validar mercado existe
        Mercado mercado = mercadoRepository.findById(request.getMercadoId())
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", request.getMercadoId()));

        // Validar usuário existe
        User usuario = userRepository.findByUsername(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", usuarioId));

        // Validar se usuário já avaliou este mercado
        if (avaliacaoRepository.existsByMercadoIdAndUsuarioId(request.getMercadoId(), usuario.getId())) {
            log.warn("Usuário {} já avaliou mercado {}", usuarioId, request.getMercadoId());
            throw new ValidationException("Você já avaliou este mercado");
        }

        // Criar avaliação
        Avaliacao avaliacao = Avaliacao.builder()
            .mercado(mercado)
            .usuario(usuario)
            .nota(request.getNota())
            .comentario(request.getComentario())
            .avaliacaoAtendimento(request.getAvaliacaoAtendimento())
            .avaliacaoLimpeza(request.getAvaliacaoLimpeza())
            .avaliacaoProdutos(request.getAvaliacaoProdutos())
            .avaliacaoPrecos(request.getAvaliacaoPrecos())
            .recomenda(request.getRecomenda())
            .totalLikes(0)
            .totalDislikes(0)
            .fotosUrl(request.getFotosUrl())
            .createdBy(usuarioId)
            .build();

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        // Atualizar rating médio do mercado
        atualizarRatingMercado(mercado.getId());

        log.info("Avaliação criada com sucesso. ID: {}", avaliacaoSalva.getId());
        return AvaliacaoResponse.from(avaliacaoSalva);
    }

    // ==================== READ ====================

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> listarAvaliacoes(Pageable pageable, Long mercadoId, Long usuarioId,
                                                     Integer notaMinima, Integer notaMaxima) {
        log.debug("Listando avaliações com filtros. MercadoId: {}, UsuarioId: {}", mercadoId, usuarioId);

        Page<Avaliacao> avaliacoes = avaliacaoRepository.findAll(
            (root, query, cb) -> {
                var predicates = new java.util.ArrayList<>();

                if (mercadoId != null) {
                    predicates.add(cb.equal(root.get("mercado").get("id"), mercadoId));
                }

                if (usuarioId != null) {
                    predicates.add(cb.equal(root.get("usuario").get("id"), usuarioId));
                }

                if (notaMinima != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("nota"), notaMinima));
                }

                if (notaMaxima != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("nota"), notaMaxima));
                }

                predicates.add(cb.equal(root.get("deleted"), false));

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
            },
            pageable
        );

        return avaliacoes.map(AvaliacaoResponse::from);
    }

    @Transactional(readOnly = true)
    public AvaliacaoDetailResponse obterDetalhes(Long id) {
        log.debug("Obtendo detalhes da avaliação ID: {}", id);

        Avaliacao avaliacao = avaliacaoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Avaliação", "id", id));

        return AvaliacaoDetailResponse.from(avaliacao);
    }

    // ==================== UPDATE ====================

    @Transactional
    public AvaliacaoResponse atualizarAvaliacao(Long id, UpdateAvaliacaoRequest request, String usuarioId) {
        log.info("Atualizando avaliação ID: {}", id);

        Avaliacao avaliacao = avaliacaoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Avaliação", "id", id));

        // Validar autorização
        if (!avaliacao.getUsuario().getUsername().equals(usuarioId)) {
            throw new ValidationException("Você não tem permissão para atualizar esta avaliação");
        }

        if (request.getNota() != null) {
            avaliacao.setNota(request.getNota());
        }
        if (request.getComentario() != null) {
            avaliacao.setComentario(request.getComentario());
        }
        if (request.getAvaliacaoAtendimento() != null) {
            avaliacao.setAvaliacaoAtendimento(request.getAvaliacaoAtendimento());
        }
        if (request.getAvaliacaoLimpeza() != null) {
            avaliacao.setAvaliacaoLimpeza(request.getAvaliacaoLimpeza());
        }
        if (request.getAvaliacaoProdutos() != null) {
            avaliacao.setAvaliacaoProdutos(request.getAvaliacaoProdutos());
        }
        if (request.getAvaliacaoPrecos() != null) {
            avaliacao.setAvaliacaoPrecos(request.getAvaliacaoPrecos());
        }
        if (request.getRecomenda() != null) {
            avaliacao.setRecomenda(request.getRecomenda());
        }
        if (request.getFotosUrl() != null) {
            avaliacao.setFotosUrl(request.getFotosUrl());
        }

        avaliacao.setUpdatedBy(usuarioId);
        Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);

        // Atualizar rating médio do mercado
        atualizarRatingMercado(avaliacao.getMercado().getId());

        log.info("Avaliação ID: {} atualizada com sucesso", id);
        return AvaliacaoResponse.from(avaliacaoAtualizada);
    }

    // ==================== DELETE ====================

    @Transactional
    public void deletarAvaliacao(Long id, String usuarioId) {
        log.info("Deletando avaliação ID: {}", id);

        Avaliacao avaliacao = avaliacaoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Avaliação", "id", id));

        // Validar autorização
        if (!avaliacao.getUsuario().getUsername().equals(usuarioId)) {
            throw new ValidationException("Você não tem permissão para deletar esta avaliação");
        }

        Long mercadoId = avaliacao.getMercado().getId();
        avaliacao.markAsDeleted(usuarioId);
        avaliacaoRepository.save(avaliacao);

        // Atualizar rating médio do mercado
        atualizarRatingMercado(mercadoId);

        log.info("Avaliação ID: {} deletada com sucesso", id);
    }

    // ==================== MERCADO SPECIFIC ====================

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> listarAvaliacoesMercado(Long mercadoId, Pageable pageable,
                                                           Integer notaMinima, Integer notaMaxima) {
        log.debug("Listando avaliações do mercado ID: {}", mercadoId);

        // Validar mercado existe
        mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        return listarAvaliacoes(pageable, mercadoId, null, notaMinima, notaMaxima);
    }

    @Transactional(readOnly = true)
    public RatingStatsResponse obterEstatisticasAvaliacao(Long mercadoId) {
        log.debug("Obtendo estatísticas de avaliação do mercado ID: {}", mercadoId);

        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        long avaliacoes5 = avaliacaoRepository.countByMercadoIdAndNota(mercadoId, 5);
        long avaliacoes4 = avaliacaoRepository.countByMercadoIdAndNota(mercadoId, 4);
        long avaliacoes3 = avaliacaoRepository.countByMercadoIdAndNota(mercadoId, 3);
        long avaliacoes2 = avaliacaoRepository.countByMercadoIdAndNota(mercadoId, 2);
        long avaliacoes1 = avaliacaoRepository.countByMercadoIdAndNota(mercadoId, 1);

        RatingStatsResponse stats = RatingStatsResponse.builder()
            .mercadoId(mercadoId)
            .mercadoNome(mercado.getNome())
            .ratingMedio(mercado.getRatingMedio())
            .totalAvaliacoes(mercado.getTotalAvaliacoes())
            .avaliacoes5Estrelas(avaliacoes5)
            .avaliacoes4Estrelas(avaliacoes4)
            .avaliacoes3Estrelas(avaliacoes3)
            .avaliacoes2Estrelas(avaliacoes2)
            .avaliacoes1Estrela(avaliacoes1)
            .build();

        stats.calcularPercentualAprovacao();

        return stats;
    }

    // ==================== HELPER METHODS ====================

    @Transactional
    private void atualizarRatingMercado(Long mercadoId) {
        log.debug("Atualizando rating médio do mercado ID: {}", mercadoId);

        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado", "id", mercadoId));

        Double ratingMedio = avaliacaoRepository.obterRatingMedioPorMercado(mercadoId);
        Long totalAvaliacoes = avaliacaoRepository.countByMercadoId(mercadoId);

        mercado.setRatingMedio(ratingMedio != null ? ratingMedio : 0.0);
        mercado.setTotalAvaliacoes(totalAvaliacoes);

        mercadoRepository.save(mercado);

        log.debug("Rating médio do mercado ID: {} atualizado para: {}", mercadoId, ratingMedio);
    }
}
```

## Métodos Customizados no Repository

### AvaliacaoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>, JpaSpecificationExecutor<Avaliacao> {

    boolean existsByMercadoIdAndUsuarioId(Long mercadoId, Long usuarioId);

    long countByMercadoIdAndNota(Long mercadoId, Integer nota);

    long countByMercadoId(Long mercadoId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.mercado.id = :mercadoId AND a.deleted = false")
    Double obterRatingMedioPorMercado(@Param("mercadoId") Long mercadoId);
}
```

### MercadoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long>, JpaSpecificationExecutor<Mercado> {

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
```

## Requisitos para pom.xml

```xml
<!-- Validação -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Springdoc OpenAPI (Swagger) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

## Resumo de Features dos Services

- ✅ Lógica de negócio completa
- ✅ Transações com @Transactional
- ✅ Validações e autorização
- ✅ Logging com @Slf4j
- ✅ Tratamento de exceções
- ✅ Cálculo de ratings agregados
- ✅ Cálculo de distância (Haversine)
- ✅ Soft delete
- ✅ Auditoria com createdBy/updatedBy
- ✅ Otimizações de banco de dados
- ✅ Queries customizadas
