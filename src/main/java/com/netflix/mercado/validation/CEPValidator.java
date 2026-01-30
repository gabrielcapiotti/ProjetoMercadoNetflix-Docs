package com.netflix.mercado.validation;

import lombok.extern.slf4j.Slf4j;
import com.netflix.mercado.validation.annotations.ValidCEP;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Validador customizado para CEP (Código de Endereçamento Postal) brasileiro.
 * Valida:
 * - Formato (com ou sem hífens)
 * - Tamanho (8 dígitos)
 * - CEPs conhecidos como inválidos
 *
 * @author Netflix Mercados
 * @version 1.0
 */
@Slf4j
@Component
public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    // CEPs conhecidos como inválidos
    private static final Set<String> INVALID_CEP_PATTERNS = new HashSet<>();

    static {
        INVALID_CEP_PATTERNS.add("00000000");
        INVALID_CEP_PATTERNS.add("11111111");
        INVALID_CEP_PATTERNS.add("22222222");
        INVALID_CEP_PATTERNS.add("33333333");
        INVALID_CEP_PATTERNS.add("44444444");
        INVALID_CEP_PATTERNS.add("55555555");
        INVALID_CEP_PATTERNS.add("66666666");
        INVALID_CEP_PATTERNS.add("77777777");
        INVALID_CEP_PATTERNS.add("88888888");
        INVALID_CEP_PATTERNS.add("99999999");
    }

    @Override
    public void initialize(ValidCEP annotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validação nula é delegada a @NotNull
        if (value == null) {
            return true;
        }

        try {
            return isValidCEP(value, context);
        } catch (Exception e) {
            log.error("Erro ao validar CEP: {}", e.getMessage());
            addConstraintViolation(context, "Erro ao validar CEP");
            return false;
        }
    }

    /**
     * Valida o CEP brasileiro.
     *
     * @param cep     o CEP a ser validado
     * @param context contexto de validação
     * @return true se válido, false caso contrário
     */
    private boolean isValidCEP(String cep, ConstraintValidatorContext context) {
        // Remove hífens
        String cleanCEP = cep.replaceAll("[^0-9]", "");

        // Valida tamanho
        if (cleanCEP.length() != 8) {
            addConstraintViolation(context, "CEP deve conter 8 dígitos");
            return false;
        }

        // Valida se são todos dígitos
        if (!cleanCEP.matches("\\d+")) {
            addConstraintViolation(context, "CEP deve conter apenas dígitos");
            return false;
        }

        // Rejeita CEPs conhecidos como inválidos
        if (INVALID_CEP_PATTERNS.contains(cleanCEP)) {
            addConstraintViolation(context, "CEP inválido ou sequência conhecida");
            return false;
        }

        // Validação básica: primeiros dígitos não podem ser 00
        if (cleanCEP.startsWith("00")) {
            addConstraintViolation(context, "CEP inválido (primeiros dígitos não podem ser 00)");
            return false;
        }

        log.debug("CEP validado com sucesso: {}", cep);
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
