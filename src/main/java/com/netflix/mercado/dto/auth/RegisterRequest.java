package com.netflix.mercado.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para registro de novo usuário")
public class RegisterRequest {

    @NotBlank(message = "Username não pode estar em branco")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Schema(description = "Nome de usuário único", example = "joao.silva")
    private String username;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Schema(description = "Senha com letras, números e caracteres especiais", example = "Senha@123")
    private String password;

    @NotBlank(message = "Confirmação de senha não pode estar em branco")
    @Schema(description = "Confirmação da senha", example = "Senha@123")
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @NotBlank(message = "Nome completo não pode estar em branco")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva Santos")
    private String fullName;
}
