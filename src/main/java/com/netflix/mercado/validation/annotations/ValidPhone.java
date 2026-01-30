package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação customizada de número telefônico.
 * Valida formato brasileiro com DDD (11-99) e tamanho apropriado.
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneValidator.class)
public @interface ValidPhone {

    String message() default "Telefone inválido. Formatos aceitos: +55 11 99999-9999 ou 11 99999999 ou 11999999999";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
