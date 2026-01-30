package com.netflix.mercado.service;

import com.netflix.mercado.entity.Favorito;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.FavoritoRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.FavoritoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável por gerenciar favoritos de usuários.
 * Implementa lógica de adicionar, remover e consultar mercados favoritos.
 */
@Slf4j
@Service
@Transactional
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private MercadoService mercadoService;

    /**
     * Adiciona um mercado aos favoritos de um usuário.
     *
     * @param mercadoId ID do mercado
     * @param usuario usuário adicionando favorito
     * @return o favorito criado
     * @throws ValidationException se mercado já está nos favoritos
     */
    public Favorito adicionarFavorito(Long mercadoId, User usuario) {
        log.info("Adicionando mercado ID: {} aos favoritos do usuário: {}", mercadoId, usuario.getEmail());

        // Buscar mercado
        Mercado mercado = mercadoService.getMercadoById(mercadoId);

        // Verificar se já está nos favoritos
        if (favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId())) {
            log.warn("Mercado ID: {} já está nos favoritos do usuário: {}", mercadoId, usuario.getEmail());
            throw new ValidationException("Este mercado já está nos seus favoritos");
        }

        Favorito favorito = new Favorito();
        favorito.setMercado(mercado);
        favorito.setUsuario(usuario);
        favorito.setDataAdicao(LocalDateTime.now());
        favorito.setPrioridade(0);

        favorito = favoritoRepository.save(favorito);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "CREATE",
                "FAVORITO",
                favorito.getId(),
                "Mercado " + mercado.getNome() + " adicionado aos favoritos",
                LocalDateTime.now()
        ));

        log.info("Favorito adicionado com sucesso. ID: {}", favorito.getId());
        return favorito;
    }

    /**
     * Remove um mercado dos favoritos de um usuário.
     *
     * @param mercadoId ID do mercado
     * @param usuario usuário removendo favorito
     * @throws ResourceNotFoundException se favorito não existe
     */
    public void removerFavorito(Long mercadoId, User usuario) {
        log.info("Removendo mercado ID: {} dos favoritos do usuário: {}", mercadoId, usuario.getEmail());

        Favorito favorito = favoritoRepository.findByMercadoIdAndUsuarioId(mercadoId, usuario.getId())
                .orElseThrow(() -> {
                    log.warn("Favorito não encontrado para mercado ID: {} e usuário: {}", mercadoId, usuario.getEmail());
                    return new ResourceNotFoundException("Favorito não encontrado");
                });

        favoritoRepository.delete(favorito);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                null,
                usuario,
                "DELETE",
                "FAVORITO",
                favorito.getId(),
                "Favorito removido",
                LocalDateTime.now()
        ));

        log.info("Favorito removido com sucesso. Mercado ID: {}", mercadoId);
    }

    /**
     * Obtém todos os favoritos de um usuário com paginação.
     *
     * @param usuarioId ID do usuário
     * @param pageable informações de paginação
     * @return página de favoritos
     */
    @Transactional(readOnly = true)
    public Page<Favorito> obterFavoritosDUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando favoritos do usuário ID: {}", usuarioId);
        return favoritoRepository.findByUsuarioId(usuarioId, pageable);
    }

    /**
     * Verifica se um mercado está nos favoritos de um usuário.
     *
     * @param mercadoId ID do mercado
     * @param usuario usuário a verificar
     * @return true se está nos favoritos
     */
    @Transactional(readOnly = true)
    public Boolean verificarFavorito(Long mercadoId, User usuario) {
        log.debug("Verificando se mercado ID: {} está nos favoritos do usuário: {}", mercadoId, usuario.getEmail());
        return favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId());
    }

    /**
     * Conta o número de favoritos de um usuário.
     *
     * @param usuarioId ID do usuário
     * @return quantidade de favoritos
     */
    @Transactional(readOnly = true)
    public Long contarFavoritosDoUsuario(Long usuarioId) {
        log.debug("Contando favoritos do usuário ID: {}", usuarioId);
        return favoritoRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Conta o número de vezes que um mercado foi favoritado.
     *
     * @param mercadoId ID do mercado
     * @return quantidade de favoritos
     */
    @Transactional(readOnly = true)
    public Long contarFavoritosDomercado(Long mercadoId) {
        log.debug("Contando favoritos do mercado ID: {}", mercadoId);
        return favoritoRepository.countByMercadoId(mercadoId);
    }

    /**
     * Alterna o estado de favorito de um mercado.
     *
     * @param mercadoId ID do mercado
     * @param usuario usuário alternando favorito
     * @return true se foi adicionado, false se foi removido
     */
    public Boolean toggleFavorito(Long mercadoId, User usuario) {
        log.info("Alternando favorito para mercado ID: {} do usuário: {}", mercadoId, usuario.getEmail());

        if (verificarFavorito(mercadoId, usuario)) {
            // Remover favorito
            removerFavorito(mercadoId, usuario);
            return false;
        } else {
            // Adicionar favorito
            adicionarFavorito(mercadoId, usuario);
            return true;
        }
    }

    /**
     * Obtém os favoritos de um usuário ordenados por prioridade.
     *
     * @param usuarioId ID do usuário
     * @return lista de favoritos ordenados por prioridade
     */
    @Transactional(readOnly = true)
    public List<Favorito> obterFavoritosComPrioridade(Long usuarioId) {
        log.debug("Buscando favoritos do usuário ID: {} ordenados por prioridade", usuarioId);
        return favoritoRepository.findByUsuarioIdOrderByPrioridadeDescDataAdicaoDesc(usuarioId);
    }

    /**
     * Define a prioridade de um favorito.
     *
     * @param favoritoId ID do favorito
     * @param prioridade nível de prioridade (0-10)
     */
    public void definirPrioridade(Long favoritoId, Integer prioridade) {
        log.debug("Definindo prioridade do favorito ID: {} para: {}", favoritoId, prioridade);

        if (prioridade < 0 || prioridade > 10) {
            throw new ValidationException("Prioridade deve estar entre 0 e 10");
        }

        Favorito favorito = favoritoRepository.findById(favoritoId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito não encontrado"));

        favorito.setPrioridade(prioridade);
        favoritoRepository.save(favorito);

        log.debug("Prioridade do favorito ID: {} definida para: {}", favoritoId, prioridade);
    }
}
