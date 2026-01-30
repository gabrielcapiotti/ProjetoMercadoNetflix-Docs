# Arquitetura de Controllers e Services - Netflix Mercados

## 📋 Índice
1. [Exceções Customizadas](#exceções-customizadas)
2. [DTOs de Autenticação](#dtos-de-autenticação)
3. [Classes de Resposta](#classes-de-resposta)
4. [Validators](#validators)
5. [Converters](#converters)
6. [Services](#services)
7. [Controllers](#controllers)
8. [Configurações Adicionais](#configurações-adicionais)

---

## Exceções Customizadas

### ResourceNotFoundException

```java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
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
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}
```

### ValidationException

```java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.HashMap;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private Map<String, String> errors = new HashMap<>();
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public ValidationException addError(String field, String message) {
        this.errors.put(field, message);
        return this;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
```

### UnauthorizedException

```java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### ResourceAlreadyExistsException

```java
package com.netflix.mercados.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}
```

### GlobalExceptionHandler

```java
package com.netflix.mercados.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.netflix.mercados.dto.response.ErrorResponse;
import com.netflix.mercados.dto.response.ValidationErrorResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Erro de validação: {}", ex.getMessage());
        
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .errors(ex.getErrors())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, WebRequest request) {
        
        log.warn("Recurso já existe: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        log.warn("Acesso não autorizado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Erro na validação dos argumentos da requisição");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Erro na validação dos campos")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .errors(errors)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Erro interno do servidor", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Erro interno do servidor. Contacte o suporte.")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

---

## DTOs de Autenticação

### RegisterRequest

```java
package com.netflix.mercados.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para requisição de registro de novo usuário")
public class RegisterRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email único do usuário", example = "joao@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve ter 10 ou 11 dígitos")
    @Schema(description = "Telefone do usuário", example = "11987654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Senha deve conter maiúsculas, minúsculas, números e caracteres especiais"
    )
    @Schema(
        description = "Senha do usuário (min 8 caracteres, deve incluir maiúsculas, minúsculas, números e especiais)",
        example = "SenhaForte@123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    @Schema(description = "Confirmação da senha", example = "SenhaForte@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String passwordConfirmation;
}
```

### LoginRequest

```java
package com.netflix.mercados.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para requisição de login")
public class LoginRequest {
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "SenhaForte@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
```

### JwtAuthenticationResponse

```java
package com.netflix.mercados.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para resposta de autenticação JWT")
public class JwtAuthenticationResponse {
    
    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Tempo de expiração do token em minutos", example = "60")
    private Long expiresIn;
    
    @Schema(description = "Informações do usuário autenticado")
    private UserResponse user;
}
```

### UserResponse

```java
package com.netflix.mercados.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para resposta de usuário")
public class UserResponse {
    
    @Schema(description = "ID único do usuário", example = "1")
    private Long id;
    
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;
    
    @Schema(description = "Email do usuário", example = "joao@example.com")
    private String email;
    
    @Schema(description = "Telefone do usuário", example = "11987654321")
    private String phone;
    
    @Schema(description = "Role/Perfil do usuário", example = "ROLE_USER")
    private String role;
    
    @Schema(description = "Status de ativação do usuário", example = "true")
    private Boolean active;
    
    @Schema(description = "Data de criação do usuário")
    private LocalDateTime createdAt;
    
    @Schema(description = "Data de atualização do usuário")
    private LocalDateTime updatedAt;
}
```

### RefreshTokenRequest

```java
package com.netflix.mercados.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO para requisição de refresh de token")
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token é obrigatório")
    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
```

---

## Classes de Resposta

### ApiResponse (Genérica)

```java
package com.netflix.mercados.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta genérica da API")
public class ApiResponse<T> {
    
    @Schema(description = "Status da requisição", example = "true")
    private boolean success;
    
    @Schema(description = "Mensagem de resposta", example = "Operação realizada com sucesso")
    private String message;
    
    @Schema(description = "Dados da resposta")
    private T data;
    
    @Schema(description = "Código HTTP da resposta", example = "200")
    private Integer statusCode;
    
    @Schema(description = "Timestamp da resposta")
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operação realizada com sucesso");
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

### ErrorResponse

```java
package com.netflix.mercados.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para resposta de erro")
public class ErrorResponse {
    
    @Schema(description = "Status HTTP do erro", example = "404")
    private Integer status;
    
    @Schema(description = "Mensagem de erro", example = "Usuário não encontrado")
    private String message;
    
    @Schema(description = "Caminho da requisição", example = "/api/v1/users/1")
    private String path;
    
    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;
}
```

### ValidationErrorResponse

```java
package com.netflix.mercados.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para resposta de erro de validação")
public class ValidationErrorResponse {
    
    @Schema(description = "Status HTTP do erro", example = "400")
    private Integer status;
    
    @Schema(description = "Mensagem de erro", example = "Erro na validação dos campos")
    private String message;
    
    @Schema(description = "Caminho da requisição", example = "/api/v1/auth/register")
    private String path;
    
    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;
    
    @Schema(description = "Mapa com erros de validação por campo")
    private Map<String, String> errors;
}
```

---

## Validators

### UserValidator

```java
package com.netflix.mercados.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.netflix.mercados.dto.request.RegisterRequest;
import com.netflix.mercados.exception.ValidationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserValidator {
    
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    
    public UserValidator(EmailValidator emailValidator, PasswordValidator passwordValidator) {
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
    }
    
    public void validateRegistrationRequest(RegisterRequest request) {
        log.debug("Validando requisição de registro para email: {}", request.getEmail());
        
        Map<String, String> errors = new HashMap<>();
        
        // Validação de nome
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.put("name", "Nome não pode ser vazio");
        } else if (request.getName().length() < 3) {
            errors.put("name", "Nome deve ter pelo menos 3 caracteres");
        }
        
        // Validação de email
        if (!emailValidator.isValid(request.getEmail())) {
            errors.put("email", "Email inválido ou já registrado");
        }
        
        // Validação de telefone
        if (request.getPhone() == null || !request.getPhone().matches("^\\d{10,11}$")) {
            errors.put("phone", "Telefone deve ter 10 ou 11 dígitos");
        }
        
        // Validação de senha
        if (!passwordValidator.isStrong(request.getPassword())) {
            errors.put("password", "Senha deve ter maiúsculas, minúsculas, números e caracteres especiais");
        }
        
        // Validação de confirmação de senha
        if (request.getPassword() != null && !request.getPassword().equals(request.getPasswordConfirmation())) {
            errors.put("passwordConfirmation", "As senhas não correspondem");
        }
        
        if (!errors.isEmpty()) {
            log.warn("Erro na validação do registro: {}", errors);
            throw new ValidationException("Erro na validação dos dados de registro", errors);
        }
        
        log.debug("Validação de registro bem-sucedida para email: {}", request.getEmail());
    }
    
    public void validateUserUpdate(String name, String phone) {
        log.debug("Validando atualização de usuário");
        
        Map<String, String> errors = new HashMap<>();
        
        if (name != null && (name.trim().isEmpty() || name.length() < 3)) {
            errors.put("name", "Nome deve ter pelo menos 3 caracteres");
        }
        
        if (phone != null && !phone.matches("^\\d{10,11}$")) {
            errors.put("phone", "Telefone deve ter 10 ou 11 dígitos");
        }
        
        if (!errors.isEmpty()) {
            log.warn("Erro na validação de atualização: {}", errors);
            throw new ValidationException("Erro na validação dos dados de atualização", errors);
        }
    }
}
```

### EmailValidator

```java
package com.netflix.mercados.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Slf4j
@Component
public class EmailValidator {
    
    private static final String EMAIL_REGEX = 
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    public boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.debug("Email vazio ou nulo");
            return false;
        }
        
        boolean isValid = EMAIL_PATTERN.matcher(email).matches();
        log.debug("Validação de email: {} - {}", email, isValid ? "válido" : "inválido");
        return isValid;
    }
    
    public boolean isValidFormat(String email) {
        return isValid(email);
    }
}
```

### PasswordValidator

```java
package com.netflix.mercados.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasswordValidator {
    
    private static final int MIN_LENGTH = 8;
    private static final String STRONG_PASSWORD_REGEX = 
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    public boolean isStrong(String password) {
        if (password == null || password.isEmpty()) {
            log.debug("Senha vazia ou nula");
            return false;
        }
        
        boolean hasMinLength = password.length() >= MIN_LENGTH;
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@$!%*?&].*");
        
        boolean isStrong = hasMinLength && hasLowerCase && hasUpperCase && hasDigit && hasSpecialChar;
        
        log.debug("Validação de força da senha - Comprimento: {}, Minúscula: {}, Maiúscula: {}, Número: {}, Especial: {}",
                hasMinLength, hasLowerCase, hasUpperCase, hasDigit, hasSpecialChar);
        
        return isStrong;
    }
    
    public boolean matches(String password, String confirmPassword) {
        boolean matches = password != null && password.equals(confirmPassword);
        log.debug("Validação de correspondência de senhas: {}", matches);
        return matches;
    }
}
```

---

## Converters

### UserConverter

```java
package com.netflix.mercados.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.netflix.mercados.dto.response.UserResponse;
import com.netflix.mercados.entity.User;

@Slf4j
@Component
public class UserConverter {
    
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            log.warn("Tentativa de converter usuário nulo");
            return null;
        }
        
        log.debug("Convertendo usuário {} para UserResponse", user.getId());
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    public UserResponse toUserResponseWithoutSensitiveData(User user) {
        if (user == null) {
            log.warn("Tentativa de converter usuário nulo");
            return null;
        }
        
        log.debug("Convertendo usuário {} para UserResponse sem dados sensíveis", user.getId());
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
```

---

## Services

### UserService

```java
package com.netflix.mercados.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.netflix.mercados.converter.UserConverter;
import com.netflix.mercados.dto.response.UserResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.ResourceAlreadyExistsException;
import com.netflix.mercados.exception.ResourceNotFoundException;
import com.netflix.mercados.repository.UserRepository;
import com.netflix.mercados.validator.UserValidator;

@Slf4j
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, UserConverter userConverter,
                      UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Buscando usuário com ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Usuário", "id", id);
                });
        
        return userConverter.toUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Buscando usuário com email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com email: {}", email);
                    return new ResourceNotFoundException("Usuário", "email", email);
                });
        
        return userConverter.toUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Buscando todos os usuários - Página: {}, Tamanho: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userConverter::toUserResponse);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Verificando se email existe: {}", email);
        return userRepository.existsByEmail(email);
    }
    
    public User createUser(String name, String email, String phone, String password, String role) {
        log.info("Criando novo usuário com email: {}", email);
        
        // Validação em nível de Service
        if (existsByEmail(email)) {
            log.warn("Tentativa de criar usuário com email já existente: {}", email);
            throw new ResourceAlreadyExistsException("Usuário", "email", email);
        }
        
        User user = User.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Usuário criado com sucesso - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
        
        return savedUser;
    }
    
    public UserResponse updateUser(Long id, String name, String phone) {
        log.info("Atualizando usuário - ID: {}", id);
        
        // Validação em nível de Service
        userValidator.validateUserUpdate(name, phone);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para atualização - ID: {}", id);
                    return new ResourceNotFoundException("Usuário", "id", id);
                });
        
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
            log.debug("Nome do usuário atualizado para: {}", name);
        }
        
        if (phone != null && !phone.isEmpty()) {
            user.setPhone(phone);
            log.debug("Telefone do usuário atualizado para: {}", phone);
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso - ID: {}", updatedUser.getId());
        
        return userConverter.toUserResponse(updatedUser);
    }
    
    public void updatePassword(Long id, String newPassword) {
        log.info("Atualizando senha do usuário - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para atualização de senha - ID: {}", id);
                    return new ResourceNotFoundException("Usuário", "id", id);
                });
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Senha do usuário atualizada com sucesso - ID: {}", id);
    }
    
    public void deleteUser(Long id) {
        log.info("Deletando usuário - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para deleção - ID: {}", id);
                    return new ResourceNotFoundException("Usuário", "id", id);
                });
        
        userRepository.delete(user);
        log.info("Usuário deletado com sucesso - ID: {}", id);
    }
    
    public void deactivateUser(Long id) {
        log.info("Desativando usuário - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para desativação - ID: {}", id);
                    return new ResourceNotFoundException("Usuário", "id", id);
                });
        
        user.setActive(false);
        userRepository.save(user);
        
        log.info("Usuário desativado com sucesso - ID: {}", id);
    }
}
```

### AuthService

```java
package com.netflix.mercados.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.netflix.mercados.converter.UserConverter;
import com.netflix.mercados.dto.request.LoginRequest;
import com.netflix.mercados.dto.request.RegisterRequest;
import com.netflix.mercados.dto.response.JwtAuthenticationResponse;
import com.netflix.mercados.dto.response.UserResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.UnauthorizedException;
import com.netflix.mercados.security.JwtTokenProvider;
import com.netflix.mercados.validator.UserValidator;

@Slf4j
@Service
@Transactional
public class AuthService {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserValidator userValidator;
    private final UserConverter userConverter;
    
    public AuthService(UserService userService, AuthenticationManager authenticationManager,
                      JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService,
                      UserValidator userValidator, UserConverter userConverter) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userValidator = userValidator;
        this.userConverter = userConverter;
    }
    
    public JwtAuthenticationResponse register(RegisterRequest registerRequest) {
        log.info("Iniciando processo de registro para email: {}", registerRequest.getEmail());
        
        // Validação em 3 níveis:
        // 1. Validação via @Valid (Bean Validation)
        // 2. Validação customizada (UserValidator)
        userValidator.validateRegistrationRequest(registerRequest);
        
        // 3. Validação em nível de negócio (Service)
        if (userService.existsByEmail(registerRequest.getEmail())) {
            log.warn("Tentativa de registro com email já existente: {}", registerRequest.getEmail());
            throw new UnauthorizedException("Email já registrado");
        }
        
        // Criar novo usuário
        User user = userService.createUser(
                registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPhone(),
                registerRequest.getPassword(),
                "ROLE_USER"
        );
        
        log.info("Usuário registrado com sucesso - ID: {}", user.getId());
        
        // Gerar tokens
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
        
        log.debug("Tokens gerados para novo usuário - ID: {}", user.getId());
        
        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(userConverter.toUserResponse(user))
                .build();
    }
    
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        log.info("Iniciando processo de login para email: {}", loginRequest.getEmail());
        
        try {
            // Autenticar usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info("Usuário autenticado com sucesso: {}", loginRequest.getEmail());
            
            // Buscar usuário
            User user = (User) authentication.getPrincipal();
            
            // Gerar tokens
            String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
            String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
            
            log.debug("Tokens gerados para login - Email: {}", user.getEmail());
            
            return JwtAuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .user(userConverter.toUserResponse(user))
                    .build();
            
        } catch (Exception ex) {
            log.error("Falha na autenticação para email: {}", loginRequest.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos", ex);
        }
    }
    
    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        log.debug("Solicitação para refresh de token");
        
        var refToken = refreshTokenService.validateRefreshToken(refreshToken);
        User user = refToken.getUser();
        
        log.info("Token refreshado para usuário: {}", user.getEmail());
        
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        String newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
        
        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(userConverter.toUserResponse(user))
                .build();
    }
    
    public void logout(String email) {
        log.info("Logout do usuário: {}", email);
        
        // Buscar usuário
        User user = userService.userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        
        // Revogar refresh tokens
        refreshTokenService.revokeAllUserRefreshTokens(user.getId());
        
        SecurityContextHolder.clearContext();
        
        log.info("Logout realizado com sucesso para usuário: {}", email);
    }
    
    public UserResponse getCurrentUser(String email) {
        log.debug("Buscando informações do usuário atual: {}", email);
        return userService.getUserByEmail(email);
    }
}
```

### RefreshTokenService

```java
package com.netflix.mercados.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.netflix.mercados.entity.RefreshToken;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.exception.UnauthorizedException;
import com.netflix.mercados.repository.RefreshTokenRepository;
import com.netflix.mercados.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RefreshTokenService {
    
    @Value("${app.auth.refresh-token-expiration}")
    private long refreshTokenExpiration;
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                              UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }
    
    public RefreshToken createRefreshToken(Long userId) {
        log.debug("Criando novo refresh token para usuário ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();
        
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token criado com sucesso para usuário ID: {}", userId);
        
        return savedToken;
    }
    
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        log.debug("Validando refresh token");
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Refresh token não encontrado");
                    return new UnauthorizedException("Refresh token inválido");
                });
        
        if (refreshToken.isRevoked()) {
            log.error("Refresh token foi revogado");
            throw new UnauthorizedException("Refresh token foi revogado");
        }
        
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.error("Refresh token expirou");
            revokeToken(refreshToken);
            throw new UnauthorizedException("Refresh token expirou");
        }
        
        log.debug("Refresh token validado com sucesso");
        return refreshToken;
    }
    
    public void revokeToken(RefreshToken token) {
        log.debug("Revogando refresh token para usuário ID: {}", token.getUser().getId());
        
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        
        log.debug("Refresh token revogado com sucesso");
    }
    
    public void revokeAllUserRefreshTokens(Long userId) {
        log.info("Revogando todos os refresh tokens do usuário ID: {}", userId);
        
        refreshTokenRepository.findByUserId(userId).forEach(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
        
        log.info("Todos os refresh tokens do usuário revogados com sucesso");
    }
    
    public void cleanupExpiredTokens() {
        log.info("Limpando tokens de refresh expirados");
        
        Instant now = Instant.now();
        refreshTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(now))
                .forEach(refreshTokenRepository::delete);
        
        log.info("Limpeza de tokens expirados concluída");
    }
}
```

---

## Controllers

### AuthController

```java
package com.netflix.mercados.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.netflix.mercados.dto.request.LoginRequest;
import com.netflix.mercados.dto.request.RefreshTokenRequest;
import com.netflix.mercados.dto.request.RegisterRequest;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.dto.response.JwtAuthenticationResponse;
import com.netflix.mercados.dto.response.UserResponse;
import com.netflix.mercados.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação e autorização")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria um novo usuário no sistema e retorna tokens de autenticação"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email já registrado"
            )
    })
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        
        log.info("Requisição de registro recebida para email: {}", registerRequest.getEmail());
        
        JwtAuthenticationResponse response = authService.register(registerRequest);
        
        log.info("Registro concluído com sucesso para email: {}", registerRequest.getEmail());
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<JwtAuthenticationResponse>builder()
                        .success(true)
                        .message("Usuário registrado com sucesso")
                        .data(response)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }
    
    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Autentica um usuário e retorna tokens JWT"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Email ou senha inválidos"
            )
    })
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        log.info("Requisição de login recebida para email: {}", loginRequest.getEmail());
        
        JwtAuthenticationResponse response = authService.login(loginRequest);
        
        log.info("Login concluído com sucesso para email: {}", loginRequest.getEmail());
        
        return ResponseEntity
                .ok(ApiResponse.<JwtAuthenticationResponse>builder()
                        .success(true)
                        .message("Login realizado com sucesso")
                        .data(response)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    
    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar tokens",
            description = "Renova o token de acesso usando um refresh token válido"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tokens renovados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Refresh token inválido"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token expirado ou revogado"
            )
    })
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        
        log.debug("Requisição de refresh token recebida");
        
        JwtAuthenticationResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        
        log.debug("Tokens renovados com sucesso");
        
        return ResponseEntity
                .ok(ApiResponse.<JwtAuthenticationResponse>builder()
                        .success(true)
                        .message("Tokens renovados com sucesso")
                        .data(response)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Logout de usuário",
            description = "Realiza o logout e revoga todos os tokens do usuário"
    )
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Logout realizado com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    public ResponseEntity<ApiResponse<String>> logout(Authentication authentication) {
        
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.info("Requisição de logout para email: {}", email);
        
        authService.logout(email);
        
        log.info("Logout concluído com sucesso para email: {}", email);
        
        return ResponseEntity
                .ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Logout realizado com sucesso")
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Obter dados do usuário atual",
            description = "Retorna as informações do usuário autenticado"
    )
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário retornados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.debug("Requisição para obter dados do usuário: {}", email);
        
        UserResponse userResponse = authService.getCurrentUser(email);
        
        log.debug("Dados do usuário retornados com sucesso: {}", email);
        
        return ResponseEntity
                .ok(ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Dados do usuário retornados com sucesso")
                        .data(userResponse)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
```

### UserController (Opcional - Gerenciamento de Usuários)

```java
package com.netflix.mercados.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.dto.response.UserResponse;
import com.netflix.mercados.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Endpoints de gerenciamento de usuários")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Obter usuário por ID",
            description = "Retorna as informações de um usuário específico"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.debug("Requisição para obter usuário - ID: {}", id);
        
        UserResponse userResponse = userService.getUserById(id);
        
        return ResponseEntity
                .ok(ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Usuário encontrado com sucesso")
                        .data(userResponse)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista paginada de todos os usuários do sistema"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Usuários listados com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Apenas admins podem listar usuários"
            )
    })
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        log.debug("Requisição para listar todos os usuários - Página: {}", pageable.getPageNumber());
        
        Page<UserResponse> users = userService.getAllUsers(pageable);
        
        return ResponseEntity
                .ok(ApiResponse.<Page<UserResponse>>builder()
                        .success(true)
                        .message("Usuários listados com sucesso")
                        .data(users)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Deletar usuário",
            description = "Remove um usuário do sistema"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Usuário deletado com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Apenas admins podem deletar usuários"
            )
    })
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        log.info("Requisição para deletar usuário - ID: {}", id);
        
        userService.deleteUser(id);
        
        log.info("Usuário deletado com sucesso - ID: {}", id);
        
        return ResponseEntity
                .ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Usuário deletado com sucesso")
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
```

---

## Configurações Adicionais

### application.yml

```yaml
spring:
  application:
    name: netflix-mercados-api
    
  datasource:
    url: jdbc:mysql://localhost:3306/netflix_mercados
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
    show-sql: false
    open-in-view: false
    
  jackson:
    serialization:
      write-dates-as-timestamps: false
      indent-output: true
    deserialization:
      fail-on-unknown-properties: false
    
app:
  auth:
    secret-key: ${JWT_SECRET_KEY:sua-chave-secreta-muito-segura-aqui-com-pelo-menos-256-bits}
    expiration: 86400000  # 24 horas em milissegundos
    refresh-token-expiration: 604800000  # 7 dias em milissegundos

logging:
  level:
    root: INFO
    com.netflix.mercados: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/netflix-mercados.log
    max-size: 10MB
    max-history: 30

server:
  port: 8080
  servlet:
    context-path: /api
  error:
    include-message: always
    include-binding-errors: always
    include-stacktrace: on_param
    include-exception: false

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
```

### SecurityConfiguration

```java
package com.netflix.mercados.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.netflix.mercados.security.JwtAuthenticationFilter;
import com.netflix.mercados.security.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                                JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### Anotações Importantes

```java
// Para adicionar ao pom.xml:
/*
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

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

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
*/
```

---

## 📝 Resumo da Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                    HTTP Request                         │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │  AuthController        │
        │  UserController        │
        └────────┬───────────────┘
                 │ (DTOs)
                 ▼
        ┌────────────────────────┐
        │  AuthService           │
        │  UserService           │
        │  RefreshTokenService   │
        └────────┬───────────────┘
                 │ (Validators)
                 ├──────────────────────┐
                 │                      │
                 ▼                      ▼
        ┌──────────────────┐  ┌────────────────────┐
        │  UserValidator   │  │ PasswordValidator  │
        │ EmailValidator   │  │ UserConverter      │
        └──────────────────┘  └────────────────────┘
                 │
                 ▼
        ┌────────────────────────┐
        │  UserRepository        │
        │  RefreshTokenRepository│
        └────────┬───────────────┘
                 │
                 ▼
        ┌────────────────────────┐
        │     Database           │
        └────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│                Exception Handling                        │
│  GlobalExceptionHandler → ErrorResponse                  │
└──────────────────────────────────────────────────────────┘
```

---

## ✅ Checklist de Implementação

- [x] Exceções customizadas com tratamento global
- [x] DTOs com validações
- [x] Services com transações
- [x] Controllers com documentação Swagger
- [x] Validators em 3 níveis
- [x] Converters para mapeamento
- [x] Respostas padronizadas
- [x] Logging estruturado com @Slf4j
- [x] Paginação com Pageable
- [x] Autenticação JWT
- [x] Refresh tokens
- [x] Métodos de logout
- [x] Segurança em método (@PreAuthorize)
- [x] Documentação Swagger completa

---

## 🚀 Próximos Passos

1. Implementar entidades JPA (User, RefreshToken)
2. Implementar repositórios
3. Configurar JWT Provider
4. Implementar filtros de segurança
5. Adicionar testes unitários e de integração
6. Configurar CI/CD
7. Implementar rate limiting
8. Adicionar compressão de respostas
7. Implementar cache (Redis)

