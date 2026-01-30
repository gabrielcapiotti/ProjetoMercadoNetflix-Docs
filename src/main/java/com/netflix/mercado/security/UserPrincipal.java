package com.netflix.mercado.security;

import com.netflix.mercado.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementação customizada de UserDetails do Spring Security
 * Encapsula as informações do usuário para uso na autenticação e autorização
 */
@Data
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    /**
     * Cria um UserPrincipal a partir de uma entidade User
     *
     * @param user Entidade User
     * @return UserPrincipal configurado
     */
    public static UserPrincipal create(User user) {
        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toSet());

        return UserPrincipal.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .enabled(user.isActive())
            .accountNonExpired(true)
            .accountNonLocked(!user.isLocked())
            .credentialsNonExpired(true)
            .build();
    }

    /**
     * Retorna o nome de usuário (email neste caso)
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Retorna a senha do usuário
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Retorna as authorities (roles) do usuário
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * Indica se a conta do usuário não expirou
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    /**
     * Indica se a conta do usuário não está bloqueada
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    /**
     * Indica se as credenciais do usuário não expiraram
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    /**
     * Indica se o usuário está habilitado
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
