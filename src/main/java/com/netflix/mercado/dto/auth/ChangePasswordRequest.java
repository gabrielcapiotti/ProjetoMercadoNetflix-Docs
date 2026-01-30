package com.netflix.mercado.dto.auth;

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
@Schema(description = "Requisição para alterar senha do usuário")
public class ChangePasswordRequest {

    @NotBlank(message = "Senha atual não pode estar em branco")
    @Schema(description = "Senha atual do usuário", example = "SenhaAntiga@123")
    @JsonProperty("oldPassword")
    private String oldPassword;

    @NotBlank(message = "Nova senha não pode estar em branco")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Schema(description = "Nova senha", example = "NovaSenh@456")
    @JsonProperty("newPassword")
    private String newPassword;

    @NotBlank(message = "Confirmação de senha não pode estar em branco")
    @Schema(description = "Confirmação da nova senha", example = "NovaSenh@456")
    @JsonProperty("confirmPassword")
    private String confirmPassword;
}
