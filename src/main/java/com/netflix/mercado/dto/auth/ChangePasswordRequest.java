package com.netflix.mercado.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
