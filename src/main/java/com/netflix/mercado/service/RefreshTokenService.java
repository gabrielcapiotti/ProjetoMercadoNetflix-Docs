package com.netflix.mercado.service;

import com.netflix.mercado.entity.RefreshToken;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.RefreshTokenRepository;
import com.netflix.mercado.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service responsável por gerenciar refresh tokens.
 * Implementa lógica de geração, validação, renovação e revogação de refresh tokens.
 */
@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token.expiration:604800000}") // 7 dias em ms
    private Long refreshTokenExpiration;

    /**
     * Cria um novo refresh token para um usuário.
     *
     * @param user usuário para o qual criar o token
     * @return o refresh token criado
     */
    public RefreshToken criarRefreshToken(User user) {
        log.info("Criando refresh token para usuário: {}", user.getEmail());

        // Revogar tokens anteriores se existirem
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setDataCriacao(LocalDateTime.now());
        refreshToken.setDataExpiracao(LocalDateTime.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setRevogado(false);

        refreshToken = refreshTokenRepository.save(refreshToken);

        log.info("Refresh token criado com sucesso para usuário: {}", user.getEmail());
        return refreshToken;
    }

    /**
     * Obtém um refresh token pelo seu valor.
     *
     * @param token valor do token
     * @return o refresh token encontrado
     * @throws ResourceNotFoundException se token não existe
     */
    @Transactional(readOnly = true)
    public RefreshToken obterRefreshToken(String token) {
        log.debug("Buscando refresh token");

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token não encontrado");
                    return new ResourceNotFoundException("Refresh token não encontrado");
                });
    }

    /**
     * Valida um refresh token.
     *
     * @param token token a validar
     * @return true se token é válido
     */
    @Transactional(readOnly = true)
    public boolean validarRefreshToken(String token) {
        log.debug("Validando refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElse(null);

        if (refreshToken == null) {
            log.warn("Refresh token não encontrado");
            return false;
        }

        // Verificar se foi revogado
        if (refreshToken.isRevogado()) {
            log.warn("Refresh token foi revogado");
            return false;
        }

        // Verificar se expirou
        if (refreshToken.getDataExpiracao().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expirou");
            return false;
        }

        return true;
    }

    /**
     * Renova um access token usando um refresh token.
     *
     * @param refreshTokenString valor do refresh token
     * @return novo access token
     * @throws ValidationException se token inválido ou expirado
     */
    public String renovarAccessToken(String refreshTokenString) {
        log.info("Renovando access token");

        if (!validarRefreshToken(refreshTokenString)) {
            log.warn("Tentativa de renovação com refresh token inválido");
            throw new ValidationException("Refresh token inválido ou expirado");
        }

        RefreshToken refreshToken = obterRefreshToken(refreshTokenString);
        User user = refreshToken.getUser();

        String novoAccessToken = jwtTokenProvider.generateToken(user);

        log.info("Access token renovado com sucesso para usuário: {}", user.getEmail());
        return novoAccessToken;
    }

    /**
     * Revoga um refresh token.
     *
     * @param token token a revogar
     * @throws ResourceNotFoundException se token não existe
     */
    public void revogarRefreshToken(String token) {
        log.info("Revogando refresh token");

        RefreshToken refreshToken = obterRefreshToken(token);
        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token revogado com sucesso");
    }

    /**
     * Revoga todos os refresh tokens de um usuário.
     *
     * @param user usuário cujos tokens devem ser revogados
     */
    public void revogarTodosOsTokensDoUsuario(User user) {
        log.info("Revogando todos os refresh tokens do usuário: {}", user.getEmail());

        refreshTokenRepository.findByUserId(user.getId())
                .forEach(token -> {
                    token.setRevogado(true);
                    refreshTokenRepository.save(token);
                });

        log.info("Todos os refresh tokens revogados para usuário: {}", user.getEmail());
    }

    /**
     * Limpa refresh tokens expirados (executado periodicamente).
     */
    @Scheduled(cron = "0 0 3 * * *") // Executar diariamente às 3 da manhã
    @Transactional
    public void limparTokensExpirados() {
        log.info("Iniciando limpeza de refresh tokens expirados");

        long deletados = refreshTokenRepository.deleteByDataExpiracaoBefore(LocalDateTime.now());

        log.info("Limpeza de refresh tokens concluída. {} tokens deletados", deletados);
    }

    /**
     * Verifica e limpa tokens expirados de um usuário específico.
     *
     * @param userId ID do usuário
     */
    @Transactional
    public void limparTokensExpiradosDoUsuario(Long userId) {
        log.debug("Limpando tokens expirados do usuário ID: {}", userId);

        refreshTokenRepository.deleteByUserIdAndDataExpiracaoBefore(userId, LocalDateTime.now());

        log.debug("Tokens expirados deletados para usuário ID: {}", userId);
    }

    /**
     * Obtém o tempo de expiração restante de um refresh token.
     *
     * @param token valor do token
     * @return minutos até expiração
     * @throws ResourceNotFoundException se token não existe
     */
    @Transactional(readOnly = true)
    public Long obterTempoExpiracaoRestante(String token) {
        log.debug("Calculando tempo de expiração restante do token");

        RefreshToken refreshToken = obterRefreshToken(token);

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime expiracao = refreshToken.getDataExpiracao();

        return ChronoUnit.MINUTES.between(agora, expiracao);
    }
    public RefreshTokenService() {
    }

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider, Long refreshTokenExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public RefreshTokenRepository getRefreshTokenRepository() {
        return this.refreshTokenRepository;
    }

    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public JwtTokenProvider getJwtTokenProvider() {
        return this.jwtTokenProvider;
    }

    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long getRefreshTokenExpiration() {
        return this.refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

}
