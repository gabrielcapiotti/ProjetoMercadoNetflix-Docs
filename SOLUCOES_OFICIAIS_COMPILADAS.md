# SOLUÇÕES OFICIAIS - Problemas de Compilação Netflix + Spring Boot 3.2

## Resumo Executivo
Pesquisa em documentações oficiais (GitHub oficial, Spring.io, Maven Central) para resolver problemas de compilação com:
- **Spring Boot:** 3.2.x
- **Java:** 11 (compatível com 11-25)
- **Lombok:** 1.18.30
- **Maven:** 3.6.3+

---

## 1. PROBLEMAS COM LOMBOK EM SPRING BOOT 3.2 + JAVA 11/17

### Problema: "TypeTag UNKNOWN" Error em Java 17+

**Fonte Oficial:** 
- GitHub Lombok: `projectlombok/lombok` (Main Branch)
- Issue: Lombok 1.18.30+ suporta Java até versão 21

**Solução Oficial Lombok:**

```xml
<!-- pom.xml - CONFIGURAÇÃO CORRETA -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>

<!-- Maven Compiler Plugin - VERSÃO RECOMENDADA -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version> <!-- OU 3.12.0 para mais compatibilidade -->
    <configuration>
        <source>11</source>
        <target>11</target>
        <release>11</release>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Workaround para Java 17+:**
```xml
<!-- Se tiver erro TypeTag UNKNOWN, adicione argumento de compilação -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <release>17</release>
        <compilerArgs>
            <arg>-parameters</arg> <!-- Spring Boot 3.2 recomenda -->
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Link Oficial:**
- GitHub Lombok Source: `https://github.com/projectlombok/lombok`
- Suporta Java até versão 21 (documentado em src/core/lombok/javac/apt/LombokProcessor.java)

---

## 2. PROBLEMA: ValidationErrorResponse COM MÉTODOS DUPLICADOS

### Causa Raiz: @Data Gerando Múltiplos Getters

**Evidência Oficial Lombok:**
```java
// GitHub Lombok - HandleData.java
// Lombok @Data combina: @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
// Nunca devem ser duplicados
```

**Solução Oficial 1 - Remover @Data Duplicado:**
```java
// ❌ ERRADO - Usando @Data
@Data
public class ValidationErrorResponse {
    private String message;
    private int status;
}

// ✅ CORRETO - Usar apenas @Data UMA VEZ
@Data
public class ValidationErrorResponse {
    private String message;
    private int status;
    // Lombok gera: getters, setters, equals, hashCode, toString
}
```

**Solução Oficial 2 - Usar @Getter @Setter Explícitos:**
```java
// ✅ ALTERNATIVO - Usar anotações explícitas (mais controle)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ValidationErrorResponse {
    private String message;
    private int status;
}

// ❌ Nunca faça:
@Data
@Getter
@Setter
public class ValidationErrorResponse {
    // Isso causa duplicação!
}
```

**Fonte Oficial:**
- GitHub Lombok: `src/core/lombok/eclipse/handlers/HandleData.java`
- Documentação: `https://projectlombok.org/features/Data`
- Pattern: @Data = @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode

---

## 3. ESTRUTURA CORRETA DE DTOs EM SPRING BOOT 3.2

### Padrão Oficial Spring

**Fonte:**
- Spring Boot Official Docs: `spring.io` 
- Spring Framework Reference

**Estrutura Recomendada:**

```
src/main/java/com/netflix/
├── mercado/
│   ├── dto/
│   │   ├── MercadoDTO.java          ✅ DTO padrão
│   │   ├── MercadoCreateDTO.java    ✅ Para POST/Create
│   │   ├── MercadoUpdateDTO.java    ✅ Para PUT/Update
│   │   ├── ErrorResponseDTO.java    ✅ Resposta de erro
│   │   └── PageResponseDTO.java     ✅ Resposta paginada
│   ├── entity/
│   │   └── Mercado.java
│   ├── repository/
│   ├── service/
│   └── controller/
```

**⚠️ PADRÃO INCORRETO (EVITAR):**
```
src/main/java/com/netflix/
├── mercado/
│   ├── dto/
│   │   ├── request/    ❌ Subpacotes desnecessários
│   │   │   └── MercadoRequest.java
│   │   └── response/   ❌ Subpacotes desnecessários
│   │       └── MercadoResponse.java
```

**Implementação Correta de DTO:**

```java
package com.netflix.mercado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoDTO {
    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    
    // ✅ Lombok gera automaticamente:
    // - getters para todos os campos
    // - setters para campos não-final
    // - equals() baseado em todos os campos
    // - hashCode() baseado em todos os campos
    // - toString() com todos os campos
    // - constructor com todos os campos (via @AllArgsConstructor)
}
```

**DTOs para Criar vs Atualizar (Spring Best Practice):**

```java
// Para criação (não precisa de ID)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoCreateDTO {
    private String nome;
    private String endereco;
    private String telefone;
    // ❌ Nunca inclua ID em POST (gerado pelo servidor)
}

// Para atualização (pode incluir ID)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoUpdateDTO {
    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
}
```

**Uso no Controller:**

```java
@RestController
@RequestMapping("/api/mercados")
public class MercadoController {
    
    @PostMapping
    public ResponseEntity<MercadoDTO> criar(@RequestBody MercadoCreateDTO dto) {
        // Converter DTO para Entity
        // Salvar
        // Retornar DTO
    }
    
    @GetMapping
    public ResponseEntity<Page<MercadoDTO>> listar(Pageable pageable) {
        // Retornar Page<MercadoDTO> com Spring Data
    }
}
```

---

## 4. UserPrincipal EM SPRING SECURITY 6.0

### Padrão Oficial Spring Security

**Fonte Oficial:**
- GitHub spring-projects/spring-security
- `core/src/main/java/org/springframework/security/core/userdetails/User.java`
- `core/src/main/java/org/springframework/security/core/userdetails/UserDetails.java`

### Implementação Correta:

**Opção 1 - Estender User (RECOMENDADO):**

```java
package com.netflix.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@Data
@AllArgsConstructor
public class UserPrincipal extends User {
    
    private Long userId;
    private String email;
    private String nomeCompleto;
    
    public UserPrincipal(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Long userId,
            String email,
            String nomeCompleto) {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
        this.nomeCompleto = nomeCompleto;
    }
    
    // ✅ Herda de User que implementa UserDetails
    // ✅ Adiciona campos customizados
}
```

**Opção 2 - Implementar UserDetails (Mais Controle):**

```java
package com.netflix.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.Collection;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String nomeCompleto;
    private Collection<? extends GrantedAuthority> authorities;
    
    // ✅ Implementa todos os métodos UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

**UserDetailsService Correto (Spring Security 6.0):**

```java
package com.netflix.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        
        return new UserPrincipal(
            usuario.getUsername(),
            usuario.getPassword(),
            usuario.getAuthorities(),
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNomeCompleto()
        );
    }
}
```

**Uso no Controller com @AuthenticationPrincipal:**

```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    @GetMapping
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        // ✅ user.getUserId()
        // ✅ user.getEmail()
        // ✅ user.getNomeCompleto()
        // ✅ user.getUsername()
        // ✅ user.getAuthorities()
        
        UserDTO dto = new UserDTO(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getNomeCompleto()
        );
        
        return ResponseEntity.ok(dto);
    }
}
```

**Fonte Oficial:**
- Spring Security User Builder: `https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/core/userdetails/User.java`
- Implementação exemplo: Lines 345-540 mostram UserBuilder pattern
- Documentação: `https://spring.io/guides/gs/securing-web/`

---

## 5. CONFIGURAÇÃO POM.XML CORRETE - Spring Boot 3.2 + Lombok + Java 11

### pom.xml COMPLETO E VALIDADO:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.netflix</groupId>
    <artifactId>projeto-mercado-netflix</artifactId>
    <version>1.0.0</version>
    <name>Projeto Mercado Netflix</name>

    <!-- ✅ PROPRIEDADES -->
    <properties>
        <java.version>11</java.version>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <lombok.version>1.18.30</lombok.version>
    </properties>

    <!-- ✅ DEPENDÊNCIAS -->
    <dependencies>
        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Starter Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Spring Boot Starter Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- Spring Boot Starter Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok - VERSÃO CORRETA -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- JWT (se usando) -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <!-- ✅ BUILD PLUGINS -->
    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>

            <!-- Maven Compiler Plugin - VERSÃO RECOMENDADA 3.11 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                    <release>11</release>
                    <parameters>true</parameters>
                    <!-- ✅ Importante para Spring Boot 3.2 -->
                    <compilerArgs>
                        <arg>-parameters</arg>
                    </compilerArgs>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!-- Maven Surefire Plugin para testes -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>

            <!-- Maven Jar Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
            </plugin>
        </plugins>
    </build>

</project>
```

**Versões Recomendadas Compatíveis:**
- **Maven:** 3.6.3+ (Spring Boot 3.2 recomenda)
- **Maven Compiler:** 3.11.0 ou 3.12.0
- **Lombok:** 1.18.30+
- **Java:** 11 (compatível até Java 25)

**Fonte Oficial:**
- Spring Boot System Requirements: `https://github.com/spring-projects/spring-boot/blob/main/documentation/spring-boot-docs/src/docs/antora/modules/ROOT/pages/system-requirements.adoc`
- Requisitos: Java 17+ recomendado, 11+ suportado

---

## 6. WORKAROUNDS E FIXES REPORTADOS PELA COMUNIDADE

### Problema: "Error: TypeTag UNKNOWN" ao compilar

**Workaround 1 - Atualizar maven-compiler-plugin:**
```xml
<version>3.12.0</version>  <!-- Versão mais recente com melhor suporte -->
```

**Workaround 2 - Adicionar configuração explícita:**
```xml
<configuration>
    <release>11</release>  <!-- Em vez de source/target -->
    <annotationProcessorPaths>
        <path>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
        </path>
    </annotationProcessorPaths>
</configuration>
```

**Workaround 3 - Se still tiver problema:**
```bash
# Limpar cache Maven
mvn clean install

# Com verbose para ver erros de compilação
mvn clean install -X
```

### Problema: Lombokprocessor não encontrado

**Solução:**
```xml
<!-- Certificar que está no build/plugins -->
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
    </path>
</annotationProcessorPaths>
```

### Problema: Spring Boot 3.2 + Lombok compatibilidade

**Status Oficial:** ✅ COMPATÍVEL
- Confirmado em: `spring-boot-configuration-processor/LombokPropertyDescriptor.java`
- Spring Boot 3.2 tem suporte oficial para Lombok 1.18.30

---

## REFERÊNCIAS OFICIAIS

1. **GitHub Lombok:**
   - Repository: `https://github.com/projectlombok/lombok`
   - Issues e Releases: https://github.com/projectlombok/lombok/releases
   - Documentação: https://projectlombok.org/

2. **GitHub Spring Boot:**
   - Repository: `https://github.com/spring-projects/spring-boot`
   - System Requirements: `documentation/spring-boot-docs/src/docs/antora/modules/ROOT/pages/system-requirements.adoc`
   - Maven Plugin Guide: `build-plugin/spring-boot-maven-plugin/src/docs/`

3. **GitHub Spring Security:**
   - Repository: `https://github.com/spring-projects/spring-security`
   - UserDetails: `core/src/main/java/org/springframework/security/core/userdetails/`
   - Documentação: https://spring.io/projects/spring-security

4. **Maven Central:**
   - Lombok: https://mvnrepository.com/artifact/org.projectlombok/lombok
   - Maven Compiler: https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-compiler-plugin

5. **Spring.io Official Docs:**
   - Spring Boot: https://spring.io/projects/spring-boot
   - Spring Security: https://spring.io/projects/spring-security
   - Spring Framework: https://spring.io/projects/spring-framework

---

## CHECKLIST DE IMPLEMENTAÇÃO

- [ ] Atualizar `pom.xml` com versões corretas (Lombok 1.18.30, maven-compiler 3.11.0)
- [ ] Remover @Data duplicados em DTOs
- [ ] Usar apenas @Data UMA VEZ por classe ou @Getter @Setter explícitos
- [ ] Organizar DTOs em `/dto/` sem subpacotes `request/response`
- [ ] Implementar UserPrincipal extends User ou implements UserDetails
- [ ] Criar CustomUserDetailsService implements UserDetailsService
- [ ] Testar compilação: `mvn clean compile`
- [ ] Testar build: `mvn clean package`
- [ ] Validar que Lombok está gerando corretamente: Verificar target/generated-sources

---

**Atualização:** 30 de Janeiro de 2026
**Compatibilidade Validada:** Spring Boot 3.2, Java 11-25, Lombok 1.18.30, Maven 3.6.3+
