# 📚 Índice Completo - Comentários e Favoritos

> Guia de navegação para implementação de Comentários e Favoritos no Netflix Mercados

## 🎯 Visão Geral

Este documento consolida a implementação completa de **Comentários nested/replies** e **Favoritos** para o projeto Netflix Mercados, seguindo as melhores práticas Spring Boot 3.2 com Java 21.

---

## 📁 Estrutura da Documentação

### 1. [COMENTARIOS_IMPLEMENTATION.md](COMENTARIOS_IMPLEMENTATION.md)
Implementação completa do sistema de comentários com suporte a respostas aninhadas.

**Conteúdo:**
- ✅ DTOs (CreateComentarioRequest, UpdateComentarioRequest, ComentarioResponse)
- ✅ Entity Comentario com relacionamentos parent/child
- ✅ ComentarioRepository com queries otimizadas
- ✅ ComentarioService com lógica de negócio
- ✅ ComentarioController com @PreAuthorize
- ✅ Testes unitários completos
- ✅ Scripts SQL de migrations

**Endpoints Implementados:**
```
POST   /api/v1/avaliacoes/{avaliacaoId}/comentarios
GET    /api/v1/avaliacoes/{avaliacaoId}/comentarios
GET    /api/v1/comentarios/{id}/respostas
POST   /api/v1/comentarios/{id}/reply
PUT    /api/v1/comentarios/{id}
DELETE /api/v1/comentarios/{id}
```

---

### 2. [FAVORITOS_IMPLEMENTATION.md](FAVORITOS_IMPLEMENTATION.md)
Implementação completa do sistema de favoritos de mercados.

**Conteúdo:**
- ✅ DTOs (FavoritoRequest, FavoritoResponse, FavoritoCountResponse)
- ✅ Entity Favorito com unique constraint
- ✅ FavoritoRepository com queries otimizadas
- ✅ FavoritoService com lógica de negócio
- ✅ FavoritoController com @PreAuthorize
- ✅ Testes unitários completos
- ✅ Scripts SQL de migrations

**Endpoints Implementados:**
```
POST   /api/v1/favoritos
GET    /api/v1/favoritos
GET    /api/v1/favoritos/count
DELETE /api/v1/favoritos/{mercadoId}
POST   /api/v1/favoritos/toggle/{mercadoId}
GET    /api/v1/favoritos/check/{mercadoId}
```

---

## 🏗️ Arquitetura

### Stack Tecnológico
- **Java:** 21
- **Spring Boot:** 3.2
- **Spring Data JPA:** Hibernate 6
- **Spring Security:** JWT Authentication
- **Lombok:** Redução de boilerplate
- **SpringDoc OpenAPI:** Documentação Swagger
- **JUnit 5 + Mockito:** Testes

### Padrões Implementados
- ✅ DTOs para request/response separation
- ✅ Service Layer com @Transactional
- ✅ Repository com queries customizadas
- ✅ Soft Delete em todas as entities
- ✅ Auditoria (createdAt, updatedAt, createdBy, updatedBy)
- ✅ Versionamento com @Version
- ✅ Paginação com Page/Pageable
- ✅ Exception Handling centralizado
- ✅ Logging com @Slf4j

---

## 🔐 Segurança

### Roles Permitidos
```java
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
```

### Autenticação
Todos os endpoints de escrita requerem autenticação JWT via:
```
Authorization: Bearer <token>
```

### Validações de Propriedade
- ✅ Usuários só podem editar/deletar seus próprios comentários
- ✅ Usuários só podem gerenciar seus próprios favoritos
- ✅ Validações de existência de recursos

---

## 📊 Modelo de Dados

### Comentários
```sql
comentarios
├── id (PK)
├── conteudo (VARCHAR 1000)
├── avaliacao_id (FK -> avaliacoes)
├── user_id (FK -> users)
├── parent_comentario_id (FK -> comentarios)
├── editado (BOOLEAN)
├── created_at, updated_at
├── created_by, updated_by
├── deleted (BOOLEAN)
└── version (BIGINT)
```

**Relacionamentos:**
- Comentário → Avaliação (ManyToOne)
- Comentário → User (ManyToOne)
- Comentário → ParentComentario (ManyToOne - self reference)
- Comentário → Respostas (OneToMany - self reference)

### Favoritos
```sql
favoritos
├── id (PK)
├── user_id (FK -> users)
├── mercado_id (FK -> mercados)
├── created_at, updated_at
├── created_by, updated_by
├── deleted (BOOLEAN)
├── version (BIGINT)
└── UNIQUE(user_id, mercado_id)
```

**Relacionamentos:**
- Favorito → User (ManyToOne)
- Favorito → Mercado (ManyToOne)

---

## 🚀 Funcionalidades Principais

### Sistema de Comentários

#### Comentários Nested (2 níveis)
- ✅ Comentários principais em avaliações
- ✅ Respostas a comentários (1 nível de profundidade)
- ✅ Não permite responder a respostas (evita deep nesting)
- ✅ Soft delete em cascata

#### Features
- ✅ Criação de comentários em avaliações
- ✅ Criação de respostas a comentários
- ✅ Edição de comentários (marca como editado)
- ✅ Deleção de comentários (soft delete)
- ✅ Listagem paginada por avaliação
- ✅ Listagem de respostas de um comentário
- ✅ Contador de comentários na avaliação

### Sistema de Favoritos

#### Features
- ✅ Adicionar mercado aos favoritos
- ✅ Remover mercado dos favoritos
- ✅ Listar favoritos com paginação
- ✅ Contar total de favoritos
- ✅ Toggle de favoritos (add/remove)
- ✅ Verificar se mercado está nos favoritos
- ✅ Contador de favoritos no mercado
- ✅ Constraint de unicidade (user + mercado)

---

## 📝 Validações Implementadas

### Comentários
```java
// CreateComentarioRequest
@NotBlank(message = "Conteúdo do comentário é obrigatório")
@Size(min = 3, max = 1000, message = "Comentário deve ter entre 3 e 1000 caracteres")
private String conteudo;

// Validações de negócio
- Avaliação não pode estar deletada
- Não pode responder a uma resposta
- Comentário pai deve pertencer à mesma avaliação
- Apenas o autor pode editar/deletar
```

### Favoritos
```java
// FavoritoRequest
@NotNull(message = "ID do mercado é obrigatório")
private Long mercadoId;

// Validações de negócio
- Mercado não pode estar deletado
- Não pode favoritar o mesmo mercado duas vezes
- Apenas o dono pode remover seu favorito
```

---

## 🧪 Testes

### Cobertura de Testes

#### ComentarioServiceTest
- ✅ Criar comentário com dados válidos
- ✅ Criar resposta a comentário
- ✅ Atualizar comentário (apenas dono)
- ✅ Deletar comentário (apenas dono)
- ✅ Buscar comentários por avaliação
- ✅ Buscar respostas de comentário
- ✅ Exceções de validação
- ✅ Exceções de autorização

#### FavoritoServiceTest
- ✅ Adicionar favorito com dados válidos
- ✅ Remover favorito
- ✅ Listar favoritos paginados
- ✅ Contar favoritos
- ✅ Verificar existência de favorito
- ✅ Toggle de favorito
- ✅ Exceções de validação

---

## 📋 Migrations SQL

### Ordem de Execução
1. `V1__Create_Comentarios_Table.sql`
2. `V2__Create_Favoritos_Table.sql`
3. `V3__Add_Total_Favoritos_To_Mercados.sql`

### Índices Criados
```sql
-- Comentários
CREATE INDEX idx_comentario_avaliacao ON comentarios(avaliacao_id);
CREATE INDEX idx_comentario_user ON comentarios(user_id);
CREATE INDEX idx_comentario_parent ON comentarios(parent_comentario_id);
CREATE INDEX idx_comentario_created_at ON comentarios(created_at);

-- Favoritos
CREATE INDEX idx_favorito_user ON favoritos(user_id);
CREATE INDEX idx_favorito_mercado ON favoritos(mercado_id);
CREATE INDEX idx_favorito_created_at ON favoritos(created_at);
```

---

## 📖 Documentação Swagger

### Acesso Local
```
http://localhost:8080/swagger-ui.html
```

### Tags
- **Comentários:** Gerenciamento de comentários em avaliações
- **Favoritos:** Gerenciamento de mercados favoritos

### Schemas Disponíveis
```
- CreateComentarioRequest
- UpdateComentarioRequest
- ComentarioResponse
- FavoritoRequest
- FavoritoResponse
- FavoritoCountResponse
- ApiResponse<T>
- Page<T>
```

---

## 🔧 Configuração do Projeto

### Dependencies (pom.xml)
```xml
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

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- SpringDoc OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- JUnit 5 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/netflix_mercados
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
```

---

## 📦 Estrutura de Pacotes

```
com.netflix.mercados
├── controller
│   ├── ComentarioController.java
│   └── FavoritoController.java
├── dto
│   ├── comentario
│   │   ├── request
│   │   │   ├── CreateComentarioRequest.java
│   │   │   └── UpdateComentarioRequest.java
│   │   └── response
│   │       └── ComentarioResponse.java
│   ├── favorito
│   │   ├── request
│   │   │   └── FavoritoRequest.java
│   │   └── response
│   │       ├── FavoritoResponse.java
│   │       └── FavoritoCountResponse.java
│   └── response
│       └── ApiResponse.java
├── entity
│   ├── BaseEntity.java
│   ├── Comentario.java
│   └── Favorito.java
├── repository
│   ├── ComentarioRepository.java
│   └── FavoritoRepository.java
├── service
│   ├── ComentarioService.java
│   └── FavoritoService.java
└── exception
    ├── ResourceNotFoundException.java
    ├── ValidationException.java
    └── UnauthorizedException.java
```

---

## ✅ Checklist de Implementação

### Comentários
- [x] DTOs criados e validados
- [x] Entity Comentario com nested relationships
- [x] Repository com queries customizadas
- [x] Service com lógica de negócio
- [x] Controller com segurança
- [x] Testes unitários
- [x] Migrations SQL
- [x] Documentação Swagger
- [x] Soft delete
- [x] Paginação

### Favoritos
- [x] DTOs criados e validados
- [x] Entity Favorito com unique constraint
- [x] Repository com queries customizadas
- [x] Service com lógica de negócio
- [x] Controller com segurança
- [x] Testes unitários
- [x] Migrations SQL
- [x] Documentação Swagger
- [x] Soft delete
- [x] Paginação
- [x] Contador de favoritos
- [x] Toggle functionality

---

## 🎨 Exemplos de Uso

### Criar Comentário
```bash
curl -X POST http://localhost:8080/api/v1/avaliacoes/1/comentarios \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "conteudo": "Excelente avaliação! Concordo totalmente."
  }'
```

### Responder Comentário
```bash
curl -X POST http://localhost:8080/api/v1/comentarios/5/reply \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "conteudo": "Obrigado pelo feedback!"
  }'
```

### Adicionar Favorito
```bash
curl -X POST http://localhost:8080/api/v1/favoritos \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "mercadoId": 42
  }'
```

### Listar Favoritos
```bash
curl -X GET "http://localhost:8080/api/v1/favoritos?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

---

## 🐛 Troubleshooting

### Erro: "Mercado já está nos favoritos"
**Causa:** Tentativa de adicionar o mesmo mercado duas vezes.  
**Solução:** Verificar com `/favoritos/check/{mercadoId}` antes de adicionar.

### Erro: "Não é permitido responder a uma resposta"
**Causa:** Tentativa de criar nested replies além de 1 nível.  
**Solução:** Responder apenas comentários principais.

### Erro: "Você não tem permissão para editar este comentário"
**Causa:** Usuário tentou editar comentário de outro usuário.  
**Solução:** Apenas o autor pode editar seus próprios comentários.

---

## 📚 Referências

### Documentação Oficial
- [Spring Boot 3.2 Documentation](https://docs.spring.io/spring-boot/docs/3.2.x/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Hibernate 6](https://hibernate.org/orm/documentation/6.0/)

### Documentos do Projeto
- [ENTITIES_AND_REPOSITORIES.md](ENTITIES_AND_REPOSITORIES.md)
- [CONTROLLERS_AND_SERVICES_ARCHITECTURE.md](CONTROLLERS_AND_SERVICES_ARCHITECTURE.md)
- [SERVICE_LAYER.md](SERVICE_LAYER.md)
- [SPRING_BOOT_JWT_CONFIG.md](SPRING_BOOT_JWT_CONFIG.md)

---

## 🚀 Próximos Passos

### Melhorias Futuras
1. **Notificações**
   - Notificar usuário quando receberem resposta em comentário
   - Notificar quando mercado favoritado tiver novidades

2. **Analytics**
   - Dashboard de comentários mais ativos
   - Mercados mais favoritados

3. **Moderação**
   - Sistema de reports para comentários impróprios
   - Aprovação de comentários (opcional)

4. **Cache**
   - Cache de contadores (Redis)
   - Cache de favoritos do usuário

5. **Search**
   - Busca em comentários
   - Filtros avançados em favoritos

---

## 👥 Suporte

Para dúvidas ou problemas:
1. Consulte a documentação completa em cada arquivo específico
2. Verifique os testes unitários para exemplos de uso
3. Revise os logs da aplicação (@Slf4j)

---

**Documentação completa e pronta para produção! ✅**

*Última atualização: Janeiro 2026*
