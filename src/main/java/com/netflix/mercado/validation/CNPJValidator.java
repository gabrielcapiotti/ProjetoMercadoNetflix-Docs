package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.ValidCNPJ;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Validador customizado para CNPJ (Cadastro Nacional da Pessoa Jurídica).
 * Valida:
 * - Formato (com ou sem pontos, hífens e barras)
 * - Tamanho (14 dígitos)
 * - Dígitos verificadores (módulo 11)
 * - CNPJs conhecidos como inválidos
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Component
public class CNPJValidator implements ConstraintValidator<ValidCNPJ, String> {

    private static final Logger log = Logger.getLogger(CNPJValidator.class.getName());

    // CNPJs conhecidos como inválidos
    private static final Set<String> INVALID_CNPJ_PATTERNS = new HashSet<>();

    static {
        INVALID_CNPJ_PATTERNS.add("00000000000000");
        INVALID_CNPJ_PATTERNS.add("11111111111111");
        INVALID_CNPJ_PATTERNS.add("22222222222222");
        INVALID_CNPJ_PATTERNS.add("33333333333333");
        INVALID_CNPJ_PATTERNS.add("44444444444444");
        INVALID_CNPJ_PATTERNS.add("55555555555555");
        INVALID_CNPJ_PATTERNS.add("66666666666666");
        INVALID_CNPJ_PATTERNS.add("77777777777777");
        INVALID_CNPJ_PATTERNS.add("88888888888888");
        INVALID_CNPJ_PATTERNS.add("99999999999999");
    }

    @Override
    public void initialize(ValidCNPJ annotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validação nula é delegada a @NotNull
        if (value == null) {
            return true;
        }

        try {
            return isValidCNPJ(value, context);
        } catch (Exception e) {
            log.severe("Erro ao validar CNPJ: " + e.getMessage());
            addConstraintViolation(context, "Erro ao validar CNPJ");
            return false;
        }
    }

    /**
     * Valida o CNPJ seguindo as regras do módulo 11.
     *
     * @param cnpj    o CNPJ a ser validado
     * @param context contexto de validação
     * @return true se válido, false caso contrário
     */
    private boolean isValidCNPJ(String cnpj, ConstraintValidatorContext context) {
        // Remove pontos, hífens e barras
        String cleanCNPJ = cnpj.replaceAll("[^0-9]", "");

        // Valida tamanho
        if (cleanCNPJ.length() != 14) {
            addConstraintViolation(context, "CNPJ deve conter 14 dígitos");
            return false;
        }

        // Valida se são todos dígitos
        if (!cleanCNPJ.matches("\\d+")) {
            addConstraintViolation(context, "CNPJ deve conter apenas dígitos");
            return false;
        }

        // Rejeita CNPJs conhecidos como inválidos
        if (INVALID_CNPJ_PATTERNS.contains(cleanCNPJ)) {
            addConstraintViolation(context, "CNPJ inválido ou sequência conhecida");
            return false;
        }

        // Calcula primeiro dígito verificador
        int firstDigit = calculateVerifierDigit(cleanCNPJ.substring(0, 12), true);
        if (Integer.parseInt(cleanCNPJ.charAt(12) + "") != firstDigit) {
            addConstraintViolation(context, "Primeiro dígito verificador inválido");
            return false;
        }

        // Calcula segundo dígito verificador
        int secondDigit = calculateVerifierDigit(cleanCNPJ.substring(0, 13), false);
        if (Integer.parseInt(cleanCNPJ.charAt(13) + "") != secondDigit) {
            addConstraintViolation(context, "Segundo dígito verificador inválido");
            return false;
        }

        log.fine("CNPJ validado com sucesso: " + cnpj + "");
        return true;
    }

    /**
     * Calcula o dígito verificador usando módulo 11.
     *
     * @param basePart    a parte base do CNPJ
     * @param isFirstDigit true se for o primeiro dígito, false para o segundo
     * @return o dígito verificador calculado
     */
    private int calculateVerifierDigit(String basePart, boolean isFirstDigit) {
        int[] multiplier = isFirstDigit ?
                new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2} :
                new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < basePart.length(); i++) {
            sum += Integer.parseInt(basePart.charAt(i) + "") * multiplier[i];
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
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
