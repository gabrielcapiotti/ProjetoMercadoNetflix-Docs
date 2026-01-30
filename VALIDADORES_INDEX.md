# 📑 Índice Rápido - Validadores Customizados

## 🎯 Acesso Rápido

### 📄 Documentação

1. **[RESUMO_VALIDADORES.md](RESUMO_VALIDADORES.md)** ⭐ **COMECE AQUI**
   - Visão geral dos 3 validadores
   - Checklist de implementação
   - Perguntas frequentes

2. **[VALIDADORES_GUIA_INTEGRACAO.md](VALIDADORES_GUIA_INTEGRACAO.md)**
   - Como usar imediatamente
   - Exemplos de código
   - Testes com cURL
   - Resposta de erro padrão

3. **[EXEMPLO_PRATICO_VALIDADORES.md](EXEMPLO_PRATICO_VALIDADORES.md)**
   - Caso de uso real completo
   - DTOs, Services, Controllers
   - Testes completos com cURL
   - Estrutura de diretórios

4. **[VALIDADORES_CUSTOMIZADOS.md](VALIDADORES_CUSTOMIZADOS.md)**
   - Documentação técnica detalhada
   - Algoritmos de validação
   - Matriz de validação
   - Dependências necessárias

5. **[VALIDADORES_CODIGO_COMPLETO.sh](VALIDADORES_CODIGO_COMPLETO.sh)**
   - Código-fonte completo dos 3 validadores
   - Código das anotações
   - Exemplos de uso em DTOs e Controllers

---

## 🗂️ Localização dos Arquivos Java

```
src/main/java/com/netflix/mercado/validation/
│
├── 📦 VALIDADORES
│   ├── CPFValidator.java          (142 linhas)
│   ├── CNPJValidator.java         (144 linhas)
│   └── PhoneValidator.java        (128 linhas)
│
├── 📦 ANOTAÇÕES
│   ├── ValidCPF.java             (17 linhas)
│   ├── ValidCNPJ.java            (17 linhas)
│   └── ValidPhone.java           (17 linhas)
│
└── 📦 TESTES
    └── ValidadoresCustomizadosTest.java  (src/test/)
```

---

## 🚀 Início Rápido (3 Passos)

### Passo 1: Adicionar ao DTO
```java
@Data
public class MeuDTO {
    @ValidCPF
    private String cpf;
    
    @ValidCNPJ
    private String cnpj;
    
    @ValidPhone
    private String telefone;
}
```

### Passo 2: Usar no Controller
```java
@PostMapping
public ResponseEntity criar(@Valid @RequestBody MeuDTO dto) {
    return ResponseEntity.ok(service.criar(dto));
}
```

### Passo 3: Testar
```bash
curl -X POST http://localhost:8080/api/meu-endpoint \
  -H "Content-Type: application/json" \
  -d '{"cpf": "12345678909", ...}'
```

---

## 📊 Validadores Disponíveis

| Validador | Campo | Tamanho | Formato | Arquivo |
|-----------|-------|--------|---------|---------|
| **CPFValidator** | `cpf` | 11 dígitos | `123.456.789-09` | [CPFValidator.java](src/main/java/com/netflix/mercado/validation/CPFValidator.java) |
| **CNPJValidator** | `cnpj` | 14 dígitos | `11.222.333/0001-81` | [CNPJValidator.java](src/main/java/com/netflix/mercado/validation/CNPJValidator.java) |
| **PhoneValidator** | `telefone` | 10-11 dígitos | `11 99999-9999` | [PhoneValidator.java](src/main/java/com/netflix/mercado/validation/PhoneValidator.java) |

---

## ✅ Exemplos Válidos

### CPF
- `123.456.789-09`
- `12345678909`
- `001.444.777-35`

### CNPJ
- `11.222.333/0001-81`
- `11222333000181`

### Telefone
- `+55 11 99999-9999`
- `11 99999-9999`
- `11999999999`
- `(11) 99999-9999`

---

## ❌ Exemplos Inválidos

### CPF
- `000.000.000-00` (sequência conhecida)
- `111.111.111-11` (sequência conhecida)
- `123.456.789-00` (dígito verificador)

### CNPJ
- `00.000.000/0000-00` (sequência conhecida)
- `11.222.333/0001-80` (dígito verificador)

### Telefone
- `10 99999-9999` (DDD < 11)
- `100 99999-9999` (DDD > 99)
- `11 89999-9999` (celular sem 9)

---

## 🔧 Configuração Rápida

### pom.xml - Dependências
```xml
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### application.yml - Sem configuração adicional necessária
✅ Os validadores funcionam out-of-the-box

---

## 📝 Anotações

### @ValidCPF
```java
@ValidCPF(message = "CPF inválido")
private String cpf;
```

### @ValidCNPJ
```java
@ValidCNPJ(message = "CNPJ inválido")
private String cnpj;
```

### @ValidPhone
```java
@ValidPhone(message = "Telefone inválido")
private String telefone;
```

---

## 🧪 Teste Rápido

### Validade de CPF
```bash
curl -X POST http://localhost:8080/api/test-cpf \
  -d '{"cpf": "12345678909"}'
# Retorna: 200 OK se válido, 400 Bad Request se inválido
```

### Validação de CNPJ
```bash
curl -X POST http://localhost:8080/api/test-cnpj \
  -d '{"cnpj": "11222333000181"}'
# Retorna: 200 OK se válido, 400 Bad Request se inválido
```

### Validação de Telefone
```bash
curl -X POST http://localhost:8080/api/test-phone \
  -d '{"telefone": "11999999999"}'
# Retorna: 200 OK se válido, 400 Bad Request se inválido
```

---

## 📚 Documentação Completa por Tópico

### Implementação
- [VALIDADORES_CUSTOMIZADOS.md](VALIDADORES_CUSTOMIZADOS.md) - Detalhes técnicos
- [VALIDADORES_CODIGO_COMPLETO.sh](VALIDADORES_CODIGO_COMPLETO.sh) - Código-fonte

### Integração
- [VALIDADORES_GUIA_INTEGRACAO.md](VALIDADORES_GUIA_INTEGRACAO.md) - Passo-a-passo
- [EXEMPLO_PRATICO_VALIDADORES.md](EXEMPLO_PRATICO_VALIDADORES.md) - Caso real

### Referência
- [RESUMO_VALIDADORES.md](RESUMO_VALIDADORES.md) - Visão geral
- [VALIDADORES_INDEX.md](VALIDADORES_INDEX.md) - Este arquivo

---

## 🎓 Tutoriais por Cenário

### Cenário 1: Adicionar validação em DTO existente
1. Abra o arquivo DTO
2. Adicione `@ValidCPF`, `@ValidCNPJ` ou `@ValidPhone` no campo
3. Pronto! A validação funciona automaticamente

### Cenário 2: Testar validadores
1. Verifique os exemplos em `EXEMPLO_PRATICO_VALIDADORES.md`
2. Execute os comandos cURL fornecidos
3. Observe as respostas 200 (sucesso) ou 400 (erro)

### Cenário 3: Criar novo validador similar
1. Consulte a implementação em `CPFValidator.java`
2. Siga o padrão: `ConstraintValidator<Anotação, String>`
3. Implemente `initialize()` e `isValid()`

---

## 🔗 Links Diretos

| Recurso | Link |
|---------|------|
| Validadores Java | [src/main/java/.../validation/](src/main/java/com/netflix/mercado/validation/) |
| Testes | [src/test/java/.../validation/](src/test/java/com/netflix/mercado/validation/) |
| Documentação | [VALIDADORES_CUSTOMIZADOS.md](VALIDADORES_CUSTOMIZADOS.md) |
| Integração | [VALIDADORES_GUIA_INTEGRACAO.md](VALIDADORES_GUIA_INTEGRACAO.md) |
| Exemplos | [EXEMPLO_PRATICO_VALIDADORES.md](EXEMPLO_PRATICO_VALIDADORES.md) |

---

## ❓ FAQ Rápido

**P: Como uso um validador?**
R: Adicione `@ValidCPF`, `@ValidCNPJ` ou `@ValidPhone` em um campo String.

**P: Preciso de configuração?**
R: Não, funciona out-of-the-box com Spring Boot 3.x.

**P: Os validadores consultam BD?**
R: Não, apenas validam localmente.

**P: Posso customizar a mensagem?**
R: Sim, use `@ValidCPF(message = "Sua mensagem")`.

**P: Qual é a performance?**
R: Excelente, sem I/O externo.

---

## 📊 Status do Projeto

```
✅ CPFValidator       - Implementado e testado
✅ CNPJValidator      - Implementado e testado
✅ PhoneValidator     - Implementado e testado
✅ Documentação       - Completa
✅ Exemplos           - Inclusos
✅ Testes             - Criados
🚀 Pronto para Produção
```

---

## 🎯 Próximas Ações

1. Ler [RESUMO_VALIDADORES.md](RESUMO_VALIDADORES.md)
2. Consultar [VALIDADORES_GUIA_INTEGRACAO.md](VALIDADORES_GUIA_INTEGRACAO.md)
3. Revisar [EXEMPLO_PRATICO_VALIDADORES.md](EXEMPLO_PRATICO_VALIDADORES.md)
4. Integrar em seus DTOs
5. Testar com cURL
6. Deploy! 🚀

---

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique a FAQ acima
2. Consulte o documento relevante
3. Revise o código-fonte comentado
4. Teste com os exemplos fornecidos

---

**Última Atualização**: 30 de Janeiro de 2026  
**Status**: ✅ COMPLETO  
**Versão**: 1.0
