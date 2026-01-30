package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.CPFValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação customizada de CPF.
 * Valida formato, tamanho e dígitos verificadores.
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CPFValidator.class)
public @interface ValidCPF {

    String message() default "CPF inválido. Formato esperado: 000.000.000-00 ou 00000000000";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
