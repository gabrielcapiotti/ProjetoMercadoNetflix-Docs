# Validadores Customizados Netflix Mercados

## 📋 Resumo

Foram implementados **3 validadores customizados** para garantir a integridade dos dados de entrada no sistema Netflix Mercados.

---

## 1️⃣ CPFValidator.java

### 📁 Localização
```
src/main/java/com/netflix/mercado/validation/CPFValidator.java
```

### 🎯 Funcionalidade
Implementa `ConstraintValidator<ValidCPF, String>` para validação de CPF (Cadastro de Pessoas Físicas).

### ✅ Validações Realizadas
1. **Limpeza**: Remove pontos e hífens automaticamente
2. **Tamanho**: Verifica se contém exatamente **11 dígitos**
3. **Dígito Verificador 1**: Calcula usando módulo 11 (multiplicadores 10-2)
4. **Dígito Verificador 2**: Calcula usando módulo 11 (multiplicadores 11-2)
5. **Sequências Inválidas**: Rejeita CPFs conhecidos:
   - `00000000000`, `11111111111`, `22222222222`, ..., `99999999999`
   - Ou seja, qualquer sequência onde todos os dígitos são iguais

### 🔧 Características
- Anotação: `@ValidCPF`
- Escopo: `@Component` (Spring)
- Suporta nulidade (delegado a `@NotNull`)
- Mensagem padrão: "CPF inválido"
- Logs estruturados com SLF4J
- Tratamento de exceções robusto

### 📝 Exemplo de Uso
```java
@Entity
public class Usuario {
    @ValidCPF
    @NotNull
    private String cpf;
}
```

### 📊 Algoritmo de Cálculo dos Dígitos
```
Para 1º dígito (posições 0-8):
- sum = d[0]*10 + d[1]*9 + ... + d[8]*2
- remainder = sum % 11
- digit = remainder < 2 ? 0 : 11 - remainder

Para 2º dígito (posições 0-9):
- sum = d[0]*11 + d[1]*10 + ... + d[9]*2
- remainder = sum % 11
- digit = remainder < 2 ? 0 : 11 - remainder
```

---

## 2️⃣ CNPJValidator.java

### 📁 Localização
```
src/main/java/com/netflix/mercado/validation/CNPJValidator.java
```

### 🎯 Funcionalidade
Implementa `ConstraintValidator<ValidCNPJ, String>` para validação de CNPJ (Cadastro Nacional da Pessoa Jurídica).

### ✅ Validações Realizadas
1. **Limpeza**: Remove pontos, hífens e barras automaticamente
2. **Tamanho**: Verifica se contém exatamente **14 dígitos**
3. **Dígito Verificador 1**: Calcula usando módulo 11 com multiplicadores específicos
4. **Dígito Verificador 2**: Calcula usando módulo 11 com multiplicadores específicos
5. **Sequências Inválidas**: Rejeita CNPJs conhecidos (todos os dígitos iguais)

### 🔧 Características
- Anotação: `@ValidCNPJ`
- Escopo: `@Component` (Spring)
- Suporta nulidade (delegado a `@NotNull`)
- Mensagem padrão: "CNPJ inválido"
- Logs estruturados com SLF4J
- Tratamento de exceções robusto

### 📝 Exemplo de Uso
```java
@Entity
public class Mercado {
    @ValidCNPJ
    @NotNull
    private String cnpj;
}
```

### 📊 Algoritmo de Cálculo dos Dígitos
```
Para 1º dígito (12 primeiros dígitos):
- multipliers = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
- sum = d[0]*5 + d[1]*4 + ... + d[11]*2
- remainder = sum % 11
- digit = remainder < 2 ? 0 : 11 - remainder

Para 2º dígito (13 primeiros dígitos):
- multipliers = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
- sum = d[0]*6 + d[1]*5 + ... + d[12]*2
- remainder = sum % 11
- digit = remainder < 2 ? 0 : 11 - remainder
```

---

## 3️⃣ PhoneValidator.java

### 📁 Localização
```
src/main/java/com/netflix/mercado/validation/PhoneValidator.java
```

### 🎯 Funcionalidade
Implementa `ConstraintValidator<ValidPhone, String>` para validação de telefones brasileiros.

### ✅ Validações Realizadas
1. **Formatos Aceitos**:
   - `+55 11 9 9999-9999` (com código do país)
   - `+55 11 99999999` (com código do país, sem separadores)
   - `11 99999-9999` (com DDD)
   - `11999999999` (apenas dígitos)
   - `(11) 99999-9999` (com parênteses)
   - Variações com/sem espaços e hífens

2. **Validação de DDD**: Verifica se está entre **11 e 99**

3. **Validação de Tamanho**: 
   - **10 dígitos**: Telefone fixo
   - **11 dígitos**: Celular (3º dígito deve ser 9)

4. **Rejeição de Sequências**: Rejeita números com todos os dígitos iguais

### 🔧 Características
- Anotação: `@ValidPhone`
- Escopo: `@Component` (Spring)
- Suporta nulidade (delegado a `@NotNull`)
- Mensagem padrão: "Telefone inválido"
- Regex complexo para múltiplos formatos
- Validações estruturadas

### 📝 Exemplo de Uso
```java
@Entity
public class Usuario {
    @ValidPhone
    @NotNull
    private String telefone;
}
```

### 🔍 Validações Detalhadas
| Aspecto | Validação |
|---------|-----------|
| **Tamanho** | 10 ou 11 dígitos (após remover país) |
| **DDD** | 11-99 |
| **Celular** | 11 dígitos, 3º dígito = 9 |
| **Fixo** | 10 dígitos |
| **Sequência** | Rejeita números como 11999999999 |
| **País** | Opcional (+55) |

---

## 📦 Anotações de Constraint

### ValidCPF.java
```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CPFValidator.class)
@Documented
public @interface ValidCPF {
    String message() default "CPF inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### ValidCNPJ.java
```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CNPJValidator.class)
@Documented
public @interface ValidCNPJ {
    String message() default "CNPJ inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### ValidPhone.java
```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface ValidPhone {
    String message() default "Telefone inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

---

## 🚀 Como Usar

### Importar Anotações
```java
import com.netflix.mercado.validation.ValidCPF;
import com.netflix.mercado.validation.ValidCNPJ;
import com.netflix.mercado.validation.ValidPhone;
import jakarta.validation.constraints.NotNull;
```

### Aplicar em DTOs
```java
@Data
@Builder
public class UsuarioDTO {
    @NotNull(message = "CPF é obrigatório")
    @ValidCPF(message = "CPF inválido")
    private String cpf;
    
    @ValidPhone
    private String telefone;
}

@Data
@Builder
public class MercadoDTO {
    @NotNull(message = "CNPJ é obrigatório")
    @ValidCNPJ(message = "CNPJ inválido")
    private String cnpj;
}
```

### Em Controllers
```java
@PostMapping
public ResponseEntity<UsuarioDTO> criarUsuario(
    @Valid @RequestBody UsuarioDTO dto) {
    // A validação é executada automaticamente
    return ResponseEntity.ok(usuarioService.criar(dto));
}
```

---

## 📋 Testes de Exemplo

### CPF Válidos
- `123.456.789-09` ✅
- `12345678909` ✅
- `001.444.777-35` ✅

### CPF Inválidos
- `000.000.000-00` ❌ (sequência conhecida)
- `111.111.111-11` ❌ (sequência conhecida)
- `123.456.789-00` ❌ (dígito verificador incorreto)

### CNPJ Válidos
- `11.222.333/0001-81` ✅
- `11222333000181` ✅

### CNPJ Inválidos
- `00.000.000/0000-00` ❌ (sequência conhecida)
- `11.222.333/0001-80` ❌ (dígito verificador incorreto)

### Telefone Válidos
- `+55 11 99999-9999` ✅
- `11 99999-9999` ✅
- `11999999999` ✅
- `(11) 98888-7777` ✅

### Telefone Inválidos
- `11 89999-9999` ❌ (celular sem 9)
- `10 99999-9999` ❌ (DDD < 11)
- `11999999999` ❌ (se for sequência com todos dígitos iguais)

---

## 🔐 Benefícios

✅ **Validação centralizada**: Reutilizável em qualquer DTO/Entity
✅ **Mensagens personalizáveis**: Cada constraint pode ter sua mensagem
✅ **Integração com Spring Validation**: Funciona com `@Valid` e `@Validated`
✅ **Performance**: Validações otimizadas com regex e cálculos matemáticos
✅ **Segurança**: Rejeita padrões conhecidos como inválidos
✅ **Manutenibilidade**: Código bem documentado e estruturado
✅ **Logging**: Rastreamento de erros com SLF4J

---

## 📚 Estrutura de Diretório

```
src/main/java/com/netflix/mercado/validation/
├── ValidCPF.java              # Anotação para CPF
├── ValidCNPJ.java             # Anotação para CNPJ
├── ValidPhone.java            # Anotação para Telefone
├── CPFValidator.java           # Implementação CPF
├── CNPJValidator.java          # Implementação CNPJ
├── PhoneValidator.java         # Implementação Telefone
├── EmailValidator.java         # Validador de Email
├── PasswordValidator.java      # Validador de Senha
├── CEPValidator.java           # Validador de CEP
└── annotations/                # Pasta para anotações adicionais
```

---

## 🎓 Próximos Passos

1. Integrar os validadores nas entities e DTOs do projeto
2. Criar testes unitários para cada validador
3. Documentar casos de teste e exemplos de validação
4. Considerar validadores adicionais (CEP, Email, Senha)

---

**Status**: ✅ Implementados e Prontos para Uso
**Versão**: 1.0
**Última Atualização**: Janeiro 2026
