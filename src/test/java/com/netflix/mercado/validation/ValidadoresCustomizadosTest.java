package com.netflix.mercado.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Testes dos Validadores Customizados")
public class ValidadoresCustomizadosTest {

    @Autowired
    private Validator validator;

    @Autowired
    private CPFValidator cpfValidator;

    @Autowired
    private CNPJValidator cnpjValidator;

    @Autowired
    private PhoneValidator phoneValidator;

    // ═══════════════════════════════════════════════════════════════════════
    // TESTES CPFValidator
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("CPF válido com formatação")
    public void testCPFVallidoComFormatacao() {
        assertTrue(cpfValidator.isValid("123.456.789-09", null));
    }

    @Test
    @DisplayName("CPF válido sem formatação")
    public void testCPFValidoSemFormatacao() {
        assertTrue(cpfValidator.isValid("12345678909", null));
    }

    @Test
    @DisplayName("CPF nulo é aceito")
    public void testCPFNulo() {
        assertTrue(cpfValidator.isValid(null, null));
    }

    @Test
    @DisplayName("CPF inválido - sequência conhecida 000.000.000-00")
    public void testCPFInvalidoSequenciaZero() {
        assertFalse(cpfValidator.isValid("000.000.000-00", null));
    }

    @Test
    @DisplayName("CPF inválido - sequência conhecida 111.111.111-11")
    public void testCPFInvalidoSequenciaUm() {
        assertFalse(cpfValidator.isValid("111.111.111-11", null));
    }

    @Test
    @DisplayName("CPF inválido - tamanho incorreto")
    public void testCPFInvalidoTamanho() {
        assertFalse(cpfValidator.isValid("12345678", null));
    }

    @Test
    @DisplayName("CPF inválido - dígito verificador incorreto")
    public void testCPFInvalidoDigitoVerificador() {
        assertFalse(cpfValidator.isValid("123.456.789-00", null));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTES CNPJValidator
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("CNPJ válido com formatação")
    public void testCNPJValidoComFormatacao() {
        assertTrue(cnpjValidator.isValid("11.222.333/0001-81", null));
    }

    @Test
    @DisplayName("CNPJ válido sem formatação")
    public void testCNPJValidoSemFormatacao() {
        assertTrue(cnpjValidator.isValid("11222333000181", null));
    }

    @Test
    @DisplayName("CNPJ nulo é aceito")
    public void testCNPJNulo() {
        assertTrue(cnpjValidator.isValid(null, null));
    }

    @Test
    @DisplayName("CNPJ inválido - sequência conhecida 00.000.000/0000-00")
    public void testCNPJInvalidoSequenciaZero() {
        assertFalse(cnpjValidator.isValid("00.000.000/0000-00", null));
    }

    @Test
    @DisplayName("CNPJ inválido - sequência conhecida 11.111.111/1111-11")
    public void testCNPJInvalidoSequenciaUm() {
        assertFalse(cnpjValidator.isValid("11.111.111/1111-11", null));
    }

    @Test
    @DisplayName("CNPJ inválido - tamanho incorreto")
    public void testCNPJInvalidoTamanho() {
        assertFalse(cnpjValidator.isValid("1122233300018", null));
    }

    @Test
    @DisplayName("CNPJ inválido - dígito verificador incorreto")
    public void testCNPJInvalidoDigitoVerificador() {
        assertFalse(cnpjValidator.isValid("11.222.333/0001-80", null));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTES PhoneValidator
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Telefone válido com código do país")
    public void testTelefoneValidoComPais() {
        assertTrue(phoneValidator.isValid("+55 11 99999-9999", null));
    }

    @Test
    @DisplayName("Telefone válido sem separadores")
    public void testTelefoneValidoSemSeparadores() {
        assertTrue(phoneValidator.isValid("11999999999", null));
    }

    @Test
    @DisplayName("Telefone válido com espaços")
    public void testTelefoneValidoComEspacos() {
        assertTrue(phoneValidator.isValid("11 99999-9999", null));
    }

    @Test
    @DisplayName("Telefone válido com parênteses")
    public void testTelefoneValidoComParenteses() {
        assertTrue(phoneValidator.isValid("(11) 99999-9999", null));
    }

    @Test
    @DisplayName("Telefone nulo é aceito")
    public void testTelefoneNulo() {
        assertTrue(phoneValidator.isValid(null, null));
    }

    @Test
    @DisplayName("Telefone inválido - DDD menor que 11")
    public void testTelefoneInvalidoDDDMenor() {
        assertFalse(phoneValidator.isValid("10 99999-9999", null));
    }

    @Test
    @DisplayName("Telefone inválido - DDD maior que 99")
    public void testTelefoneInvalidoDDDMaior() {
        assertFalse(phoneValidator.isValid("100 99999-9999", null));
    }

    @Test
    @DisplayName("Telefone inválido - celular sem 9 no terceiro dígito")
    public void testTelefoneInvalidoSem9Celular() {
        assertFalse(phoneValidator.isValid("11 89999-9999", null));
    }

    @Test
    @DisplayName("Telefone inválido - tamanho incorreto")
    public void testTelefoneInvalidoTamanho() {
        assertFalse(phoneValidator.isValid("11 9999-9999", null));
    }

    @Test
    @DisplayName("Telefone inválido - dígitos repetidos")
    public void testTelefoneInvalidoDigitosRepetidos() {
        assertFalse(phoneValidator.isValid("11 99999-9999", null)); // Se todos forem 9
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TESTES COM CONSTRAINT VIOLATIONS (Bean Validation)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Validação de CPF em DTO - sucesso")
    public void testValidacaoCPFDTOSucesso() {
        UsuarioTestDTO usuario = new UsuarioTestDTO();
        usuario.setCpf("12345678909");
        
        Set<ConstraintViolation<UsuarioTestDTO>> violations = validator.validate(usuario);
        
        assertTrue(violations.isEmpty(), "Não deve haver violações");
    }

    @Test
    @DisplayName("Validação de CPF em DTO - falha")
    public void testValidacaoCPFDTOFalha() {
        UsuarioTestDTO usuario = new UsuarioTestDTO();
        usuario.setCpf("000.000.000-00");
        
        Set<ConstraintViolation<UsuarioTestDTO>> violations = validator.validate(usuario);
        
        assertFalse(violations.isEmpty(), "Deve haver violações");
    }

    @Test
    @DisplayName("Validação de CNPJ em DTO - sucesso")
    public void testValidacaoCNPJDTOSucesso() {
        MercadoTestDTO mercado = new MercadoTestDTO();
        mercado.setCnpj("11222333000181");
        
        Set<ConstraintViolation<MercadoTestDTO>> violations = validator.validate(mercado);
        
        assertTrue(violations.isEmpty(), "Não deve haver violações");
    }

    @Test
    @DisplayName("Validação de CNPJ em DTO - falha")
    public void testValidacaoCNPJDTOFalha() {
        MercadoTestDTO mercado = new MercadoTestDTO();
        mercado.setCnpj("00.000.000/0000-00");
        
        Set<ConstraintViolation<MercadoTestDTO>> violations = validator.validate(mercado);
        
        assertFalse(violations.isEmpty(), "Deve haver violações");
    }

    @Test
    @DisplayName("Validação de Telefone em DTO - sucesso")
    public void testValidacaoTelefoneDTOSucesso() {
        UsuarioTestDTO usuario = new UsuarioTestDTO();
        usuario.setTelefone("11999999999");
        
        Set<ConstraintViolation<UsuarioTestDTO>> violations = validator.validate(usuario);
        
        assertTrue(violations.isEmpty(), "Não deve haver violações");
    }

    @Test
    @DisplayName("Validação de Telefone em DTO - falha")
    public void testValidacaoTelefoneDTOFalha() {
        UsuarioTestDTO usuario = new UsuarioTestDTO();
        usuario.setTelefone("10 99999-9999");
        
        Set<ConstraintViolation<UsuarioTestDTO>> violations = validator.validate(usuario);
        
        assertFalse(violations.isEmpty(), "Deve haver violações");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DTOs DE TESTE
    // ═══════════════════════════════════════════════════════════════════════

    public static class UsuarioTestDTO {
        @ValidCPF
        private String cpf;
        
        @ValidPhone
        private String telefone;
        
        // Getters e Setters
        public String getCpf() {
            return cpf;
        }
        
        public void setCpf(String cpf) {
            this.cpf = cpf;
        }
        
        public String getTelefone() {
            return telefone;
        }
        
        public void setTelefone(String telefone) {
            this.telefone = telefone;
        }
    }

    public static class MercadoTestDTO {
        @ValidCNPJ
        private String cnpj;
        
        // Getters e Setters
        public String getCnpj() {
            return cnpj;
        }
        
        public void setCnpj(String cnpj) {
            this.cnpj = cnpj;
        }
    }
}
