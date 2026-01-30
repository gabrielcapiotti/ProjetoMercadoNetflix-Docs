package com.netflix.mercado.security;

import lombok.extern.slf4j.Slf4j;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    private final UserRepository userRepository;

    /**
     * Carrega os detalhes do usuário baseado no email (username)
     * Implementa o contrato UserDetailsService do Spring Security
     *
     * @param email Email do usuário (usado como username único)
     * @return UserDetails contendo as informações do usuário e suas roles
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Carregando usuário com email: {}", email);

        // Buscar usuário no banco de dados
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado com email: {}", email);
                return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
            });

        // Verificar se o usuário está ativo
        if (!user.isActive()) {
            log.warn("Tentativa de login com usuário inativo: {}", email);
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        log.debug("Usuário carregado com sucesso: {} com roles: {}", email, user.getRoles());

        // Construir e retornar UserDetails com as informações do usuário
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .enabled(user.isActive())
            .accountNonExpired(true)
            .accountNonLocked(!user.isLocked())
            .credentialsNonExpired(true)
            .authorities(mapRolesToAuthorities(user))
            .build();
    }

    /**
     * Carrega os detalhes do usuário baseado no ID
     * Método auxiliar para ser usado em outros serviços
     *
     * @param userId ID do usuário
     * @return UserDetails do usuário
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Carregando usuário com ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado com ID: {}", userId);
                return new UsernameNotFoundException("Usuário não encontrado com ID: " + userId);
            });

        return loadUserByUsername(user.getEmail());
    }

    /**
     * Converte os roles do usuário em GrantedAuthorities do Spring Security
     * Prepara os roles no formato esperado pelo Spring Security (com prefixo ROLE_)
     *
     * @param user Entidade User
     * @return Collection de GrantedAuthority
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.debug("Usuário {} sem roles, atribuindo role padrão USER", user.getEmail());
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return user.getRoles().stream()
            .map(role -> {
                String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                return new SimpleGrantedAuthority(roleName);
            })
            .collect(Collectors.toSet());
    }

    /**
     * Valida se um usuário tem uma determinada role
     * Método auxiliar para verificações de autorização
     *
     * @param email Email do usuário
     * @param role Role a ser verificada
     * @return true se o usuário possui a role, false caso contrário
     */
    @Transactional(readOnly = true)
    public boolean userHasRole(String email, String role) {
        try {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

            String roleName = role.startsWith("ROLE_") ? role.substring(5) : role;
            return user.getRoles() != null && user.getRoles().contains(roleName);
        } catch (Exception e) {
            log.error("Erro ao verificar role do usuário: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Recarrega as informações do usuário do banco de dados
     * Útil para atualizar dados de sessão após mudanças
     *
     * @param userDetails UserDetails atual
     * @return UserDetails atualizado
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Transactional(readOnly = true)
    public UserDetails reloadUser(UserDetails userDetails) throws UsernameNotFoundException {
        return loadUserByUsername(userDetails.getUsername());
    }
}
