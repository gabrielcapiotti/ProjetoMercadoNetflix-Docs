package com.netflix.mercado.validation;

import lombok.extern.slf4j.Slf4j;
import com.netflix.mercado.validation.annotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validador customizado para email.
 * Complementa @Email padrão com validações RFC 5322 mais rigorosas.
 * Valida:
 * - Formato RFC 5322 completo
 * - Tamanho máximo (254 caracteres)
 * - Domínio válido (não vazio, contém ponto)
 * - Rejeita emails conhecidos de teste
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Slf4j
@Component
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    // Regex RFC 5322 completo (simplificado mas robusto)
    // Aceita: user+tag@domain.co.uk, user.name@sub.domain.com, etc
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9][a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );

    // Emails conhecidos de teste (a rejeitar)
    private static final Set<String> INVALID_TEST_EMAILS = new HashSet<>();

    static {
        INVALID_TEST_EMAILS.add("test@test.com");
        INVALID_TEST_EMAILS.add("demo@demo.com");
        INVALID_TEST_EMAILS.add("user@example.com");
        INVALID_TEST_EMAILS.add("admin@admin.com");
        INVALID_TEST_EMAILS.add("test@example.com");
        INVALID_TEST_EMAILS.add("admin@example.com");
        INVALID_TEST_EMAILS.add("user@user.com");
        INVALID_TEST_EMAILS.add("test@localhost.com");
    }

    // Tamanho máximo de um email (RFC 5321)
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_LOCAL_PART_LENGTH = 64;

    @Override
    public void initialize(ValidEmail annotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validação nula é delegada a @NotNull
        if (value == null) {
            return true;
        }

        try {
            return isValidEmail(value, context);
        } catch (Exception e) {
            log.error("Erro ao validar email: {}", e.getMessage());
            addConstraintViolation(context, "Erro ao validar email");
            return false;
        }
    }

    /**
     * Valida o email de forma completa.
     *
     * @param email   o email a ser validado
     * @param context contexto de validação
     * @return true se válido, false caso contrário
     */
    private boolean isValidEmail(String email, ConstraintValidatorContext context) {
        // Remove espaços
        String trimmedEmail = email.trim();

        // Valida tamanho total
        if (trimmedEmail.length() > MAX_EMAIL_LENGTH) {
            addConstraintViolation(context, "Email muito longo (máximo 254 caracteres)");
            return false;
        }

        // Valida tamanho mínimo
        if (trimmedEmail.length() < 5) {
            addConstraintViolation(context, "Email muito curto");
            return false;
        }

        // Rejeita emails conhecidos de teste (case-insensitive)
        if (INVALID_TEST_EMAILS.contains(trimmedEmail.toLowerCase())) {
            addConstraintViolation(context, "Email de teste não é permitido em produção");
            return false;
        }

        // Separa local part e domínio
        int atIndex = trimmedEmail.lastIndexOf('@');
        if (atIndex <= 0 || atIndex == trimmedEmail.length() - 1) {
            addConstraintViolation(context, "Email deve conter @ separando usuário e domínio");
            return false;
        }

        String localPart = trimmedEmail.substring(0, atIndex);
        String domain = trimmedEmail.substring(atIndex + 1);

        // Valida tamanho da local part
        if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
            addConstraintViolation(context, "Parte local do email muito longa (máximo 64 caracteres)");
            return false;
        }

        // Valida domínio
        if (!isValidDomain(domain)) {
            addConstraintViolation(context, "Domínio inválido ou não possui TLD");
            return false;
        }

        // Valida com regex RFC 5322
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            addConstraintViolation(context, "Formato de email inválido");
            return false;
        }

        log.debug("Email validado com sucesso: {}", trimmedEmail);
        return true;
    }

    /**
     * Valida o domínio do email.
     *
     * @param domain o domínio a ser validado
     * @return true se válido, false caso contrário
     */
    private boolean isValidDomain(String domain) {
        // Domínio não pode ser vazio
        if (domain.isEmpty()) {
            return false;
        }

        // Domínio deve conter pelo menos um ponto (TLD)
        if (!domain.contains(".")) {
            return false;
        }

        // Partes do domínio separadas por ponto
        String[] parts = domain.split("\\.");

        // Deve ter pelo menos 2 partes (nome.tld)
        if (parts.length < 2) {
            return false;
        }

        // TLD (última parte) deve ter pelo menos 2 caracteres
        String tld = parts[parts.length - 1];
        if (tld.length() < 2) {
            return false;
        }

        // Valida cada parte do domínio
        for (String part : parts) {
            // Parte não pode ser vazia
            if (part.isEmpty()) {
                return false;
            }

            // Parte não pode começar ou terminar com hífen
            if (part.startsWith("-") || part.endsWith("-")) {
                return false;
            }

            // Parte deve conter apenas caracteres alfanuméricos e hífens
            if (!part.matches("^[a-zA-Z0-9-]+$")) {
                return false;
            }
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
