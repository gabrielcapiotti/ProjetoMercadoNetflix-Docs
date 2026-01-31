package com.netflix.mercado.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }


    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    /**
     * Filtra cada requisição para validar JWT token
     * Executado uma única vez por requisição
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extrair JWT do header Authorization
            String jwt = extractJwtFromRequest(request);

            // 2. Se o token existe e é válido, processar a autenticação
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                authenticateUser(jwt);
            }

        } catch (Exception e) {
            log.severe("Erro ao processar autenticação JWT: " + e.getMessage());
            // Continuar a cadeia de filtros mesmo com erro para que o controlador
            // de exceção global possa tratar a requisição não autenticada
        }

        // 3. Continuar a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o JWT token do header Authorization
     * Esperado formato: Authorization: Bearer <token>
     *
     * @param request HttpServletRequest
     * @return JWT token ou null se não encontrado
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX_LENGTH);
        }

        log.fine("Token JWT não encontrado no header Authorization");
        return null;
    }

    /**
     * Autentica o usuário baseado no JWT token
     * Extrai o username, carrega seus detalhes e cria um objeto Authentication
     *
     * @param token JWT token validado
     */
    private void authenticateUser(String token) {
        try {
            // 1. Extrair username do token
            String username = tokenProvider.extractUsername(token);

            if (!StringUtils.hasText(username)) {
                log.warning("Username não encontrado no token");
                return;
            }

            // 2. Carregar detalhes do usuário
            var userDetails = userDetailsService.loadUserByUsername(username);

            // 3. Extrair roles do token
            List<String> roles = tokenProvider.extractRoles(token);

            // 4. Converter roles para GrantedAuthority
            var authorities = roles != null ? 
                roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()) :
                userDetails.getAuthorities().stream()
                    .collect(Collectors.toList());

            // 5. Criar token de autenticação
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
                );

            // 6. Setar no SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.fine("Autenticação JWT bem-sucedida para usuário: " + username + "");

        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.severe("Usuário não encontrado: " + e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.severe("Erro ao processar autenticação do usuário: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Determina se o filtro deve ser aplicado a essa requisição
     * Pula o filtro para endpoints públicos como login/register
     *
     * @param request HttpServletRequest
     * @return true para processar o filtro, false para pular
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Pular filtro para endpoints públicos
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.equals("/actuator/health");
    }
}
