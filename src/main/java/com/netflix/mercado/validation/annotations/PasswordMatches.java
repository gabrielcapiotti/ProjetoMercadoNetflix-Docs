package com.netflix.mercado.validation.annotations;

import com.netflix.mercado.validation.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotação para validar se dois campos de senha combinam
 * Usada em classes que possuem password e confirmPassword
 *
 * @author System
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    
    String message() default "As senhas não combinam";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Nome do campo de senha
     */
    String password() default "password";
    
    /**
     * Nome do campo de confirmação de senha
     */
    String passwordConfirm() default "confirmPassword";
}
