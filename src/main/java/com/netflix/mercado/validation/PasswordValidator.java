package com.netflix.mercado.validation;

import lombok.extern.slf4j.Slf4j;
import com.netflix.mercado.validation.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validador customizado para senha.
 * Valida:
 * - Comprimento (8-100 caracteres)
 * - Presença de letra maiúscula
 * - Presença de letra minúscula
 * - Presença de número
 * - Presença de caractere especial (!@#$%^&*)
 * - Rejeita senhas comuns
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Slf4j
@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;

    // Padrões para validação
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>?/\\\\|`~]");

    // Senhas comuns ou fracas a rejeitar
    private static final Set<String> WEAK_PASSWORDS = new HashSet<>();

    static {
        WEAK_PASSWORDS.add("123456");
        WEAK_PASSWORDS.add("password");
        WEAK_PASSWORDS.add("12345678");
        WEAK_PASSWORDS.add("qwerty");
        WEAK_PASSWORDS.add("abc123");
        WEAK_PASSWORDS.add("password123");
        WEAK_PASSWORDS.add("123123123");
        WEAK_PASSWORDS.add("111111");
        WEAK_PASSWORDS.add("000000");
        WEAK_PASSWORDS.add("admin123");
        WEAK_PASSWORDS.add("letmein");
        WEAK_PASSWORDS.add("trustno1");
        WEAK_PASSWORDS.add("1234567");
        WEAK_PASSWORDS.add("welcome");
        WEAK_PASSWORDS.add("monkey");
        WEAK_PASSWORDS.add("dragon");
        WEAK_PASSWORDS.add("master");
        WEAK_PASSWORDS.add("sunshine");
        WEAK_PASSWORDS.add("princess");
        WEAK_PASSWORDS.add("qwertyuiop");
        WEAK_PASSWORDS.add("654321");
        WEAK_PASSWORDS.add("superman");
        WEAK_PASSWORDS.add("batman");
        WEAK_PASSWORDS.add("iloveyou");
        WEAK_PASSWORDS.add("starwars");
    }

    @Override
    public void initialize(ValidPassword annotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validação nula é delegada a @NotNull
        if (value == null) {
            return true;
        }

        try {
            return isValidPassword(value, context);
        } catch (Exception e) {
            log.error("Erro ao validar senha: {}", e.getMessage());
            addConstraintViolation(context, "Erro ao validar senha");
            return false;
        }
    }

    /**
     * Valida a senha de forma completa.
     *
     * @param password o password a ser validado
     * @param context  contexto de validação
     * @return true se válido, false caso contrário
     */
    private boolean isValidPassword(String password, ConstraintValidatorContext context) {
        // Valida tamanho mínimo
        if (password.length() < MIN_LENGTH) {
            addConstraintViolation(context, "Senha deve conter no mínimo 8 caracteres");
            return false;
        }

        // Valida tamanho máximo
        if (password.length() > MAX_LENGTH) {
            addConstraintViolation(context, "Senha deve conter no máximo 100 caracteres");
            return false;
        }

        // Rejeita senhas comuns
        if (WEAK_PASSWORDS.contains(password.toLowerCase())) {
            addConstraintViolation(context, "Senha muito comum e fraca, escolha uma mais segura");
            return false;
        }

        // Valida presença de letra maiúscula
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "Senha deve conter pelo menos uma letra MAIÚSCULA");
            return false;
        }

        // Valida presença de letra minúscula
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "Senha deve conter pelo menos uma letra minúscula");
            return false;
        }

        // Valida presença de número
        if (!DIGIT_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "Senha deve conter pelo menos um número (0-9)");
            return false;
        }

        // Valida presença de caractere especial
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "Senha deve conter pelo menos um caractere especial (!@#$%^&*)");
            return false;
        }

        // Validações adicionais de segurança
        if (!isSecurePassword(password)) {
            addConstraintViolation(context, "Senha não atende aos critérios de segurança");
            return false;
        }

        log.debug("Senha validada com sucesso (comprimento: {})", password.length());
        return true;
    }

    /**
     * Validações adicionais de segurança.
     *
     * @param password a senha a ser validada
     * @return true se passar nas validações adicionais
     */
    private boolean isSecurePassword(String password) {
        // Rejeita senhas com muitos caracteres repetidos
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) &&
                password.charAt(i) == password.charAt(i + 2)) {
                return false;
            }
        }

        // Rejeita senhas com sequências óbvias
        if (password.matches(".*(?:abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz).*") ||
            password.matches(".*(?:123|234|345|456|567|678|789|890).*")) {
            return false;
        }

        // Rejeita senhas que começam ou terminam com sequências numéricas comuns
        if (password.matches("^(19[0-9]{2}|20[0-2][0-9]).*") ||
            password.matches(".*(19[0-9]{2}|20[0-2][0-9])$")) {
            return false;
        }

        return true;
    }

    /**
     * Adiciona uma violação de constraint personalizada ao contexto.
     *
     * @param context contexto de validação
     * @param message mensagem de erro
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
