# 🎬 Netflix Mercados - Projeto Full Stack

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-18-blue)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Plataforma de descoberta e avaliação de mercados/restaurantes com geolocalização, análise de ratings, promoções e notificações em tempo real.

## 🚀 Features

### Backend (Spring Boot 3.2)
- ✅ **Autenticação JWT** com refresh tokens
- ✅ **Autorização** baseada em roles (USER, ADMIN, SELLER)
- ✅ **Busca por Geolocalização** usando algoritmo Haversine
- ✅ **Sistema de Avaliações** com histórico e estatísticas
- ✅ **Comentários Aninhados** (nested replies)
- ✅ **Favoritos** com sincronização
- ✅ **Notificações Real-time** via WebSocket
- ✅ **Promoções e Descontos** com validação de código
- ✅ **Horários de Funcionamento** com múltiplos períodos
- ✅ **Soft Delete** para auditoria
- ✅ **Auditoria Completa** (createdBy, updatedBy, timestamps)
- ✅ **Documentação Swagger/OpenAPI** automática

### Frontend (React 18 + TypeScript)
- ✅ **Interface Responsiva** com Tailwind CSS
- ✅ **Mapa Interativo** com localização de mercados
- ✅ **Filtros Avançados** por distância, avaliação, tipo
- ✅ **Gerenciamento de Favoritos**
- ✅ **Sistema de Avaliações e Comentários**
- ✅ **Dashboard Admin**
- ✅ **Notificações em Tempo Real**
- ✅ **Autenticação JWT com Persistência**

### DevOps & Deployment
- ✅ **Docker & Docker Compose** para ambiente local
- ✅ **Kubernetes Manifests** para produção
- ✅ **GitHub Actions** para CI/CD
- ✅ **PostgreSQL** com backups automáticos
- ✅ **Redis** para cache
- ✅ **Elasticsearch** para busca avançada

## 📊 Stack Tecnológico

### Backend
```
Spring Boot 3.2
├── Spring Security 6.0
├── Spring Data JPA / Hibernate
├── Spring WebSocket
├── Spring Mail
└── Spring Actuator

Bibliotecas
├── JWT (jjwt 0.12.3)
├── Lombok
├── MapStruct
├── Jakarta Validation
├── Swagger/OpenAPI
├── PostgreSQL Driver
├── Redis
└── Elasticsearch Client
```

### Frontend
```
React 18
├── TypeScript
├── Vite
├── Tailwind CSS
├── Axios
├── React Router
├── Zustand (State Management)
└── SWR (Data Fetching)
```

### Database & Cache
```
PostgreSQL 15
├── PostGIS (Geolocalização)
├── Extensions
└── Replicação

Redis 7
├── Sessions
├── Cache
└── Rate Limiting

Elasticsearch 8.10
└── Full Text Search
```

## 🏗️ Arquitetura

### Camadas do Backend

```
┌─────────────────────────────────────┐
│     REST API Controllers            │ ← /api/v1/*
├─────────────────────────────────────┤
│     Service Layer (Business Logic)  │
├─────────────────────────────────────┤
│     Repository Layer (Data Access)  │ ← JPA/Hibernate
├─────────────────────────────────────┤
│     Entity Layer (Domain Models)    │
├─────────────────────────────────────┤
│     PostgreSQL Database             │
└─────────────────────────────────────┘

Cross-Cutting Concerns:
├── Security (JWT + Spring Security)
├── Exception Handling (@ControllerAdvice)
├── Logging (@Slf4j)
├── Validation (Jakarta Validation)
├── Auditing (JPA Auditing)
├── Caching (Redis)
└── WebSocket (Real-time Notifications)
```

### Entidades Principais

```
User
├── username, email, password
├── roles: List<Role>
├── favoritos: List<Favorito>
├── avaliacoes: List<Avaliacao>
└── notificacoes: List<Notificacao>

Mercado
├── nome, descricao, endereco
├── latitude, longitude
├── avaliacao_media, total_avaliacoes
├── proprietario: User
├── status: PENDING, APPROVED, REJECTED
├── avaliacoes: List<Avaliacao>
├── promocoes: List<Promocao>
├── horarios: List<HorarioFuncionamento>
└── imagens: List<String>

Avaliacao
├── usuario: User
├── mercado: Mercado
├── estrelas: 1-5
├── comentario: String
├── comentarios: List<Comentario>
└── timestamp: LocalDateTime

Promocao
├── mercado: Mercado
├── codigo: String (unique)
├── desconto: Integer (1-100)
├── vigencia_inicio, vigencia_fim: LocalDateTime
└── uso_maximo: Integer

Notificacao
├── usuario: User
├── titulo, mensagem: String
├── tipo: MERCADO_CRIADO, AVALIACAO, PROMOCAO, etc
└── lido: Boolean
```

## 🚀 Quick Start

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+ (para frontend)
- Git

### Instalação Local

#### 1. Clonar Repositório

```bash
git clone https://github.com/seu-usuario/ProjetoMercadoNetflix.git
cd ProjetoMercadoNetflix
```

#### 2. Iniciar Banco de Dados e Serviços

```bash
docker-compose up -d
```

Isso iniciará:
- PostgreSQL (porta 5432)
- Redis (porta 6379)
- Elasticsearch (porta 9200)

#### 3. Compilar Backend

```bash
mvn clean compile
```

#### 4. Executar Testes

```bash
mvn test
```

#### 5. Iniciar Backend

```bash
mvn spring-boot:run
```

Backend rodará em: `http://localhost:8080`

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

#### 6. Instalação Frontend (em outra aba do terminal)

```bash
cd frontend
npm install
npm run dev
```

Frontend rodará em: `http://localhost:5173`

## 📚 Documentação

- **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - Guia completo de integração
- **[API_ENDPOINTS.md](docs/API_ENDPOINTS.md)** - Lista de todos os endpoints
- **[DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md)** - Schema do banco de dados
- **[SECURITY.md](docs/SECURITY.md)** - Documentação de segurança
- **[DEPLOYMENT.md](docs/DEPLOYMENT.md)** - Guia de deployment
- **Swagger UI** - `http://localhost:8080/swagger-ui.html`

## 📝 Exemplos de API

### Autenticação

#### Register
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@example.com",
  "password": "Senha@123"
}

Response (201):
{
  "id": 1,
  "username": "usuario",
  "email": "usuario@example.com",
  "roles": ["USER"],
  "createdAt": "2024-01-15T10:30:00"
}
```

#### Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "Senha@123"
}

Response (200):
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": { ... }
}
```

### Mercados

#### Listar Mercados Próximos
```bash
GET /api/v1/mercados/nearby?latitude=-23.5505&longitude=-46.6333&raio=10
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "nome": "Mercado Central",
    "descricao": "Mercado de frutas e verduras",
    "latitude": -23.5505,
    "longitude": -46.6333,
    "distancia": 0.5,
    "avaliacaoMedia": 4.5,
    "totalAvaliacoes": 120
  }
]
```

#### Criar Avaliação
```bash
POST /api/v1/avaliacoes
Authorization: Bearer {token}
Content-Type: application/json

{
  "mercadoId": 1,
  "estrelas": 5,
  "comentario": "Excelente mercado! Tudo muito fresco."
}

Response (201):
{
  "id": 42,
  "mercadoId": 1,
  "usuarioId": 5,
  "estrelas": 5,
  "comentario": "Excelente mercado! Tudo muito fresco.",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Notificações WebSocket

#### Conectar WebSocket
```javascript
const ws = new WebSocket('ws://localhost:8080/ws/notificacoes/1');

ws.onmessage = (event) => {
  const notification = JSON.parse(event.data);
  console.log('Nova notificação:', notification);
};

// Enviar mensagem (do servidor para o cliente)
{
  "id": 1,
  "titulo": "Nova Promoção!",
  "mensagem": "Desconto de 20% em Mercado Central",
  "tipo": "PROMOCAO",
  "createdAt": "2024-01-15T10:30:00"
}
```

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes de Integração

```bash
mvn verify
```

### Cobertura de Testes

```bash
mvn jacoco:report
# Abrir target/site/jacoco/index.html
```

## 🐳 Docker

### Executar em Docker

```bash
docker-compose up -d
```

### Parar Containers

```bash
docker-compose down
```

### Logs

```bash
docker-compose logs -f api
```

## ☁️ Deployment em Produção

### Kubernetes

```bash
kubectl apply -f k8s/namespace.yml
kubectl apply -f k8s/configmap.yml
kubectl apply -f k8s/secret.yml
kubectl apply -f k8s/deployment.yml
kubectl apply -f k8s/service.yml
kubectl apply -f k8s/ingress.yml
```

### GitHub Actions

Todos os push para `main` disparam:
- ✅ Build da aplicação
- ✅ Testes unitários
- ✅ Testes de integração
- ✅ Build da imagem Docker
- ✅ Push para Docker Registry
- ✅ Deploy em Kubernetes

Ver: `.github/workflows/ci-cd.yml`

## 📊 Endpoints

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/v1/auth/register` | Registrar novo usuário | Não |
| POST | `/api/v1/auth/login` | Login | Não |
| POST | `/api/v1/auth/refresh` | Renovar token | Sim |
| POST | `/api/v1/auth/logout` | Logout | Sim |
| GET | `/api/v1/mercados` | Listar mercados | Não |
| GET | `/api/v1/mercados/{id}` | Detalhes mercado | Não |
| POST | `/api/v1/mercados` | Criar mercado | Sim (SELLER) |
| GET | `/api/v1/mercados/nearby` | Mercados próximos | Não |
| POST | `/api/v1/avaliacoes` | Criar avaliação | Sim |
| GET | `/api/v1/mercados/{id}/avaliacoes` | Avaliações de mercado | Não |
| POST | `/api/v1/favoritos` | Adicionar favorito | Sim |
| GET | `/api/v1/favoritos` | Meus favoritos | Sim |
| GET | `/api/v1/notificacoes` | Minhas notificações | Sim |
| POST | `/api/v1/mercados/{id}/promocoes` | Criar promoção | Sim (SELLER) |

Ver documentação completa em: `http://localhost:8080/swagger-ui.html`

## 🔒 Segurança

- ✅ JWT (JSON Web Token) com refresh tokens
- ✅ HTTPS/TLS em produção
- ✅ CORS configurado
- ✅ Spring Security
- ✅ Password Encoding (bcrypt)
- ✅ Rate Limiting
- ✅ SQL Injection Protection
- ✅ XSS Protection
- ✅ CSRF Protection
- ✅ Input Validation
- ✅ Autorização baseada em roles

Ver: [SECURITY.md](docs/SECURITY.md)

## 🐛 Troubleshooting

### Erro de Conexão com PostgreSQL
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```
**Solução:** Certifique-se que PostgreSQL está rodando: `docker-compose ps`

### Erro de CORS
```
Access to XMLHttpRequest at 'http://localhost:8080/api/v1/mercados' from origin 'http://localhost:5173' has been blocked
```
**Solução:** Verificar `app.corsAllowedOrigins` em `application.yml`

### JWT Inválido
```
org.springframework.security.authentication.BadCredentialsException: Bad credentials
```
**Solução:** Verificar que JWT_SECRET possui pelo menos 256 bits

## 📞 Suporte

- **Issues:** [GitHub Issues](https://github.com/seu-usuario/ProjetoMercadoNetflix/issues)
- **Discussions:** [GitHub Discussions](https://github.com/seu-usuario/ProjetoMercadoNetflix/discussions)
- **Email:** seu-email@example.com

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja [LICENSE](LICENSE) para detalhes.

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -am 'Add MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 🙏 Agradecimentos

- Spring Boot Team
- React Community
- PostgreSQL Community

---

**Desenvolvido com ❤️ para simplificar a descoberta de mercados**

Última atualização: 15/01/2024 | Versão: 1.0.0
