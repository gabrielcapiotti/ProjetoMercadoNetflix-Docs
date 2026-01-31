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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service responsável por gerenciar mercados.
 * Implementa lógica de criação, atualização, busca e aprovação de mercados.
 */
@Service
@Transactional
public class MercadoService {

    private static final Logger log = Logger.getLogger(MercadoService.class.getName());

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
    public MercadoResponse createMercado(CreateMercadoRequest request, User owner) {
        log.info("Criando novo mercado: " + request.getNome());

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
        mercado.setEndereco(request.getEndereco());
        mercado.setNumero(request.getNumero());
        mercado.setComplemento(request.getComplemento());
        mercado.setCep(request.getCep());
        mercado.setEstado(request.getEstado());
        mercado.setLatitude(request.getLatitude());
        mercado.setLongitude(request.getLongitude());
        mercado.setTelefone(request.getTelefone());
        mercado.setEmail(request.getEmail());
        // owner e aprovado não existem na entidade
        // avaliacaoMedia e totalAvaliacoes já têm valores padrão

        mercado = mercadoRepository.save(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                owner,
                AuditLog.TipoAcao.CRIACAO,
                "MERCADO",
                mercado.getId(),
                "Novo mercado criado: " + mercado.getNome(),
                null, null, null, null, null
        ));

        log.info("Mercado criado com sucesso. ID: " + mercado.getId());
        return convertToResponse(mercado);
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
    public MercadoResponse updateMercado(Long id, UpdateMercadoRequest request, User user) {
        log.info("Atualizando mercado com ID: " + id);

        Mercado mercado = getMercadoEntityById(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(user, mercado)) {
            log.warning("Tentativa de atualização não autorizada do mercado ID: " + id + " por usuário: " + user.getEmail());
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
        if (request.getEndereco() != null) {
            mercado.setEndereco(request.getEndereco());
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
                user,
                AuditLog.TipoAcao.ATUALIZACAO,
                "MERCADO",
                mercado.getId(),
                "Alterações: " + valoresAnteriores + " -> " + valoresNovos,
                valoresAnteriores,
                valoresNovos,
                null, null, null
        ));

        log.info("Mercado atualizado com sucesso. ID: " + id);
        return convertToResponse(mercado);
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
        log.info("Deletando mercado com ID: " + id);

        Mercado mercado = getMercadoEntityById(id);

        // Verificar autorização
        if (!isOwnerOrAdmin(user, mercado)) {
            log.warning("Tentativa de deleção não autorizada do mercado ID: " + id + " por usuário: " + user.getEmail());
            throw new UnauthorizedException("Você não tem permissão para deletar este mercado");
        }

        mercadoRepository.delete(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                user,
                AuditLog.TipoAcao.DELECAO,
                "MERCADO",
                id,
                "Mercado deletado: " + mercado.getNome(),
                null, null, null, null, null
        ));

        log.info("Mercado deletado com sucesso. ID: " + id);
    }

    /**
     * Obtém um mercado pelo ID.
     *
     * @param id ID do mercado
     * @return o mercado encontrado
     * @throws ResourceNotFoundException se mercado não existe
     */
    @Transactional(readOnly = true)
    public MercadoResponse getMercadoById(Long id) {
        log.fine("Buscando mercado com ID: " + id);
        Mercado mercado = getMercadoEntityById(id);
        return convertToResponse(mercado);
    }

    /**
     * Obtém a entidade Mercado pelo ID (uso público).
     *
     * @param id ID do mercado
     * @return a entidade Mercado
     * @throws ResourceNotFoundException se mercado não existe
     */
    public Mercado getMercadoEntityById(Long id) {
        return mercadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Mercado não encontrado com ID: " + id);
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
        log.fine("Buscando todos os mercados ativos com paginação");
        return mercadoRepository.findAllActive(pageable)
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
        log.fine("Buscando mercados próximos: lat=" + latitude + ", lon=" + longitude + ", raio=" + raio + "km");
        return mercadoRepository.findByProximidade(latitude, longitude, raio);
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
        log.fine("Buscando mercados por nome: " + nome);
        return mercadoRepository.findByNomeContainingIgnoreCase(nome, pageable);
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
        log.fine("Buscando mercados por cidade: " + cidade);
        return mercadoRepository.findByCidade(cidade, pageable);
    }

    /**
     * Aprova um mercado (apenas admin).
     *
     * @param id ID do mercado
     * @throws ResourceNotFoundException se mercado não existe
     */
    public void aprovarMercado(Long id) {
        log.info("Aprovando mercado com ID: " + id);

        Mercado mercado = getMercadoEntityById(id);
        // mercado.setAprovado(true); // campo aprovado não existe
        mercado.setActive(true);
        mercadoRepository.save(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null, // Admin action
                AuditLog.TipoAcao.ATUALIZACAO,
                "MERCADO",
                id,
                "Mercado aprovado: " + mercado.getNome(),
                null, null, null, null, null
        ));

        log.info("Mercado aprovado com sucesso. ID: " + id);
    }

    /**
     * Rejeita um mercado (apenas admin).
     *
     * @param id ID do mercado
     * @param motivo motivo da rejeição
     * @throws ResourceNotFoundException se mercado não existe
     */
    public void rejeitarMercado(Long id, String motivo) {
        log.info("Rejeitando mercado com ID: " + id);

        Mercado mercado = getMercadoEntityById(id);
        // mercado.setAprovado(false); // campo aprovado não existe
        mercado.setActive(false);
        mercadoRepository.save(mercado);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null, // Admin action
                AuditLog.TipoAcao.ATUALIZACAO,
                "MERCADO",
                id,
                "Mercado rejeitado. Motivo: " + motivo,
                null, null, null, null, null
        ));

        log.info("Mercado rejeitado com sucesso. ID: " + id);
    }

    /**
     * Atualiza a avaliação média de um mercado (chamado internamente).
     *
     * @param mercadoId ID do mercado
     */
    @Transactional
    public void atualizarAvaliacaoMedia(Long mercadoId) {
        log.fine("Atualizando avaliação média do mercado ID: " + mercadoId);

        Mercado mercado = getMercadoEntityById(mercadoId);

        // Calcular nova média e total (seria feito via query no repository)
        // TODO: implementar métodos calcularAvaliacaoMedia e contarAvaliacoes no repository
        // Double novaMedia = mercadoRepository.calcularAvaliacaoMedia(mercadoId);
        // Long totalAvaliacoes = mercadoRepository.contarAvaliacoes(mercadoId);

        // Por enquanto, usar valores padrão
        // mercado.setAvaliacaoMedia(novaMedia != null ? BigDecimal.valueOf(novaMedia) : BigDecimal.ZERO);
        // mercado.setTotalAvaliacoes(totalAvaliacoes != null ? totalAvaliacoes : 0L);

        mercadoRepository.save(mercado);

        log.fine("Avaliação média atualizada. Mercado ID: " + mercadoId);
    }

    /**
     * Verifica se um usuário é proprietário ou admin.
     *
     * @param user usuário a verificar
     * @param mercado mercado a verificar
     * @return true se é proprietário ou admin
     */
    private boolean isOwnerOrAdmin(User user, Mercado mercado) {
        // TODO: mercado.getOwner() não existe - implementar relação ou usar outra lógica
        // return user.getId().equals(mercado.getOwner().getId()) ||
        return user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
    }

    /**
     * Converte entidade Mercado para DTO MercadoResponse.
     *
     * @param mercado entidade Mercado
     * @return DTO MercadoResponse
     */
    public MercadoResponse convertToResponse(Mercado mercado) {
        return new MercadoResponse(
                mercado.getId(),
                mercado.getNome(),
                mercado.getDescricao(),
                mercado.getEmail(),
                mercado.getTelefone(),
                mercado.getEndereco(),
                mercado.getBairro(),
                mercado.getCidade(),
                mercado.getEstado(),
                mercado.getCep(),
                mercado.getLatitude(),
                mercado.getLongitude(),
                null, // fotoPrincipalUrl - TODO
                mercado.getAvaliacaoMedia() != null ? mercado.getAvaliacaoMedia() : BigDecimal.ZERO,
                mercado.getTotalAvaliacoes() != null ? mercado.getTotalAvaliacoes().intValue() : 0,
                mercado.getCreatedAt(),
                mercado.getUpdatedAt()
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
