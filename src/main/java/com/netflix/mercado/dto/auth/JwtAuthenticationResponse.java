package com.netflix.mercado.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com tokens JWT de autenticação")
public class JwtAuthenticationResponse {

    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("accessToken")
    private String accessToken;

    @Schema(description = "Token de refresh JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("refreshToken")
    private String refreshToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    @JsonProperty("tokenType")
    private String tokenType;

    @Schema(description = "Tempo de expiração em segundos", example = "3600")
    @JsonProperty("expiresIn")
    private Long expiresIn;

    @Schema(description = "Dados do usuário autenticado")
    private UserResponse user;
}
