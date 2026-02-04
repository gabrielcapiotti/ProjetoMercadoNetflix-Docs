# 🚀 INICIANDO O SERVIDOR - Netflix Mercados

## 🎯 Escolha Uma Opção:

### ✅ Opção 1: Docker Compose (RECOMENDADO - Completo)
```bash
docker-compose up -d

# Aguarde 30s para iniciar (PostgreSQL + API)
# Verifique status:
docker-compose ps
docker-compose logs -f api

# Para parar:
docker-compose down
```

**Vantagens:**
- ✅ PostgreSQL automaticamente
- ✅ Ambiente isolado
- ✅ Production-like
- ✅ Network pronto
- ✅ Health checks ativo

**URLs:**
- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

---

### Opção 2: Maven Spring Boot (Local)
```bash
cd /workspaces/ProjetoMercadoNetflix-Docs

# Compilar
mvn clean install

# Executar (precisa de PostgreSQL rodando)
mvn spring-boot:run

# Alternativamente:
java -jar target/netflix-mercados-api-1.0.0.jar
```

**Requisitos:**
- PostgreSQL 16+ em localhost:5432
- Credenciais padrão: postgres/postgres123

---

### Opção 3: Docker Run (Apenas API)
```bash
# Com rede bridge
docker run -d \
  --name netflix-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/netflix_mercados \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres123 \
  netflix-mercados-api:latest
```

---

## ✅ DEPOIS DE INICIAR:

### 1️⃣ Verifique a Saúde
```bash
curl http://localhost:8080/actuator/health
# Response: {"status":"UP"}
```

### 2️⃣ Acesse Swagger
```
http://localhost:8080/swagger-ui.html
```

### 3️⃣ Teste um Endpoint
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@netflix.com","password":"admin123"}'

# Resposta: Token JWT
```

### 4️⃣ Teste Script Automático
```bash
chmod +x test-api.sh
./test-api.sh
# Resultado: 18/18 endpoints ✅
```

### 5️⃣ Importe no Postman
```
1. Import Netflix-Mercados-API.postman_collection.json
2. Import Netflix-Mercados-Environments.postman_environment.json
3. Run collection
```

---

## 📋 CREDENCIAIS PADRÃO

| Role | Email | Senha |
|------|-------|-------|
| ADMIN | admin@netflix.com | admin123 |
| SELLER | seller@netflix.com | seller123 |
| USER | user@netflix.com | user123 |
| CUSTOMER | customer@netflix.com | customer123 |

---

## 🔍 LOGS EM TEMPO REAL

```bash
# Logs API (Docker)
docker-compose logs -f api

# Logs PostgreSQL (Docker)
docker-compose logs -f postgres

# Logs Maven
# Aparecerão no terminal onde mvn foi executado
```

---

## 📊 MONITORAMENTO

```bash
# Status dos containers
docker-compose ps

# CPU e memória
docker stats netflix-mercados-api netflix-mercados-db

# Conexões PostgreSQL
docker-compose exec postgres psql -U postgres -d netflix_mercados -c "\l"

# Actuator endpoints
curl http://localhost:8080/actuator
```

---

## 🛑 PARAR O SERVIDOR

```bash
# Docker Compose
docker-compose down

# Remover volumes (limpar dados)
docker-compose down -v

# Kill Maven (Ctrl+C)
# Ou: pkill -f "spring-boot:run"
```

---

**Qual opção você prefere? Digite o número (1, 2 ou 3)**
