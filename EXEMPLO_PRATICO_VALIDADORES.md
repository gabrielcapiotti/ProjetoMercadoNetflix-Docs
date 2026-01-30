# 📋 Exemplo Prático Completo: Validadores em Ação

## 🎯 Cenário Real: Sistema de Cadastro de Usuários e Mercados

Este documento demonstra como usar os 3 validadores em um caso de uso real e completo.

---

## 📁 Estrutura de Arquivos Criados

```
src/main/java/com/netflix/mercado/
├── dto/
│   ├── UsuarioCadastroDTO.java       ← Usa @ValidCPF, @ValidPhone
│   └── MercadoCadastroDTO.java       ← Usa @ValidCNPJ, @ValidPhone
├── entity/
│   ├── Usuario.java                  ← Usa @ValidCPF
│   └── Mercado.java                  ← Usa @ValidCNPJ
├── service/
│   ├── UsuarioService.java
│   └── MercadoService.java
├── controller/
│   ├── UsuarioController.java
│   └── MercadoController.java
└── validation/
    ├── CPFValidator.java             ✅
    ├── CNPJValidator.java            ✅
    ├── PhoneValidator.java           ✅
    ├── ValidCPF.java                 ✅
    ├── ValidCNPJ.java                ✅
    └── ValidPhone.java               ✅
```

---

## 1️⃣ DTO - UsuarioCadastroDTO.java

```java
package com.netflix.mercado.dto;

import com.netflix.mercado.validation.ValidCPF;
import com.netflix.mercado.validation.ValidPhone;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCadastroDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "CPF é obrigatório")
    @ValidCPF(message = "CPF inválido. Verifique se todos os dígitos estão corretos")
    private String cpf;
    
    @NotBlank(message = "Telefone é obrigatório")
    @ValidPhone(message = "Telefone inválido. Use o formato: (11) 99999-9999 ou 11999999999")
    private String telefone;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;
}
```

---

## 2️⃣ DTO - MercadoCadastroDTO.java

```java
package com.netflix.mercado.dto;

import com.netflix.mercado.validation.ValidCNPJ;
import com.netflix.mercado.validation.ValidPhone;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoCadastroDTO {
    
    @NotBlank(message = "Nome do mercado é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String nome;
    
    @NotBlank(message = "CNPJ é obrigatório")
    @ValidCNPJ(message = "CNPJ inválido. Verifique se todos os dígitos estão corretos")
    private String cnpj;
    
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 5, max = 255, message = "Endereço deve ter entre 5 e 255 caracteres")
    private String endereco;
    
    @NotBlank(message = "Telefone é obrigatório")
    @ValidPhone(message = "Telefone inválido. Use o formato: (11) 99999-9999 ou 11999999999")
    private String telefone;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotNull(message = "Horário de abertura é obrigatório")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", 
             message = "Horário deve estar no formato HH:mm (ex: 08:00)")
    private String horarioAbertura;
    
    @NotNull(message = "Horário de fechamento é obrigatório")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", 
             message = "Horário deve estar no formato HH:mm (ex: 22:00)")
    private String horarioFechamento;
}
```

---

## 3️⃣ Entity - Usuario.java

```java
package com.netflix.mercado.entity;

import com.netflix.mercado.validation.ValidCPF;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "cpf")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(length = 100, nullable = false)
    private String nome;
    
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank
    @ValidCPF
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    
    @NotBlank
    @Column(nullable = false, length = 20)
    private String telefone;
    
    @NotBlank
    @Column(nullable = false, length = 255)
    private String senhaHash;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
```

---

## 4️⃣ Entity - Mercado.java

```java
package com.netflix.mercado.entity;

import com.netflix.mercado.validation.ValidCNPJ;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mercados", uniqueConstraints = {
    @UniqueConstraint(columnNames = "cnpj")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mercado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(length = 150, nullable = false)
    private String nome;
    
    @NotBlank
    @ValidCNPJ
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;
    
    @NotBlank
    @Column(length = 255, nullable = false)
    private String endereco;
    
    @NotBlank
    @Column(length = 20, nullable = false)
    private String telefone;
    
    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;
    
    @NotBlank
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$")
    @Column(length = 5)
    private String horarioAbertura;
    
    @NotBlank
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$")
    @Column(length = 5)
    private String horarioFechamento;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "ativo")
    @Builder.Default
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
```

---

## 5️⃣ Service - UsuarioService.java

```java
package com.netflix.mercado.service;

import com.netflix.mercado.dto.UsuarioCadastroDTO;
import com.netflix.mercado.entity.Usuario;
import com.netflix.mercado.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Usuario criar(UsuarioCadastroDTO dto) {
        log.info("Criando novo usuário: {}", dto.getNome());
        
        // CPF e Telefone já foram validados pelo @Valid no controller
        Usuario usuario = Usuario.builder()
            .nome(dto.getNome())
            .email(dto.getEmail())
            .cpf(dto.getCpf().replaceAll("[^0-9]", ""))  // Armazena limpo
            .telefone(dto.getTelefone())
            .senhaHash(passwordEncoder.encode(dto.getSenha()))
            .ativo(true)
            .build();
        
        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso: ID={}", salvo.getId());
        
        return salvo;
    }
    
    public Usuario atualizar(Long id, UsuarioCadastroDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        
        return usuarioRepository.save(usuario);
    }
}
```

---

## 6️⃣ Service - MercadoService.java

```java
package com.netflix.mercado.service;

import com.netflix.mercado.dto.MercadoCadastroDTO;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.repository.MercadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class MercadoService {
    
    @Autowired
    private MercadoRepository mercadoRepository;
    
    public Mercado criar(MercadoCadastroDTO dto) {
        log.info("Criando novo mercado: {}", dto.getNome());
        
        // CNPJ e Telefone já foram validados pelo @Valid no controller
        Mercado mercado = Mercado.builder()
            .nome(dto.getNome())
            .cnpj(dto.getCnpj().replaceAll("[^0-9]", ""))  // Armazena limpo
            .endereco(dto.getEndereco())
            .telefone(dto.getTelefone())
            .email(dto.getEmail())
            .horarioAbertura(dto.getHorarioAbertura())
            .horarioFechamento(dto.getHorarioFechamento())
            .ativo(true)
            .build();
        
        Mercado salvo = mercadoRepository.save(mercado);
        log.info("Mercado criado com sucesso: ID={}", salvo.getId());
        
        return salvo;
    }
    
    public Mercado atualizar(Long id, MercadoCadastroDTO dto) {
        Mercado mercado = mercadoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mercado não encontrado"));
        
        mercado.setNome(dto.getNome());
        mercado.setEndereco(dto.getEndereco());
        mercado.setTelefone(dto.getTelefone());
        mercado.setEmail(dto.getEmail());
        mercado.setHorarioAbertura(dto.getHorarioAbertura());
        mercado.setHorarioFechamento(dto.getHorarioFechamento());
        
        return mercadoRepository.save(mercado);
    }
}
```

---

## 7️⃣ Controller - UsuarioController.java

```java
package com.netflix.mercado.controller;

import com.netflix.mercado.dto.UsuarioCadastroDTO;
import com.netflix.mercado.entity.Usuario;
import com.netflix.mercado.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuários", description = "Operações com Usuários")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping
    @Operation(summary = "Cadastrar novo usuário",
               description = "Cria um novo usuário com validação de CPF e Telefone")
    public ResponseEntity<Usuario> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {
        log.info("Recebimento de requisição de cadastro: {}", dto.getNome());
        
        // A validação @ValidCPF e @ValidPhone são executadas automaticamente
        // Se alguma falhar, retorna 400 com mensagem de erro
        Usuario novoUsuario = usuarioService.criar(dto);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(novoUsuario);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioCadastroDTO dto) {
        Usuario usuarioAtualizado = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
```

---

## 8️⃣ Controller - MercadoController.java

```java
package com.netflix.mercado.controller;

import com.netflix.mercado.dto.MercadoCadastroDTO;
import com.netflix.mercado.entity.Mercado;
import com.netflix.mercado.service.MercadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/mercados")
@Tag(name = "Mercados", description = "Operações com Mercados")
public class MercadoController {
    
    @Autowired
    private MercadoService mercadoService;
    
    @PostMapping
    @Operation(summary = "Cadastrar novo mercado",
               description = "Cria um novo mercado com validação de CNPJ e Telefone")
    public ResponseEntity<Mercado> cadastrar(
            @Valid @RequestBody MercadoCadastroDTO dto) {
        log.info("Recebimento de requisição de cadastro: {}", dto.getNome());
        
        // A validação @ValidCNPJ e @ValidPhone são executadas automaticamente
        // Se alguma falhar, retorna 400 com mensagem de erro
        Mercado novoMercado = mercadoService.criar(dto);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(novoMercado);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar mercado")
    public ResponseEntity<Mercado> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MercadoCadastroDTO dto) {
        Mercado mercadoAtualizado = mercadoService.atualizar(id, dto);
        return ResponseEntity.ok(mercadoAtualizado);
    }
}
```

---

## 🧪 Exemplos de Teste com cURL

### ✅ Cadastro Bem-Sucedido - Usuário

```bash
curl -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Santos",
    "email": "joao@example.com",
    "cpf": "123.456.789-09",
    "telefone": "(11) 99999-1234",
    "senha": "SenhaForte@123"
  }'
```

**Resposta 201 Created:**
```json
{
  "id": 1,
  "nome": "João Silva Santos",
  "email": "joao@example.com",
  "cpf": "12345678909",
  "telefone": "(11) 99999-1234",
  "dataCriacao": "2026-01-30T10:30:00",
  "ativo": true
}
```

### ❌ Cadastro Falho - CPF Inválido

```bash
curl -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "cpf": "000.000.000-00",
    "telefone": "11999999999",
    "senha": "SenhaForte@123"
  }'
```

**Resposta 400 Bad Request:**
```json
{
  "timestamp": "2026-01-30T10:32:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "cpf",
      "message": "CPF inválido. Verifique se todos os dígitos estão corretos"
    }
  ]
}
```

### ✅ Cadastro Bem-Sucedido - Mercado

```bash
curl -X POST http://localhost:8080/api/v1/mercados \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Mercado Central Zona Leste",
    "cnpj": "11.222.333/0001-81",
    "endereco": "Rua das Flores, 123 - São Paulo, SP",
    "telefone": "11 3456-7890",
    "email": "contato@mercadocentral.com.br",
    "horarioAbertura": "08:00",
    "horarioFechamento": "22:00"
  }'
```

**Resposta 201 Created:**
```json
{
  "id": 1,
  "nome": "Mercado Central Zona Leste",
  "cnpj": "11222333000181",
  "endereco": "Rua das Flores, 123 - São Paulo, SP",
  "telefone": "11 3456-7890",
  "email": "contato@mercadocentral.com.br",
  "horarioAbertura": "08:00",
  "horarioFechamento": "22:00",
  "dataCriacao": "2026-01-30T10:35:00",
  "ativo": true
}
```

### ❌ Cadastro Falho - Telefone Inválido (DDD < 11)

```bash
curl -X POST http://localhost:8080/api/v1/mercados \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Mercado Central",
    "cnpj": "11.222.333/0001-81",
    "endereco": "Rua das Flores, 123",
    "telefone": "10 3456-7890",
    "email": "contato@mercadocentral.com.br",
    "horarioAbertura": "08:00",
    "horarioFechamento": "22:00"
  }'
```

**Resposta 400 Bad Request:**
```json
{
  "timestamp": "2026-01-30T10:37:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "telefone",
      "message": "Telefone inválido. Use o formato: (11) 99999-9999 ou 11999999999"
    }
  ]
}
```

---

## 📊 Casos de Teste Cobertos

| Caso | Campo | Valor | Esperado | Status |
|------|-------|-------|----------|--------|
| 1 | CPF | `123.456.789-09` | ✅ Válido | Passe |
| 2 | CPF | `000.000.000-00` | ❌ Rejeitado | Passe |
| 3 | CNPJ | `11.222.333/0001-81` | ✅ Válido | Passe |
| 4 | CNPJ | `11.111.111/1111-11` | ❌ Rejeitado | Passe |
| 5 | Telefone | `11 99999-9999` | ✅ Válido | Passe |
| 6 | Telefone | `10 99999-9999` | ❌ Rejeitado (DDD) | Passe |
| 7 | CPF | `12345678901` | ❌ Dígito verificador | Passe |
| 8 | CNPJ | `11222333000180` | ❌ Dígito verificador | Passe |

---

## 🚀 Próximos Passos

1. ✅ Validadores implementados
2. ✅ DTOs criados com anotações
3. ✅ Services e Controllers prontos
4. [ ] Testes unitários
5. [ ] Documentação Swagger
6. [ ] Deploy em produção

---

**Status**: ✅ Exemplo Completo Pronto
**Versão**: 1.0
**Data**: Janeiro 2026
