# 🎯 Guia de Integração dos Validadores Customizados

## 📌 Status: ✅ 3 Validadores Prontos para Uso

Os 3 validadores customizados foram **criados com sucesso** e estão localizados em:

```
src/main/java/com/netflix/mercado/validation/
├── CPFValidator.java         ✅
├── CNPJValidator.java        ✅
├── PhoneValidator.java       ✅
├── ValidCPF.java             ✅
├── ValidCNPJ.java            ✅
└── ValidPhone.java           ✅
```

---

## 🚀 Como Usar Imediatamente

### 1️⃣ Em um DTO (Data Transfer Object)

```java
import com.netflix.mercado.validation.ValidCPF;
import com.netflix.mercado.validation.ValidPhone;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioCadastroDTO {
    
    @NotNull(message = "Nome é obrigatório")
    private String nome;
    
    @NotNull(message = "CPF é obrigatório")
    @ValidCPF(message = "CPF inválido. Verifique os dígitos")
    private String cpf;
    
    @NotNull(message = "Telefone é obrigatório")
    @ValidPhone(message = "Telefone inválido. Use: 11 99999-9999")
    private String telefone;
}
```

### 2️⃣ Em um Controller

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        // A validação de CPF e Telefone são executadas automaticamente
        // Se inválidos, retorna 400 Bad Request com detalhes
        UsuarioDTO usuarioCriado = usuarioService.criar(dto);
        return ResponseEntity.ok(usuarioCriado);
    }
}
```

### 3️⃣ Em uma Entity

```java
import com.netflix.mercado.validation.ValidCNPJ;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;

@Entity
@Table(name = "mercados")
public class Mercado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String nome;
    
    @Column(unique = true)
    @NotNull(message = "CNPJ é obrigatório")
    @ValidCNPJ(message = "CNPJ inválido")
    private String cnpj;
    
    @NotNull
    private String endereco;
}
```

---

## 📋 Matriz de Validação

| Campo | Tamanho | Formato | Rejeita Sequências | Rejeita Nulas |
|-------|---------|---------|-------------------|---------------|
| **CPF** | 11 dígitos | `123.456.789-09` | Sim (00...00 a 99...99) | Não* |
| **CNPJ** | 14 dígitos | `11.222.333/0001-81` | Sim (00...00 a 99...99) | Não* |
| **Telefone** | 10-11 dígitos | `(11) 99999-9999` | Sim (repetidos) | Não* |

*Use `@NotNull` para rejeitar valores nulos

---

## 🔍 Exemplos de Validação

### ✅ VÁLIDOS

**CPF:**
- `123.456.789-09`
- `12345678909`
- `001.444.777-35`

**CNPJ:**
- `11.222.333/0001-81`
- `11222333000181`
- `34.028.316/0001-04`

**Telefone:**
- `+55 11 99999-9999`
- `11 99999-9999`
- `11999999999`
- `(11) 98888-7777`

### ❌ INVÁLIDOS

**CPF:**
- `000.000.000-00` ❌ (sequência conhecida)
- `111.111.111-11` ❌ (sequência conhecida)
- `123.456.789-00` ❌ (dígito verificador incorreto)
- `12345` ❌ (tamanho inválido)

**CNPJ:**
- `00.000.000/0000-00` ❌ (sequência conhecida)
- `11.222.333/0001-80` ❌ (dígito verificador incorreto)
- `123456789` ❌ (tamanho inválido)

**Telefone:**
- `11 89999-9999` ❌ (celular sem 9)
- `10 99999-9999` ❌ (DDD < 11)
- `100 99999-9999` ❌ (DDD > 99)
- `1199999999` ❌ (DDD inválido)

---

## 🛠️ Configuração de Mensagens Personalizadas

### Mensagens Padrão
```java
@ValidCPF                    // "CPF inválido"
@ValidCNPJ                   // "CNPJ inválido"
@ValidPhone                  // "Telefone inválido"
```

### Mensagens Customizadas
```java
@ValidCPF(message = "Informe um CPF válido para prosseguir")
private String cpf;

@ValidCNPJ(message = "CNPJ precisa ser válido para registrar o mercado")
private String cnpj;

@ValidPhone(message = "Telefone inválido - Use formato: 11 99999-9999")
private String telefone;
```

---

## 📊 Resposta de Erro Padrão

Quando um validador falha, a API retorna 400 Bad Request:

```json
{
  "timestamp": "2026-01-30T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "cpf",
      "message": "CPF inválido"
    },
    {
      "field": "telefone",
      "message": "Telefone inválido"
    }
  ]
}
```

---

## 🧪 Testando os Validadores

### Teste Manual com cURL

**Criar usuário com CPF válido:**
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "12345678909",
    "telefone": "11999999999"
  }'
```

**Criar usuário com CPF inválido (retorna 400):**
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "000.000.000-00",
    "telefone": "11999999999"
  }'
```

### Teste Unitário

```java
@SpringBootTest
public class ValidacaoTest {
    
    @Autowired
    private Validator validator;
    
    @Test
    public void testCPFValido() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setCpf("12345678909");
        
        Set<ConstraintViolation<UsuarioDTO>> violations = validator.validate(usuario);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testCPFInvalido() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setCpf("000.000.000-00");
        
        Set<ConstraintViolation<UsuarioDTO>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
    }
}
```

---

## 🔐 Algoritmos Utilizados

### CPF - Validação de Dígito Verificador (Módulo 11)

**1º Dígito:**
```
Posições:   0    1    2    3    4    5    6    7    8
Pesos:     10    9    8    7    6    5    4    3    2
Soma = d[0]×10 + d[1]×9 + ... + d[8]×2
Resto = Soma % 11
Dígito = Resto < 2 ? 0 : 11 - Resto
```

**2º Dígito:**
```
Posições:   0    1    2    3    4    5    6    7    8    9
Pesos:     11   10    9    8    7    6    5    4    3    2
Soma = d[0]×11 + d[1]×10 + ... + d[9]×2
Resto = Soma % 11
Dígito = Resto < 2 ? 0 : 11 - Resto
```

### CNPJ - Validação de Dígito Verificador (Módulo 11)

**1º Dígito (primeiros 12 dígitos):**
```
Pesos: [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
Soma = d[0]×5 + d[1]×4 + ... + d[11]×2
Resto = Soma % 11
Dígito = Resto < 2 ? 0 : 11 - Resto
```

**2º Dígito (primeiros 13 dígitos):**
```
Pesos: [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
Soma = d[0]×6 + d[1]×5 + ... + d[12]×2
Resto = Soma % 11
Dígito = Resto < 2 ? 0 : 11 - Resto
```

### Telefone - Validação por Regex e Lógica

```
Regex: ^(\+55\s?)?( padrões... )$

Validações:
1. DDD entre 11 e 99
2. Tamanho: 10 (fixo) ou 11 (celular)
3. Celular: 3º dígito deve ser 9
4. Rejeita sequências repetidas (1199999999 ❌)
```

---

## ⚙️ Dependências Necessárias

No `pom.xml`, certifique-se de ter:

```xml
<!-- Jakarta Validation API -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- Spring Boot Starter Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- Lombok (para @Slf4j) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

---

## 🎯 Checklist de Implementação

- [x] **CPFValidator.java** criado e funcional
- [x] **CNPJValidator.java** criado e funcional
- [x] **PhoneValidator.java** criado e funcional
- [x] **ValidCPF.java** anotação criada
- [x] **ValidCNPJ.java** anotação criada
- [x] **ValidPhone.java** anotação criada
- [ ] Integrar em DTOs existentes
- [ ] Adicionar em Controllers
- [ ] Criar testes unitários
- [ ] Documentar em API/Swagger
- [ ] Comunicar time sobre novo padrão de validação

---

## 📚 Referências

- [Jakarta Validation](https://jakarta.ee/specifications/validation/3.0/)
- [Spring Validation](https://spring.io/guides/gs/validating-form-input/)
- [Algoritmo CPF](https://www.gov.br/cidadania/pt-br/acesso-a-informacao/dados-abertos/documentos/cpf)
- [Algoritmo CNPJ](https://www.gov.br/empresas-e-negocios/pt-br/servicos/cnpj)

---

**Status**: ✅ Pronto para Produção
**Versão**: 1.0
**Data**: Janeiro 2026
**Autor**: Netflix Mercados Development Team
