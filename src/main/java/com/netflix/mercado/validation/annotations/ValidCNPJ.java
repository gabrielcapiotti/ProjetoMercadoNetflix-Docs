package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.CNPJValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação customizada de CNPJ.
 * Valida formato, tamanho e dígitos verificadores.
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CNPJValidator.class)
public @interface ValidCNPJ {

    String message() default "CNPJ inválido. Formato esperado: 00.000.000/0000-00 ou 00000000000000";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
