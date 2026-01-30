package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação customizada de senha.
 * Valida comprimento, complexidade e rejeita senhas comuns.
 * Requisitos:
 * - Mínimo 8 caracteres, máximo 100
 * - Pelo menos 1 letra maiúscula
 * - Pelo menos 1 letra minúscula
 * - Pelo menos 1 número
 * - Pelo menos 1 caractere especial (!@#$%^&*)
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {

    String message() default "Senha fraca. Requerimentos: 8-100 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 caractere especial (!@#$%^&*)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
