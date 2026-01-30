# Guia de Integração e Boas Práticas - Controllers REST

> Documentação completa para integração dos Controllers, Services e DTOs em um projeto Spring Boot 3.2

## 📋 Índice
1. [Estrutura de Diretórios](#estrutura-de-diretórios)
2. [Configuração de Segurança](#configuração-de-segurança)
3. [Configuração do OpenAPI/Swagger](#configuração-do-openappi--swagger)
4. [Tratamento de Erros](#tratamento-de-erros)
5. [Validações](#validações)
6. [Testes](#testes)
7. [Boas Práticas](#boas-práticas)
8. [Checklist de Implementação](#checklist-de-implementação)

---

## Estrutura de Diretórios

```
src/main/java/com/netflix/mercados/
├── controller/
│   ├── MercadoController.java
│   └── AvaliacaoController.java
├── service/
│   ├── MercadoService.java
│   └── AvaliacaoService.java
├── repository/
│   ├── MercadoRepository.java
│   ├── AvaliacaoRepository.java
│   ├── HorarioFuncionamentoRepository.java
│   ├── UserRepository.java
│   └── RoleRepository.java
├── entity/
│   ├── BaseEntity.java
│   ├── Mercado.java
│   ├── Avaliacao.java
│   ├── HorarioFuncionamento.java
│   ├── User.java
│   ├── Role.java
│   └── Favorito.java
├── dto/
│   ├── response/
│   │   ├── ApiResponse.java
│   │   └── PageResponse.java
│   ├── mercado/
│   │   ├── request/
│   │   │   ├── CreateMercadoRequest.java
│   │   │   └── UpdateMercadoRequest.java
│   │   └── response/
│   │       ├── MercadoResponse.java
│   │       ├── MercadoDetailResponse.java
│   │       ├── HorarioFuncionamentoResponse.java
│   │       └── RatingStatsResponse.java
│   ├── avaliacao/
│   │   ├── request/
│   │   │   ├── CreateAvaliacaoRequest.java
│   │   │   └── UpdateAvaliacaoRequest.java
│   │   └── response/
│   │       ├── AvaliacaoResponse.java
│   │       └── AvaliacaoDetailResponse.java
│   └── horario/
│       └── request/
│           └── CreateHorarioRequest.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   └── UnauthorizedException.java
├── config/
│   ├── CorsConfig.java
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── OpenApiConfig.java
└── MercadoNetflixApplication.java
```

---

## Configuração de Segurança

### SecurityConfig.java

```java
package com.netflix.mercados.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Públicos
                .requestMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // GET - Públicos
                .requestMatchers(HttpMethod.GET, "/api/v1/mercados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/avaliacoes/**").permitAll()
                
                // Resto requer autenticação
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### JwtAuthenticationFilter.java

```java
package com.netflix.mercados.config;

import com.netflix.mercados.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Não foi possível processar token JWT", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### JwtAuthenticationEntryPoint.java

```java
package com.netflix.mercados.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.mercados.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> apiResponse = ApiResponse.error("Não autorizado. JWT inválido ou expirado", "UNAUTHORIZED");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
```

---

## Configuração do OpenAPI / Swagger

### OpenApiConfig.java

```java
package com.netflix.mercados.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token de autenticação")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .info(new Info()
                .title("Netflix Mercados API")
                .description("API REST para gerenciamento de Mercados e Avaliações")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Dev Team")
                    .email("dev@mercados.com")
                    .url("https://mercados.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
```

### application.yml - Swagger

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: list
    display-request-duration: true
    filter: true
    syntax-highlight:
      theme: atom-one-dark
    try-it-out-enabled: true
    show-extensions: true
    
  show-actuator: true
  
  paths-to-match: /api/v1/**
  packages-to-scan: com.netflix.mercados.controller
```

---

## Tratamento de Erros

### Exceções Customizadas Completas

```java
// ResourceNotFoundException.java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado(a) com %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

```java
// ValidationException.java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.HashMap;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    
    private Map<String, String> errors = new HashMap<>();
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}
```

```java
// UnauthorizedException.java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
```

### GlobalExceptionHandler (Completo)

```java
package com.netflix.mercados.exception;

import com.netflix.mercados.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            ValidationException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(), "VALIDATION_ERROR"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        log.error("Unauthorized: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ex.getMessage(), "FORBIDDEN"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("Acesso negado", "ACCESS_DENIED"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation errors: {}", errors);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.builder()
                .success(false)
                .message("Erros de validação encontrados")
                .error(ApiResponse.ErrorDetails.builder()
                    .code("VALIDATION_ERROR")
                    .message(errors.toString())
                    .build())
                .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("Type mismatch: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Tipo de dados inválido", "TYPE_MISMATCH"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            NoHandlerFoundException ex, WebRequest request) {
        log.error("Endpoint not found: {}", ex.getRequestURL());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Endpoint não encontrado", "NOT_FOUND"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception ex, WebRequest request) {
        log.error("Internal server error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(
                "Erro interno do servidor",
                "INTERNAL_SERVER_ERROR"
            ));
    }
}
```

---

## Validações

### Custom Validators

```java
package com.netflix.mercados.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.regex.Pattern;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CnpjValidator.class)
@Documented
public @interface ValidCnpj {
    String message() default "CNPJ inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class CnpjValidator implements ConstraintValidator<ValidCnpj, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        // Remove caracteres não numéricos
        String cnpj = value.replaceAll("\\D", "");
        
        if (cnpj.length() != 14) {
            return false;
        }
        
        // Validação de CNPJ (implementação simplificada)
        return !cnpj.matches("(\\d)\\1{13}");
    }
}
```

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CepValidator.class)
@Documented
public @interface ValidCep {
    String message() default "CEP inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class CepValidator implements ConstraintValidator<ValidCep, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches("^\\d{5}-?\\d{3}$");
    }
}
```

---

## Testes

### Integration Tests

```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.mercado.request.CreateMercadoRequest;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.repository.UserRepository;
import com.netflix.mercados.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MercadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;

    @BeforeEach
    void setup() {
        User user = User.builder()
            .username("seller@test.com")
            .email("seller@test.com")
            .senha("password")
            .nome("Test Seller")
            .build();
        
        userRepository.save(user);
        token = jwtTokenProvider.generateToken(user.getUsername());
    }

    @Test
    void testCriarMercadoWithValidData() throws Exception {
        CreateMercadoRequest request = CreateMercadoRequest.builder()
            .nome("Mercado Test")
            .descricao("Descrição do mercado teste")
            .telefone("11987654321")
            .email("mercado@test.com")
            .cep("01234567")
            .rua("Rua Test")
            .numero(123)
            .cidade("São Paulo")
            .estado("SP")
            .latitude(-23.550520)
            .longitude(-46.633309)
            .cnpj("12345678000123")
            .tipoMercado("SUPERMERCADO")
            .build();

        mockMvc.perform(post("/api/v1/mercados")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.nome").value("Mercado Test"));
    }

    @Test
    void testListarMercadosWithPagination() throws Exception {
        mockMvc.perform(get("/api/v1/mercados?page=0&size=20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testObtenerDetalhesNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/mercados/99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }
}
```

---

## Boas Práticas

### 1. **Logging**
- Use `@Slf4j` para logging automático
- Log em INFO para ações importantes
- Log em DEBUG para fluxo detalhado
- Log em ERROR para exceções

### 2. **Transações**
- Use `@Transactional` apenas quando necessário
- Sempre use `readOnly = true` para operações de leitura
- Deixe a exceção propagar para rollback automático

### 3. **Validações**
- Valide entrada em Controllers com `@Valid`
- Valide regras de negócio em Services
- Use exceções customizadas para erros de negócio

### 4. **Segurança**
- Sempre verifique autorização antes de acessar recursos
- Use `@PreAuthorize` nos Controllers
- Valide propriedade em Services (quem criou pode atualizar)

### 5. **Performance**
- Use projeções em queries quando possível
- Implemente paginação para listas grandes
- Use lazy loading com cuidado

### 6. **DTOs**
- Sempre converta Entities em DTOs
- Nunca exponha Entities direto
- Use `@Builder` para facilitar criação

### 7. **Documentação**
- Use `@Operation` em cada endpoint
- Documente parâmetros com `@Parameter`
- Descreva respostas possíveis com `@ApiResponses`

---

## Checklist de Implementação

- [ ] Controllers implementados (`MercadoController`, `AvaliacaoController`)
- [ ] Services implementados (`MercadoService`, `AvaliacaoService`)
- [ ] Repositories com queries customizadas
- [ ] DTOs criados e convertendo corretamente
- [ ] Exceções customizadas implementadas
- [ ] GlobalExceptionHandler configurado
- [ ] SecurityConfig implementada com JWT
- [ ] OpenAPI/Swagger configurado
- [ ] CORS habilitado
- [ ] Validações em Request DTOs
- [ ] Transações configuradas nos Services
- [ ] Logging com @Slf4j
- [ ] Paginação implementada
- [ ] Soft Delete funcionando
- [ ] Auditoria (createdBy, updatedBy) funcionando
- [ ] Testes de integração escritos
- [ ] Documentação de API completa
- [ ] Teste manual de todos endpoints
- [ ] Performance otimizada
- [ ] Deploy pronto para produção

---

## Scripts Úteis

### Criar database

```sql
CREATE DATABASE mercado_netflix;
USE mercado_netflix;

-- Será criada automaticamente pelo Hibernate
-- com ddl-auto: create-drop ou update
```

### Iniciar aplicação

```bash
# Com Maven
mvn spring-boot:run

# Com Gradle
gradle bootRun

# Build
mvn clean package -DskipTests

# Docker
docker build -t mercado-netflix .
docker run -p 8080:8080 mercado-netflix
```

### Acessar Swagger

```
http://localhost:8080/swagger-ui.html
```

---

## Exemplo de Requisição cURL

```bash
# Criar Mercado
curl -X POST http://localhost:8080/api/v1/mercados \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Mercado Central",
    "descricao": "Melhor mercado da região",
    "telefone": "11987654321",
    "email": "mercado@example.com",
    "cnpj": "12345678000123",
    "cep": "01234567",
    "rua": "Rua das Flores",
    "numero": 100,
    "cidade": "São Paulo",
    "estado": "SP",
    "latitude": -23.550520,
    "longitude": -46.633309,
    "tipoMercado": "SUPERMERCADO"
  }'

# Listar Mercados
curl -X GET "http://localhost:8080/api/v1/mercados?page=0&size=20&aprovado=true"

# Criar Avaliação
curl -X POST http://localhost:8080/api/v1/avaliacoes \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "mercadoId": 1,
    "nota": 5,
    "comentario": "Excelente atendimento e produtos de qualidade",
    "avaliacaoAtendimento": 5,
    "avaliacaoLimpeza": 5,
    "avaliacaoProdutos": 4,
    "avaliacaoPrecos": 4,
    "recomenda": true
  }'
```

---

## Observações Importantes

1. **JWT Token**: Obtenha um token válido do endpoint de login (não incluído nesta documentação)
2. **Soft Delete**: O atributo `deleted` é gerenciado automaticamente pelo Hibernate
3. **Rating Médio**: É atualizado automaticamente ao criar/atualizar/deletar avaliação
4. **Distância**: Usa fórmula de Haversine para calcular distância real
5. **Auditoria**: `createdBy` e `updatedBy` são preenchidos automaticamente
