package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação customizada de email.
 * Complementa @Email padrão com validações mais rigorosas RFC 5322.
 * Rejeita emails conhecidos de teste.
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {

    String message() default "Email inválido. Verifique o formato e o domínio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
