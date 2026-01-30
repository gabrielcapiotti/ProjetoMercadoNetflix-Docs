package com.netflix.mercado.service;

import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.exception.UnauthorizedException;
import com.netflix.mercado.repository.MercadoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.mercado.CreateMercadoRequest;
import com.netflix.mercado.dto.mercado.UpdateMercadoRequest;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsável por gerenciar mercados.
 * Implementa lógica de criação, atualização, busca e aprovação de mercados.
 */
@Slf4j
@Service
@Transactional
public class MercadoService {

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Cria um novo mercado.
     *
     * @param request dados do novo mercado
     * @param owner usuário proprietário do mercado
     * @return o mercado criado
     * @throws ValidationException se dados inválidos
     */
    public Mercado createMercado(CreateMercadoRequest request, User owner) {
        log.info("Criando novo mercado: {}", request.getNome());

        // Validar dados obrigatórios
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new ValidationException("Nome do mercado é obrigatório");
        }
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new ValidationException("Coordenadas geográficas são obrigatórias");
        }

        Mercado mercado = new Mercado();
        mercado.setNome(request.getNome());
        mercado.setDescricao(request.getDescricao());
        mercado.setCidade(request.getCidade());
        mercado.setBairro(request.getBairro());
        mercado.setRua(request.getRua());
        mercado.setNumero(request.getNumero());
        mercado.setComplemento(request.getComplemento());
        mercado.setCep(request.getCep());
        mercado.setLatitude(request.getLatitude());
        mercado.setLongitude(request.getLongitude());
        mercado.setTelefone(request.getTelefone());
        mercado.setEmail(request.getEmail());
        mercado.setOwner(owner);
        mercado.setAprovado(false);
        mercado.setAvaliacaoMedia(0.0);
        mercado.setTotalAvaliacoes(0L);

        mercado = mercadoRepository.save(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                owner,
                "CREATE",
                "MERCADO",
                mercado.getId(),
                "Novo mercado criado: " + mercado.getNome(),
                LocalDateTime.now()
        ));

        log.info("Mercado criado com sucesso. ID: {}", mercado.getId());
        return mercado;
    }

    /**
     * Atualiza um mercado existente.
     *
     * @param id ID do mercado
     * @param request dados a atualizar
     * @param user usuário realizando a atualização
     * @return o mercado atualizado
     * @throws ResourceNotFoundException se mercado não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public Mercado updateMercado(Long id, UpdateMercadoRequest request, User user) {
        log.info("Atualizando mercado com ID: {}", id);

        Mercado mercado = getMercadoById(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(user, mercado)) {
            log.warn("Tentativa de atualização não autorizada do mercado ID: {} por usuário: {}", id, user.getEmail());
            throw new UnauthorizedException("Você não tem permissão para atualizar este mercado");
        }

        String valoresAnteriores = String.format("nome=%s, cidade=%s", mercado.getNome(), mercado.getCidade());

        // Atualizar campos
        if (request.getNome() != null && !request.getNome().isBlank()) {
            mercado.setNome(request.getNome());
        }
        if (request.getDescricao() != null) {
            mercado.setDescricao(request.getDescricao());
        }
        if (request.getCidade() != null && !request.getCidade().isBlank()) {
            mercado.setCidade(request.getCidade());
        }
        if (request.getBairro() != null) {
            mercado.setBairro(request.getBairro());
        }
        if (request.getRua() != null) {
            mercado.setRua(request.getRua());
        }
        if (request.getNumero() != null) {
            mercado.setNumero(request.getNumero());
        }
        if (request.getTelefone() != null) {
            mercado.setTelefone(request.getTelefone());
        }
        if (request.getEmail() != null) {
            mercado.setEmail(request.getEmail());
        }

        mercado = mercadoRepository.save(mercado);

        String valoresNovos = String.format("nome=%s, cidade=%s", mercado.getNome(), mercado.getCidade());

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                user,
                "UPDATE",
                "MERCADO",
                mercado.getId(),
                "Alterações: " + valoresAnteriores + " -> " + valoresNovos,
                LocalDateTime.now()
        ));

        log.info("Mercado atualizado com sucesso. ID: {}", id);
        return mercado;
    }

    /**
     * Deleta um mercado.
     *
     * @param id ID do mercado
     * @param user usuário realizando a deleção
     * @throws ResourceNotFoundException se mercado não existe
     * @throws UnauthorizedException se usuário não é proprietário
     */
    public void deleteMercado(Long id, User user) {
        log.info("Deletando mercado com ID: {}", id);

        Mercado mercado = getMercadoById(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(user, mercado)) {
            log.warn("Tentativa de deleção não autorizada do mercado ID: {} por usuário: {}", id, user.getEmail());
            throw new UnauthorizedException("Você não tem permissão para deletar este mercado");
        }

        mercadoRepository.delete(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                user,
                "DELETE",
                "MERCADO",
                id,
                "Mercado deletado: " + mercado.getNome(),
                LocalDateTime.now()
        ));

        log.info("Mercado deletado com sucesso. ID: {}", id);
    }

    /**
     * Obtém um mercado pelo ID.
     *
     * @param id ID do mercado
     * @return o mercado encontrado
     * @throws ResourceNotFoundException se mercado não existe
     */
    @Transactional(readOnly = true)
    public Mercado getMercadoById(Long id) {
        log.debug("Buscando mercado com ID: {}", id);
        return mercadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Mercado não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Mercado não encontrado com ID: " + id);
                });
    }

    /**
     * Obtém todos os mercados com paginação.
     *
     * @param pageable informações de paginação
     * @return página de mercados
     */
    @Transactional(readOnly = true)
    public Page<MercadoResponse> getAllMercados(Pageable pageable) {
        log.debug("Buscando todos os mercados aprovados com paginação");
        return mercadoRepository.findByAprovadoTrue(pageable)
                .map(this::convertToResponse);
    }

    /**
     * Busca mercados próximos a uma localização.
     *
     * @param latitude latitude da localização
     * @param longitude longitude da localização
     * @param raio raio de busca em km
     * @return lista de mercados próximos
     */
    @Transactional(readOnly = true)
    public List<Mercado> buscarPorProximidade(Double latitude, Double longitude, Double raio) {
        log.debug("Buscando mercados próximos: lat={}, lon={}, raio={}km", latitude, longitude, raio);

        // Usar cálculo de proximidade (aproximado com retângulo)
        Double latDelta = raio / 111.0; // 1 grau ≈ 111 km
        Double lonDelta = raio / (111.0 * Math.cos(Math.toRadians(latitude)));

        Double minLat = latitude - latDelta;
        Double maxLat = latitude + latDelta;
        Double minLon = longitude - lonDelta;
        Double maxLon = longitude + lonDelta;

        return mercadoRepository.findByAprovadoTrueAndLatitudeBetweenAndLongitudeBetween(
                minLat, maxLat, minLon, maxLon
        );
    }

    /**
     * Busca mercados por nome.
     *
     * @param nome nome ou parte do nome do mercado
     * @param pageable informações de paginação
     * @return página de mercados encontrados
     */
    @Transactional(readOnly = true)
    public Page<Mercado> buscarPorNome(String nome, Pageable pageable) {
        log.debug("Buscando mercados por nome: {}", nome);
        return mercadoRepository.findByAprovadoTrueAndNomeContainingIgnoreCase(nome, pageable);
    }

    /**
     * Busca mercados por cidade.
     *
     * @param cidade nome da cidade
     * @param pageable informações de paginação
     * @return página de mercados encontrados
     */
    @Transactional(readOnly = true)
    public Page<Mercado> buscarPorCidade(String cidade, Pageable pageable) {
        log.debug("Buscando mercados por cidade: {}", cidade);
        return mercadoRepository.findByAprovadoTrueAndCidadeContainingIgnoreCase(cidade, pageable);
    }

    /**
     * Aprova um mercado (apenas admin).
     *
     * @param id ID do mercado
     * @throws ResourceNotFoundException se mercado não existe
     */
    public void aprovarMercado(Long id) {
        log.info("Aprovando mercado com ID: {}", id);

        Mercado mercado = getMercadoById(id);
        mercado.setAprovado(true);
        mercadoRepository.save(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                null, // Admin action
                "UPDATE",
                "MERCADO",
                id,
                "Mercado aprovado: " + mercado.getNome(),
                LocalDateTime.now()
        ));

        log.info("Mercado aprovado com sucesso. ID: {}", id);
    }

    /**
     * Rejeita um mercado (apenas admin).
     *
     * @param id ID do mercado
     * @param motivo motivo da rejeição
     * @throws ResourceNotFoundException se mercado não existe
     */
    public void rejeitarMercado(Long id, String motivo) {
        log.info("Rejeitando mercado com ID: {}", id);

        Mercado mercado = getMercadoById(id);
        mercado.setAprovado(false);
        mercadoRepository.save(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                null, // Admin action
                "UPDATE",
                "MERCADO",
                id,
                "Mercado rejeitado. Motivo: " + motivo,
                LocalDateTime.now()
        ));

        log.info("Mercado rejeitado com sucesso. ID: {}", id);
    }

    /**
     * Atualiza a avaliação média de um mercado (chamado internamente).
     *
     * @param mercadoId ID do mercado
     */
    @Transactional
    public void atualizarAvaliacaoMedia(Long mercadoId) {
        log.debug("Atualizando avaliação média do mercado ID: {}", mercadoId);

        Mercado mercado = getMercadoById(mercadoId);

        // Calcular nova média e total (seria feito via query no repository)
        Double novaMedia = mercadoRepository.calcularAvaliacaoMedia(mercadoId);
        Long totalAvaliacoes = mercadoRepository.contarAvaliacoes(mercadoId);

        mercado.setAvaliacaoMedia(novaMedia != null ? novaMedia : 0.0);
        mercado.setTotalAvaliacoes(totalAvaliacoes != null ? totalAvaliacoes : 0L);

        mercadoRepository.save(mercado);

        log.debug("Avaliação média atualizada. Mercado ID: {}, Nova média: {}", mercadoId, novaMedia);
    }

    /**
     * Verifica se um usuário é proprietário ou admin.
     *
     * @param user usuário a verificar
     * @param mercado mercado a verificar
     * @return true se é proprietário ou admin
     */
    private boolean isOwnerOrAdmin(User user, Mercado mercado) {
        return user.getId().equals(mercado.getOwner().getId()) ||
                user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
    }

    /**
     * Converte entidade Mercado para DTO MercadoResponse.
     *
     * @param mercado entidade Mercado
     * @return DTO MercadoResponse
     */
    private MercadoResponse convertToResponse(Mercado mercado) {
        return new MercadoResponse(
                mercado.getId(),
                mercado.getNome(),
                mercado.getDescricao(),
                mercado.getCidade(),
                mercado.getBairro(),
                mercado.getTelefone(),
                mercado.getLatitude(),
                mercado.getLongitude(),
                mercado.getAvaliacaoMedia(),
                mercado.getTotalAvaliacoes(),
                mercado.isAprovado(),
                mercado.getCreatedAt()
        );
    }
    public MercadoService() {
    }

    public MercadoService(MercadoRepository mercadoRepository, AuditLogRepository auditLogRepository, NotificacaoService notificacaoService) {
        this.mercadoRepository = mercadoRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificacaoService = notificacaoService;
    }

    public MercadoRepository getMercadoRepository() {
        return this.mercadoRepository;
    }

    public void setMercadoRepository(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

    public AuditLogRepository getAuditLogRepository() {
        return this.auditLogRepository;
    }

    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public NotificacaoService getNotificacaoService() {
        return this.notificacaoService;
    }

    public void setNotificacaoService(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

}
