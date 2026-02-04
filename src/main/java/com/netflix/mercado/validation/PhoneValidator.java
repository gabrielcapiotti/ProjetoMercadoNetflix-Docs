package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * Validador customizado para números telefônicos brasileiros.
 * Valida:
 * - Formato: +55 11 9 9999-9999, 11 99999999, 11999999999
 * - DDD entre 11 e 99
 * - Tamanho apropriado (10-11 dígitos)
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Component
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final Logger log = Logger.getLogger(PhoneValidator.class.getName());

    // Regex para validação de número telefônico brasileiro
    // Aceita formatos: +55 (11) 99999-9999, (11) 99999-9999, 11 99999-9999, 11999999999, etc
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+55\\s?)?(" +
            "\\(?([0-9]{2})\\)?\\s?[9]?[0-9]{4}[-\\s]?[0-9]{4}|" +
            "[0-9]{2}[9]?[0-9]{4}[-\\s]?[0-9]{4}|" +
            "[0-9]{2}[9]?[0-9]{8}" +
            ")$"
    );

    // DDD válidos no Brasil (11 até 99)
    private static final int MIN_DDD = 11;
    private static final int MAX_DDD = 99;

    @Override
    public void initialize(ValidPhone annotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validação nula é delegada a @NotNull
        if (value == null) {
            return true;
        }

        try {
            return isValidPhone(value, context);
        } catch (Exception e) {
            log.severe("Erro ao validar telefone: " + e.getMessage());
            addConstraintViolation(context, "Erro ao validar telefone");
            return false;
        }
    }

    /**
     * Valida o número telefônico.
     *
     * @param phone   o telefone a ser validado
     * @param context contexto de validação
     * @return true se válido, false caso contrário
     */
    private boolean isValidPhone(String phone, ConstraintValidatorContext context) {
        // Remove espaços e obtém versão limpa para análise
        String cleanPhone = phone.replaceAll("[^0-9+]", "");

        // Valida com regex
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            addConstraintViolation(context, "Formato de telefone inválido");
            return false;
        }

        // Extrai os dígitos para validação de DDD
        String digitsOnly = cleanPhone.replaceAll("[^0-9]", "");

        // Remove código do país se presente (55)
        if (digitsOnly.startsWith("55")) {
            digitsOnly = digitsOnly.substring(2);
        }

        // Valida tamanho (10 para telefone fixo, 11 para celular)
        if (digitsOnly.length() != 10 && digitsOnly.length() != 11) {
            addConstraintViolation(context, "Telefone deve ter 10 ou 11 dígitos");
            return false;
        }

        // Extrai DDD (2 primeiros dígitos)
        int ddd = Integer.parseInt(digitsOnly.substring(0, 2));
        if (ddd < MIN_DDD || ddd > MAX_DDD) {
            addConstraintViolation(context, "DDD deve estar entre 11 e 99");
            return false;
        }

        // Validações adicionais
        // Para celular (11 dígitos), o terceiro dígito deve ser 9
        if (digitsOnly.length() == 11) {
            if (digitsOnly.charAt(2) != '9') {
                addConstraintViolation(context, "Número de celular deve começar com 9");
                return false;
            }
        }

        // Rejeita números com todos os dígitos iguais
        if (digitsOnly.matches("(\\d)\\1+")) {
            addConstraintViolation(context, "Telefone inválido (dígitos repetidos)");
            return false;
        }

        log.fine("Telefone validado com sucesso: " + phone + "");
        return true;
    }

    /**
     * Adiciona uma violação de constraint personalizada ao contexto.
     *
     * @param context contexto de validação
     * @param message mensagem de erro
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        if (context == null) {
            return;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
