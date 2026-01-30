package com.netflix.mercado.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
