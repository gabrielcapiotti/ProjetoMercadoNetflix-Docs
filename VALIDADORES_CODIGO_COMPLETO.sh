#!/bin/bash
# Script de Referência: Validadores Customizados Netflix Mercados
# Este script contém o código completo dos 3 validadores

cat << 'EOF'

╔════════════════════════════════════════════════════════════════════════════╗
║         VALIDADORES CUSTOMIZADOS - NETFLIX MERCADOS                       ║
║                        3 VALIDADORES PRONTOS                              ║
╚════════════════════════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════════════════════════
1. CPFValidator.java - VALIDADOR DE CPF
═══════════════════════════════════════════════════════════════════════════════

Arquivo: src/main/java/com/netflix/mercado/validation/CPFValidator.java

package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.ValidCPF;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CPFValidator implements ConstraintValidator<ValidCPF, String> {

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
    public void initialize(ValidCPF annotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        try {
            return isValidCPF(value, context);
        } catch (Exception e) {
            log.error("Erro ao validar CPF: {}", e.getMessage());
            addConstraintViolation(context, "Erro ao validar CPF");
            return false;
        }
    }

    private boolean isValidCPF(String cpf, ConstraintValidatorContext context) {
        String cleanCPF = cpf.replaceAll("[^0-9]", "");
        
        if (cleanCPF.length() != 11) {
            addConstraintViolation(context, "CPF deve conter 11 dígitos");
            return false;
        }
        
        if (!cleanCPF.matches("\\d+")) {
            addConstraintViolation(context, "CPF deve conter apenas dígitos");
            return false;
        }
        
        if (INVALID_CPF_PATTERNS.contains(cleanCPF)) {
            addConstraintViolation(context, "CPF inválido ou sequência conhecida");
            return false;
        }
        
        int firstDigit = calculateVerifierDigit(cleanCPF.substring(0, 9));
        if (Integer.parseInt(cleanCPF.charAt(9) + "") != firstDigit) {
            addConstraintViolation(context, "Primeiro dígito verificador inválido");
            return false;
        }
        
        int secondDigit = calculateVerifierDigit(cleanCPF.substring(0, 10));
        if (Integer.parseInt(cleanCPF.charAt(10) + "") != secondDigit) {
            addConstraintViolation(context, "Segundo dígito verificador inválido");
            return false;
        }
        
        log.debug("CPF validado com sucesso: {}", cpf);
        return true;
    }

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

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}


═══════════════════════════════════════════════════════════════════════════════
2. CNPJValidator.java - VALIDADOR DE CNPJ
═══════════════════════════════════════════════════════════════════════════════

Arquivo: src/main/java/com/netflix/mercado/validation/CNPJValidator.java

package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.ValidCNPJ;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CNPJValidator implements ConstraintValidator<ValidCNPJ, String> {

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
    public void initialize(ValidCNPJ annotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        try {
            return isValidCNPJ(value, context);
        } catch (Exception e) {
            log.error("Erro ao validar CNPJ: {}", e.getMessage());
            addConstraintViolation(context, "Erro ao validar CNPJ");
            return false;
        }
    }

    private boolean isValidCNPJ(String cnpj, ConstraintValidatorContext context) {
        String cleanCNPJ = cnpj.replaceAll("[^0-9]", "");
        
        if (cleanCNPJ.length() != 14) {
            addConstraintViolation(context, "CNPJ deve conter 14 dígitos");
            return false;
        }
        
        if (!cleanCNPJ.matches("\\d+")) {
            addConstraintViolation(context, "CNPJ deve conter apenas dígitos");
            return false;
        }
        
        if (INVALID_CNPJ_PATTERNS.contains(cleanCNPJ)) {
            addConstraintViolation(context, "CNPJ inválido ou sequência conhecida");
            return false;
        }
        
        int firstDigit = calculateVerifierDigit(cleanCNPJ.substring(0, 12), true);
        if (Integer.parseInt(cleanCNPJ.charAt(12) + "") != firstDigit) {
            addConstraintViolation(context, "Primeiro dígito verificador inválido");
            return false;
        }
        
        int secondDigit = calculateVerifierDigit(cleanCNPJ.substring(0, 13), false);
        if (Integer.parseInt(cleanCNPJ.charAt(13) + "") != secondDigit) {
            addConstraintViolation(context, "Segundo dígito verificador inválido");
            return false;
        }
        
        log.debug("CNPJ validado com sucesso: {}", cnpj);
        return true;
    }

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

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}


═══════════════════════════════════════════════════════════════════════════════
3. PhoneValidator.java - VALIDADOR DE TELEFONE
═══════════════════════════════════════════════════════════════════════════════

Arquivo: src/main/java/com/netflix/mercado/validation/PhoneValidator.java

package com.netflix.mercado.validation;

import com.netflix.mercado.validation.annotations.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+55\\s?)?(" +
            "\\(?([0-9]{2})\\)?\\s?[9]?[0-9]{4}[-\\s]?[0-9]{4}|" +
            "[0-9]{2}[9]?[0-9]{4}[-\\s]?[0-9]{4}|" +
            "[0-9]{2}[9]?[0-9]{8}" +
            ")$"
    );

    private static final int MIN_DDD = 11;
    private static final int MAX_DDD = 99;

    @Override
    public void initialize(ValidPhone annotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        try {
            return isValidPhone(value, context);
        } catch (Exception e) {
            log.error("Erro ao validar telefone: {}", e.getMessage());
            addConstraintViolation(context, "Erro ao validar telefone");
            return false;
        }
    }

    private boolean isValidPhone(String phone, ConstraintValidatorContext context) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            addConstraintViolation(context, "Formato de telefone inválido");
            return false;
        }
        
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        
        if (digitsOnly.startsWith("55")) {
            digitsOnly = digitsOnly.substring(2);
        }
        
        if (digitsOnly.length() != 10 && digitsOnly.length() != 11) {
            addConstraintViolation(context, "Telefone deve ter 10 ou 11 dígitos");
            return false;
        }
        
        int ddd = Integer.parseInt(digitsOnly.substring(0, 2));
        if (ddd < MIN_DDD || ddd > MAX_DDD) {
            addConstraintViolation(context, "DDD deve estar entre 11 e 99");
            return false;
        }
        
        if (digitsOnly.length() == 11) {
            if (digitsOnly.charAt(2) != '9') {
                addConstraintViolation(context, "Número de celular deve começar com 9");
                return false;
            }
        }
        
        if (digitsOnly.matches("(\\d)\\1+")) {
            addConstraintViolation(context, "Telefone inválido (dígitos repetidos)");
            return false;
        }
        
        log.debug("Telefone validado com sucesso: {}", phone);
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}


═══════════════════════════════════════════════════════════════════════════════
ANOTAÇÕES DE CONSTRAINT (Annotations)
═══════════════════════════════════════════════════════════════════════════════

ValidCPF.java:
───────────────────────────────────────────────────────────────────────────────
package com.netflix.mercado.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CPFValidator.class)
@Documented
public @interface ValidCPF {
    String message() default "CPF inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


ValidCNPJ.java:
───────────────────────────────────────────────────────────────────────────────
package com.netflix.mercado.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CNPJValidator.class)
@Documented
public @interface ValidCNPJ {
    String message() default "CNPJ inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


ValidPhone.java:
───────────────────────────────────────────────────────────────────────────────
package com.netflix.mercado.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface ValidPhone {
    String message() default "Telefone inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


═══════════════════════════════════════════════════════════════════════════════
EXEMPLO DE USO EM DTO
═══════════════════════════════════════════════════════════════════════════════

package com.netflix.mercado.dto;

import com.netflix.mercado.validation.ValidCPF;
import com.netflix.mercado.validation.ValidPhone;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UsuarioDTO {
    @NotNull(message = "Nome é obrigatório")
    private String nome;
    
    @NotNull(message = "CPF é obrigatório")
    @ValidCPF(message = "CPF inválido")
    private String cpf;
    
    @NotNull(message = "Telefone é obrigatório")
    @ValidPhone(message = "Telefone inválido")
    private String telefone;
    
    @NotNull(message = "Email é obrigatório")
    private String email;
}

package com.netflix.mercado.dto;

import com.netflix.mercado.validation.ValidCNPJ;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class MercadoDTO {
    @NotNull(message = "Nome é obrigatório")
    private String nome;
    
    @NotNull(message = "CNPJ é obrigatório")
    @ValidCNPJ(message = "CNPJ inválido")
    private String cnpj;
    
    @NotNull(message = "Endereço é obrigatório")
    private String endereco;
}


═══════════════════════════════════════════════════════════════════════════════
EXEMPLO DE USO EM CONTROLLER
═══════════════════════════════════════════════════════════════════════════════

package com.netflix.mercado.controller;

import com.netflix.mercado.dto.UsuarioDTO;
import com.netflix.mercado.dto.MercadoDTO;
import com.netflix.mercado.service.UsuarioService;
import com.netflix.mercado.service.MercadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    private final UsuarioService service;
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
        // Validações de @ValidCPF e @ValidPhone são executadas automaticamente
        UsuarioDTO criado = service.criar(dto);
        return ResponseEntity.ok(criado);
    }
}

@RestController
@RequestMapping("/api/mercados")
public class MercadoController {
    
    private final MercadoService service;
    
    @PostMapping
    public ResponseEntity<MercadoDTO> criar(@Valid @RequestBody MercadoDTO dto) {
        // Validação de @ValidCNPJ é executada automaticamente
        MercadoDTO criado = service.criar(dto);
        return ResponseEntity.ok(criado);
    }
}


═══════════════════════════════════════════════════════════════════════════════
EXEMPLOS DE VALORES VÁLIDOS/INVÁLIDOS
═══════════════════════════════════════════════════════════════════════════════

CPF VÁLIDOS:
✅ 123.456.789-09
✅ 12345678909
✅ 001.444.777-35

CPF INVÁLIDOS:
❌ 000.000.000-00 (sequência conhecida)
❌ 111.111.111-11 (sequência conhecida)
❌ 123.456.789-00 (dígito verificador incorreto)
❌ 12345 (tamanho inválido)


CNPJ VÁLIDOS:
✅ 11.222.333/0001-81
✅ 11222333000181
✅ 34.028.316/0001-04

CNPJ INVÁLIDOS:
❌ 00.000.000/0000-00 (sequência conhecida)
❌ 11.222.333/0001-80 (dígito verificador incorreto)
❌ 12345678901234 (dígitos verificadores inválidos)


TELEFONE VÁLIDOS:
✅ +55 11 99999-9999
✅ 11 99999-9999
✅ 11999999999
✅ (11) 98888-7777
✅ +55 (11) 98888-7777

TELEFONE INVÁLIDOS:
❌ 11 89999-9999 (celular sem 9)
❌ 10 99999-9999 (DDD < 11)
❌ 100 99999-9999 (DDD > 99)
❌ 1199999999 (DDD < 11)


═══════════════════════════════════════════════════════════════════════════════
INFORMAÇÕES TÉCNICAS
═══════════════════════════════════════════════════════════════════════════════

Framework: Spring Boot 3.x
Jakarta Validation: 3.0+
Java: 17+
Lombok: 1.18+
SLF4J: Logging integrado

Localização dos arquivos:
- /src/main/java/com/netflix/mercado/validation/CPFValidator.java
- /src/main/java/com/netflix/mercado/validation/CNPJValidator.java
- /src/main/java/com/netflix/mercado/validation/PhoneValidator.java
- /src/main/java/com/netflix/mercado/validation/ValidCPF.java
- /src/main/java/com/netflix/mercado/validation/ValidCNPJ.java
- /src/main/java/com/netflix/mercado/validation/ValidPhone.java

═══════════════════════════════════════════════════════════════════════════════

EOF
