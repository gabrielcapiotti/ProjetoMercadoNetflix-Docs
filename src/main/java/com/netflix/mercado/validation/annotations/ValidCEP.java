package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.CEPValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validação customizada de CEP (Código de Endereçamento Postal).
 * Valida formato e tamanho do CEP brasileiro.
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CEPValidator.class)
public @interface ValidCEP {

    String message() default "CEP inválido. Formato esperado: 12345-678 ou 12345678";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
