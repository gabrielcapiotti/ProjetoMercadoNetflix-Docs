# 🎯 Quick Start - Netflix Mercados API

Guia de início rápido para começar a usar a API em menos de 5 minutos.

## ⚡ Opção 1: Docker Compose (Mais Rápido)

### Passo 1: Iniciar Stack Completa

```bash
# Clone o repositório (se ainda não tiver)
git clone https://github.com/gabrielcapiotti/ProjetoMercadoNetflix-Docs.git
cd ProjetoMercadoNetflix-Docs

# Iniciar API + PostgreSQL
docker-compose up -d

# Acompanhar logs
docker-compose logs -f api
```

**Aguarde**: `Started NetflixMercadosApplication` (30-60 segundos)

### Passo 2: Testar API

```bash
# Health check
curl http://localhost:8080/actuator/health

# Teste completo de todos os 18 endpoints
./test-api.sh
```

**Resultado Esperado**:
```
🚀 Netflix Mercados API - Quick Test
========================================
✅ PASSED: 18/18 endpoints (100%)
🎉 All tests passed!
```

### Passo 3: Importar no Postman

1. Abra o Postman
2. Import → Upload Files
3. Selecione:
   - `Netflix-Mercados-API.postman_collection.json`
   - `Netflix-Mercados-Environments.postman_environment.json`
4. Selecione environment "Netflix Mercados - Local"
5. Execute "Auth → Login"
6. Teste qualquer endpoint!

✅ **Pronto!** API funcionando em http://localhost:8080

---

## 🔧 Opção 2: Build Manual (Maven)

### Pré-requisitos

- Java 17+
- Maven 3.6+
- PostgreSQL 16

### Passo 1: Configurar Banco de Dados

```bash
# PostgreSQL via Docker
docker run -d \
  --name postgres-netflix \
  -e POSTGRES_DB=netflix_mercados \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

### Passo 2: Configurar application.properties

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/netflix_mercados
spring.datasource.username=postgres
spring.datasource.password=postgres

jwt.secret=YOUR_SECRET_KEY_HERE
jwt.expiration=86400000
```

### Passo 3: Build e Run

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/netflix-mercados-api-1.0.0.jar

# Ou via Maven
mvn spring-boot:run
```

### Passo 4: Verificar

```bash
curl http://localhost:8080/actuator/health
```

---

## 🚢 Opção 3: Kubernetes (Produção)

### Deploy Rápido

```bash
# Criar namespace
kubectl create namespace netflix-mercados

# Configurar secrets
kubectl create secret generic netflix-secrets \
  --from-literal=database.username=postgres \
  --from-literal=database.password='YOUR_PASSWORD' \
  --from-literal=jwt.secret='YOUR_JWT_SECRET' \
  -n netflix-mercados

# Deploy completo
kubectl apply -f k8s/ -n netflix-mercados

# Verificar
kubectl get pods -n netflix-mercados
kubectl get services -n netflix-mercados
```

**Aguarde**: Pods RUNNING (1-2 minutos)

### Port Forward (para testes locais)

```bash
kubectl port-forward svc/netflix-mercados-api 8080:80 -n netflix-mercados
```

Acesse: http://localhost:8080

---

## 📖 Documentação Swagger

Após iniciar a API, acesse:

**Swagger UI**: http://localhost:8080/swagger-ui/index.html

**OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## 🧪 Testando Endpoints

### Via cURL

```bash
# 1. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@netflix.com","password":"admin123"}'

# Copiar o accessToken da resposta
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 2. Testar endpoint autenticado
curl -X POST http://localhost:8080/api/aplicacao-promocao/aplicar/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"codigoPromocao":"PROMO2024","produtos":[1,2,3]}'

# 3. Testar endpoint público
curl http://localhost:8080/api/tendencias/produtos-alta?limite=10
```

### Via Script Automatizado

```bash
# Testar todos os 18 endpoints
./test-api.sh

# Com URL customizada
BASE_URL=http://192.168.1.100:8080 ./test-api.sh

# Com credenciais customizadas
AUTH_EMAIL=user@example.com AUTH_PASSWORD=pass123 ./test-api.sh
```

### Via Postman

Ver guia completo: [POSTMAN_TESTING_GUIDE.md](POSTMAN_TESTING_GUIDE.md)

---

## 🔍 Troubleshooting Rápido

### API não inicia

```bash
# Ver logs
docker-compose logs -f api

# Verificar portas em uso
lsof -i :8080
netstat -tulpn | grep 8080

# Reiniciar
docker-compose restart api
```

### Banco de dados não conecta

```bash
# Verificar PostgreSQL
docker-compose ps postgres

# Testar conexão
docker exec -it postgres psql -U postgres -d netflix_mercados

# Recriar banco
docker-compose down -v
docker-compose up -d
```

### Erro 401 Unauthorized

```bash
# Fazer novo login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@netflix.com","password":"admin123"}'

# Verificar se token está correto no header
# Authorization: Bearer <token>
```

### Portas ocupadas

```bash
# Mudar porta no docker-compose.yml
# ports: "9090:8080"  # Usar 9090 ao invés de 8080

# Ou parar serviço que está usando
sudo lsof -ti:8080 | xargs kill -9
```

---

## 📊 Endpoints Disponíveis

### Autenticação
- `POST /auth/login` - Login

### Promoções (2)
- `POST /api/aplicacao-promocao/aplicar/{mercadoId}` - Aplicar promoção
- `POST /api/aplicacao-promocao/reverter/{mercadoId}` - Reverter promoção

### Recomendações (3)
- `GET /api/recomendacoes/mercado/{mercadoId}` - Por mercado
- `GET /api/recomendacoes/usuario/{usuarioId}` - Por usuário
- `GET /api/recomendacoes/populares` - Populares

### Relatórios (5)
- `GET /api/relatorios/vendas/{mercadoId}` - Vendas
- `GET /api/relatorios/estoque/{mercadoId}` - Estoque
- `GET /api/relatorios/desempenho/{mercadoId}` - Desempenho
- `GET /api/relatorios/clientes/{mercadoId}` - Clientes
- `GET /api/relatorios/financeiro/{mercadoId}` - Financeiro

### Tendências (5)
- `GET /api/tendencias/produtos-alta` - Produtos em alta
- `GET /api/tendencias/mercados-destaque` - Mercados destaque
- `GET /api/tendencias/categorias-crescimento` - Categorias crescimento
- `GET /api/tendencias/ranking-vendedores` - Ranking vendedores
- `GET /api/tendencias/previsao-demanda` - Previsão demanda

### Validação (3)
- `POST /api/validacao/email` - Validar email
- `POST /api/validacao/url` - Validar URL
- `POST /api/validacao/texto-ofensivo` - Validar texto

**Total**: 18 endpoints REST + 1 auth

---

## 🎯 Próximos Passos

### Para Desenvolvedores

1. ✅ Explorar código fonte
2. ✅ Executar testes: `mvn test`
3. ✅ Ver cobertura: `mvn jacoco:report`
4. ✅ Contribuir: Fork → Branch → PR

### Para DevOps

1. ✅ Configurar CI/CD: [CI_CD_GUIDE.md](CI_CD_GUIDE.md)
2. ✅ Setup GitHub Secrets: [GITHUB_SECRETS_SETUP.md](GITHUB_SECRETS_SETUP.md)
3. ✅ Deploy Kubernetes: [k8s/README.md](k8s/README.md)
4. ✅ Monitorar: Prometheus + Grafana

### Para QA

1. ✅ Importar Postman collection
2. ✅ Executar testes: `./test-api.sh`
3. ✅ Rodar Collection Runner
4. ✅ Gerar reports: Newman CLI

---

## 📚 Documentação Completa

| Documento | Descrição |
|-----------|-----------|
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) | Índice completo do projeto |
| [SWAGGER_API_REFERENCE_FASE3.md](SWAGGER_API_REFERENCE_FASE3.md) | Referência completa da API |
| [POSTMAN_TESTING_GUIDE.md](POSTMAN_TESTING_GUIDE.md) | Guia de testes Postman |
| [DOCKER_SETUP.md](DOCKER_SETUP.md) | Docker & Docker Compose |
| [k8s/README.md](k8s/README.md) | Kubernetes deployment |
| [CI_CD_GUIDE.md](CI_CD_GUIDE.md) | CI/CD Pipeline |
| [GITHUB_SECRETS_SETUP.md](GITHUB_SECRETS_SETUP.md) | Configuração de secrets |

---

## 🆘 Precisa de Ajuda?

- 📖 **Documentação**: Veja arquivos `.md` na raiz
- 🐛 **Issues**: https://github.com/gabrielcapiotti/ProjetoMercadoNetflix-Docs/issues
- 💬 **Discussions**: GitHub Discussions

---

**⏱️ Tempo total para setup**: 2-5 minutos  
**🎯 Status**: Production Ready  
**📦 Versão**: 1.0.0

**Criado por**: Netflix Mercados DevOps Team  
**Última atualização**: 2024-02-03
