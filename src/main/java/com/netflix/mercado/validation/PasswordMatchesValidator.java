package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Validador customizado para verificar se dois campos de senha combinam.
 * Usado para validar que password e confirmPassword são iguais em DTOs
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private static final Logger log = Logger.getLogger(PasswordMatchesValidator.class.getName());

    private String passwordFieldName;
    private String passwordConfirmFieldName;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.passwordFieldName = constraintAnnotation.password();
        this.passwordConfirmFieldName = constraintAnnotation.passwordConfirm();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            // Obter valores dos campos
            Field passwordField = value.getClass().getDeclaredField(passwordFieldName);
            Field passwordConfirmField = value.getClass().getDeclaredField(passwordConfirmFieldName);

            passwordField.setAccessible(true);
            passwordConfirmField.setAccessible(true);

            Object passwordValue = passwordField.get(value);
            Object passwordConfirmValue = passwordConfirmField.get(value);

            // Ambos nulos é válido (delegado a @NotNull se necessário)
            if (passwordValue == null && passwordConfirmValue == null) {
                return true;
            }

            // Um nulo e outro não é inválido
            if (passwordValue == null || passwordConfirmValue == null) {
                addConstraintViolation(context, "As senhas não podem ser nulas");
                return false;
            }

            // Verificar se combinam
            boolean matches = passwordValue.toString().equals(passwordConfirmValue.toString());

            if (!matches) {
                addConstraintViolation(context, "As senhas não combinam");
            }

            return matches;

        } catch (NoSuchFieldException e) {
            log.severe("Campo não encontrado na classe: " + e.getMessage());
            addConstraintViolation(context, "Erro ao validar senhas - campos não encontrados");
            return false;
        } catch (IllegalAccessException e) {
            log.severe("Erro ao acessar campo: " + e.getMessage());
            addConstraintViolation(context, "Erro ao validar senhas");
            return false;
        }
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
