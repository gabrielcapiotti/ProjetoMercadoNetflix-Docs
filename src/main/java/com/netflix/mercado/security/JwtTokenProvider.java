package com.netflix.mercado.security;

import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger log = Logger.getLogger(JwtTokenProvider.class.getName());

    @Value("${jwt.secret:my-secret-key-for-jwt-token-provider-please-change-in-production}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationMs;

    /**
     * Gera um JWT token a partir da Authentication (1 hora de expiração)
     *
     * @param authentication A autenticação do usuário
     * @return JWT token assinado
     */
    public String generateToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        return generateTokenFromUsername(user.getUsername(), authentication.getAuthorities(), null);
    }

    /**
     * Gera um token a partir de um objeto User (sobrecarga para compatibilidade)
     *
     * @param user Objeto User da entidade
     * @return JWT token assinado
     */
    public String generateToken(com.netflix.mercado.entity.User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> (GrantedAuthority) () -> role.getName().name())
            .collect(Collectors.toList());
        
        return generateTokenFromUsername(user.getEmail(), authorities, user.getId());
    }

    /**
     * Gera um refresh token para o usuário (7 dias de expiração)
     *
     * @param username Nome do usuário
     * @return Refresh token assinado
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date refreshExpiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(refreshExpiryDate)
            .claim("type", "refresh")
            .signWith(key(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Gera um access token com todas as informações necessárias
     *
     * @param username Nome do usuário
     * @param authorities Autoridades/roles do usuário
     * @param userId ID do usuário
     * @return Access token assinado
     */
    public String generateTokenFromUsername(String username, 
                                           Collection<? extends GrantedAuthority> authorities,
                                           Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .claim("roles", roles)
            .claim("type", "access")
            .signWith(key(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Extrai o username do JWT token
     *
     * @param token JWT token
     * @return Username contido no token
     */
    public String extractUsername(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warning("Token expirado ao extrair username: " + e.getMessage());
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            log.severe("Erro ao extrair username do token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrai os roles do JWT token
     *
     * @param token JWT token
     * @return Lista de roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (List<String>) claims.get("roles", List.class);
        } catch (Exception e) {
            log.severe("Erro ao extrair roles do token: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Valida se o JWT token é válido e não expirou
     *
     * @param token JWT token
     * @return true se o token é válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.severe("Chave JWT inválida: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.severe("JWT token inválido: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            log.severe("JWT token expirou: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.severe("JWT token não suportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.severe("JWT claims string vazio: " + e.getMessage());
        } catch (JwtException e) {
            log.severe("Erro ao validar JWT token: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtém a data de expiração do token
     *
     * @param token JWT token
     * @return Data de expiração
     */
    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        } catch (Exception e) {
            log.severe("Erro ao extrair data de expiração: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica se o token está expirado
     *
     * @param token JWT token
     * @return true se expirado, false caso contrário
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            log.severe("Erro ao verificar expiração do token: " + e.getMessage());
            return true;
        }
    }

    /**
     * Extrai todos os claims do token
     *
     * @param token JWT token
     * @return Claims do token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(key())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Gera a chave de assinatura a partir da string secreta
     *
     * @return SecretKey para assinar tokens
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Obtém o tempo de expiração do token em milissegundos
     *
     * @return Tempo de expiração configurado
     */
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    /**
     * Obtém o tempo de expiração do refresh token em milissegundos
     *
     * @return Tempo de expiração do refresh token
     */
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    /**
     * Obtém o tempo de expiração do token em segundos (para compatibilidade com AuthService)
     *
     * @return Tempo de expiração em segundos
     */
    public long getJwtExpirationTime() {
        return jwtExpirationMs / 1000;
    }
}
