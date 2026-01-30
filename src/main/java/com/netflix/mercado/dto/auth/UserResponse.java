package com.netflix.mercado.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Resposta com informações do usuário")
public class UserResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @Schema(description = "Username", example = "joao.silva")
    private String username;

    @Schema(description = "Nome completo", example = "João Silva Santos")
    @JsonProperty("fullName")
    private String fullName;

    @Schema(description = "Funções/roles do usuário", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean active;

    @Schema(description = "Data de criação do usuário")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de última atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public UserResponse() {
    }

    public UserResponse(Long id, String email, String username, String fullName, Set<String> roles, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.roles = roles;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
