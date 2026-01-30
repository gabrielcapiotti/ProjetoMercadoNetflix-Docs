# 🚀 Guia de Integração - Netflix Mercados Backend

## Estrutura do Projeto

```
ProjetoMercadoNetflix/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/netflix/mercado/
│   │   │   ├── ProjetoMercadoNetflixApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebSocketConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── AuditingConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── MercadoController.java
│   │   │   │   ├── AvaliacaoController.java
│   │   │   │   ├── ComentarioController.java
│   │   │   │   ├── FavoritoController.java
│   │   │   │   ├── NotificacaoController.java
│   │   │   │   ├── PromocaoController.java
│   │   │   │   └── HorarioController.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── MercadoService.java
│   │   │   │   ├── AvaliacaoService.java
│   │   │   │   ├── ComentarioService.java
│   │   │   │   ├── FavoritoService.java
│   │   │   │   ├── NotificacaoService.java
│   │   │   │   ├── PromocaoService.java
│   │   │   │   └── HorarioService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   ├── MercadoRepository.java
│   │   │   │   ├── AvaliacaoRepository.java
│   │   │   │   ├── ComentarioRepository.java
│   │   │   │   ├── FavoritoRepository.java
│   │   │   │   ├── NotificacaoRepository.java
│   │   │   │   ├── PromocaoRepository.java
│   │   │   │   └── HorarioRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Mercado.java
│   │   │   │   ├── Avaliacao.java
│   │   │   │   ├── Comentario.java
│   │   │   │   ├── Favorito.java
│   │   │   │   ├── Notificacao.java
│   │   │   │   ├── Promocao.java
│   │   │   │   └── HorarioFuncionamento.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   └── *.java
│   │   │   │   ├── response/
│   │   │   │   │   └── *.java
│   │   │   │   └── common/
│   │   │   │       └── *.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   ├── UserPrincipal.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── exception/
│   │   │   │   ├── BaseException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── validator/
│   │   │   │   ├── UserValidator.java
│   │   │   │   ├── EmailValidator.java
│   │   │   │   ├── PasswordValidator.java
│   │   │   │   └── MercadoValidator.java
│   │   │   ├── converter/
│   │   │   │   ├── UserConverter.java
│   │   │   │   ├── MercadoConverter.java
│   │   │   │   ├── AvaliacaoConverter.java
│   │   │   │   └── ...Converter.java
│   │   │   ├── util/
│   │   │   │   ├── GeolocationUtils.java
│   │   │   │   ├── PasswordEncoderUtil.java
│   │   │   │   ├── DateUtils.java
│   │   │   │   └── JwtUtils.java
│   │   │   ├── event/
│   │   │   │   ├── MercadoCreatedEvent.java
│   │   │   │   ├── AvaliacaoCreatedEvent.java
│   │   │   │   └── UserRegisteredEvent.java
│   │   │   └── listener/
│   │   │       ├── MercadoEventListener.java
│   │   │       ├── AvaliacaoEventListener.java
│   │   │       └── UserEventListener.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── messages.properties
│   │       └── db/migration/
│   │           └── V1__initial_schema.sql
│   └── test/
│       └── java/com/netflix/mercado/
│           ├── controller/
│           ├── service/
│           └── integration/
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
└── README.md
```

## Passos de Implementação

### 1. Clonar e Configurar Repositório

```bash
cd /tmp
git clone https://github.com/seu-usuario/ProjetoMercadoNetflix.git
cd ProjetoMercadoNetflix
git checkout develop
```

### 2. Estrutura Maven já Criada

✅ **pom.xml** com todas as dependências:
- Spring Boot 3.2
- Spring Security com JWT
- Spring Data JPA
- PostgreSQL Driver
- Elasticsearch
- Redis
- WebSocket
- Swagger/OpenAPI
- Lombok
- MapStruct
- JUnit 5

### 3. Criar Entidades (JPA/Hibernate)

Já temos:
- ✅ BaseEntity com auditoria
- Criar: User, Role, Mercado, Avaliacao, Comentario, Favorito, Notificacao, Promocao, HorarioFuncionamento

**Arquivo:** `src/main/java/com/netflix/mercado/entity/*.java`

### 4. Criar Repositories

**Arquivo:** `src/main/java/com/netflix/mercado/repository/*.java`

Estender `JpaRepository` e `JpaSpecificationExecutor` para filtros avançados.

### 5. Criar DTOs (Request/Response)

**Arquivo:** `src/main/java/com/netflix/mercado/dto/**/*.java`

Incluir validações com Jakarta Validation:
- `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Pattern`

### 6. Criar Services (Lógica de Negócio)

**Arquivo:** `src/main/java/com/netflix/mercado/service/*.java`

Padrão:
```java
@Service
@Transactional
@Slf4j
public class MercadoService {
    // lógica de negócio
}
```

### 7. Criar Controllers (REST Endpoints)

**Arquivo:** `src/main/java/com/netflix/mercado/controller/*.java`

Padrão:
```java
@RestController
@RequestMapping("/api/v1/mercados")
@Slf4j
public class MercadoController {
    // endpoints
}
```

### 8. Exception Handling Global

**Arquivo:** `src/main/java/com/netflix/mercado/exception/GlobalExceptionHandler.java`

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(...) { ... }
}
```

### 9. Security Configuration

**Arquivo:** `src/main/java/com/netflix/mercado/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // configurar JWT, CORS, etc
    }
}
```

### 10. WebSocket Configuration

**Arquivo:** `src/main/java/com/netflix/mercado/config/WebSocketConfig.java`

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig {
    @Bean
    public WebSocketHandler notificacaoHandler() { ... }
}
```

## Checklist de Implementação

- [ ] Criar todas as **Entidades** (10 arquivos)
- [ ] Criar todos os **Repositories** (10 arquivos)
- [ ] Criar todos os **DTOs** (40+ arquivos)
- [ ] Criar todos os **Services** (10 arquivos)
- [ ] Criar todos os **Controllers** (8 arquivos)
- [ ] Criar **Exception Handlers** (5 arquivos)
- [ ] Criar **Validators** (5 arquivos)
- [ ] Criar **Converters** (5 arquivos)
- [ ] Criar **Utilities** (3 arquivos)
- [ ] Configurar **Security** (SecurityConfig.java)
- [ ] Configurar **WebSocket** (WebSocketConfig.java)
- [ ] Configurar **OpenAPI/Swagger** (OpenApiConfig.java)
- [ ] Criar **Scripts SQL** (migrations)
- [ ] Criar **Testes Unitários** (test/)
- [ ] Criar **Testes de Integração** (test/)
- [ ] Configurar **Docker/Compose**
- [ ] Configurar **GitHub Actions CI/CD**

## Compilação e Execução

### Compilar

```bash
mvn clean compile
```

### Executar Testes

```bash
mvn test
```

### Executar Aplicação

```bash
mvn spring-boot:run
```

### Acessar Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### Acessar H2 Console (para testes)

```
http://localhost:8080/h2-console
```

## Variáveis de Ambiente

Criar arquivo `.env`:

```env
# JWT
JWT_SECRET=sua_chave_secreta_de_256_bits_aqui

# Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/netflix_mercado
DB_USER=netflix_user
DB_PASSWORD=netflix_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Mail
MAIL_USERNAME=seu_email@gmail.com
MAIL_PASSWORD=sua_senha_app

# AWS S3
AWS_ACCESS_KEY=sua_chave
AWS_SECRET_KEY=sua_secreta

# Elasticsearch
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200
```

## Testes com cURL

### Register

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario",
    "email": "usuario@example.com",
    "password": "Senha@123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "password": "Senha@123"
  }'
```

### Usar Token

```bash
curl -X GET http://localhost:8080/api/v1/mercados \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

## Problemas Comuns

### 1. Erro de Conexão PostgreSQL
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```
**Solução:** Iniciar PostgreSQL: `docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=netflix_password postgres:15`

### 2. Erro de Validação JWT
```
io.jsonwebtoken.JwtException: JWT signature does not match
```
**Solução:** Verificar que `JWT_SECRET` possui pelo menos 256 bits (32 caracteres)

### 3. Erro de CORS
```
Access to XMLHttpRequest has been blocked by CORS policy
```
**Solução:** Verificar `app.corsAllowedOrigins` em `application.yml`

## Próximos Passos

1. ✅ Criar Entidades
2. ✅ Criar Repositories
3. ✅ Criar DTOs
4. ✅ Criar Services
5. ✅ Criar Controllers
6. ⏳ Criar Testes
7. ⏳ Criar Frontend (React)
8. ⏳ Criar Docker
9. ⏳ Criar Kubernetes
10. ⏳ Criar CI/CD
