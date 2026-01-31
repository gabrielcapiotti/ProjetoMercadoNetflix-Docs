package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.ValidCPF;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Validador customizado para CPF (Cadastro de Pessoas Físicas).
 * Valida:
 * - Formato (com ou sem pontos e hífens)
 * - Tamanho (11 dígitos)
 * - Dígitos verificadores (módulo 11)
 * - CPFs conhecidos como inválidos
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Component
public class CPFValidator implements ConstraintValidator<ValidCPF, String> {

    private static final Logger log = Logger.getLogger(CPFValidator.class.getName());

    // CPFs conhecidos como inválidos
    private static final Set<String> INVALID_CPF_PATTERNS = new HashSet<>();

    static {
        INVALID_CPF_PATTERNS.add("00000000000");
        INVALID_CPF_PATTERNS.add("11111111111");
        INVALID_CPF_PATTERNS.add("22222222222");
        INVALID_CPF_PATTERNS.add("33333333333");
        INVALID_CPF_PATTERNS.add("44444444444");
        INVALID_CPF_PATTERNS.add("55555555555");
        INVALID_CPF_PATTERNS.add("66666666666");
        INVALID_CPF_PATTERNS.add("77777777777");
        INVALID_CPF_PATTERNS.add("88888888888");
        INVALID_CPF_PATTERNS.add("99999999999");
    }

    @Override
    public void initialize(ValidCPF annotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validação nula é delegada a @NotNull
        if (value == null) {
            return true;
        }

        try {
            return isValidCPF(value, context);
        } catch (Exception e) {
            log.severe("Erro ao validar CPF: " + e.getMessage());
            addConstraintViolation(context, "Erro ao validar CPF");
            return false;
        }
    }

    /**
     * Valida o CPF seguindo as regras do módulo 11.
     *
     * @param cpf     o CPF a ser validado
     * @param context contexto de validação
     * @return true se válido, false caso contrário
     */
    private boolean isValidCPF(String cpf, ConstraintValidatorContext context) {
        // Remove pontos e hífens
        String cleanCPF = cpf.replaceAll("[^0-9]", "");

        // Valida tamanho
        if (cleanCPF.length() != 11) {
            addConstraintViolation(context, "CPF deve conter 11 dígitos");
            return false;
        }

        // Valida se são todos dígitos
        if (!cleanCPF.matches("\\d+")) {
            addConstraintViolation(context, "CPF deve conter apenas dígitos");
            return false;
        }

        // Rejeita CPFs conhecidos como inválidos
        if (INVALID_CPF_PATTERNS.contains(cleanCPF)) {
            addConstraintViolation(context, "CPF inválido ou sequência conhecida");
            return false;
        }

        // Calcula primeiro dígito verificador
        int firstDigit = calculateVerifierDigit(cleanCPF.substring(0, 9));
        if (Integer.parseInt(cleanCPF.charAt(9) + "") != firstDigit) {
            addConstraintViolation(context, "Primeiro dígito verificador inválido");
            return false;
        }

        // Calcula segundo dígito verificador
        int secondDigit = calculateVerifierDigit(cleanCPF.substring(0, 10));
        if (Integer.parseInt(cleanCPF.charAt(10) + "") != secondDigit) {
            addConstraintViolation(context, "Segundo dígito verificador inválido");
            return false;
        }

        log.fine("CPF validado com sucesso: " + cpf + "");
        return true;
    }

    /**
     * Calcula o dígito verificador usando módulo 11.
     *
     * @param basePart a parte base do CPF (9 ou 10 dígitos)
     * @return o dígito verificador calculado
     */
    private int calculateVerifierDigit(String basePart) {
        int sum = 0;
        int multiplier = basePart.length() + 1;

        for (char digit : basePart.toCharArray()) {
            sum += Integer.parseInt(digit + "") * multiplier;
            multiplier--;
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
