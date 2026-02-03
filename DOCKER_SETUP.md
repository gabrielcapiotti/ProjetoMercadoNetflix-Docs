# 🐳 Docker Setup - Netflix Mercados API

**Status:** ✅ Configuração completa de containerização

---

## 📦 Arquivos Criados

1. [Dockerfile](Dockerfile) - Multi-stage build otimizado
2. [docker-compose.yml](docker-compose.yml) - Stack completa (API + PostgreSQL)
3. [.dockerignore](.dockerignore) - Exclusões para build

---

## 🚀 Quick Start

### Opção 1: Docker Compose (Recomendado)

```bash
# Build e iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f api

# Parar serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

### Opção 2: Docker Manual

```bash
# Build da imagem
docker build -t netflix-mercados-api .

# Executar container
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/netflix_mercados \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres123 \
  --name netflix-api \
  netflix-mercados-api
```

---

## ⚙️ Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SPRING_DATASOURCE_URL` | URL do banco PostgreSQL | jdbc:postgresql://postgres:5432/netflix_mercados |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | postgres123 |
| `JWT_SECRET` | Secret para JWT | (definir via .env) |
| `JWT_EXPIRATION` | Tempo de expiração JWT (ms) | 86400000 (24h) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia DDL | update |

### Arquivo .env (Recomendado)

Crie um arquivo `.env` na raiz:

```env
JWT_SECRET=seu-secret-super-seguro-com-no-minimo-256-bits
JWT_EXPIRATION=86400000
POSTGRES_PASSWORD=senha-super-segura
```

Use no docker-compose:

```bash
docker-compose --env-file .env up -d
```

---

## 🏗️ Arquitetura Multi-Stage

### Stage 1: Build (maven:3.9.6-eclipse-temurin-17)
- Baixa dependências Maven
- Compila código-fonte
- Gera JAR executável
- **Resultado:** ~500 MB

### Stage 2: Runtime (eclipse-temurin:17-jre-alpine)
- Copia apenas JAR final
- Usuário não-root (spring:spring)
- Health check configurado
- **Resultado:** ~200 MB

**Benefícios:**
- Imagem final 60% menor
- Mais seguro (JRE ao invés de JDK)
- Sem código-fonte na imagem final

---

## 🔍 Health Checks

### API Health Check
```bash
# Via Docker
docker ps

# Via curl
curl http://localhost:8080/actuator/health

# Via wget (usado pelo container)
wget --spider http://localhost:8080/actuator/health
```

### PostgreSQL Health Check
```bash
# Dentro do container
docker exec netflix-mercados-db pg_isready -U postgres
```

---

## 📊 Volumes

### Persistência de Dados

O `docker-compose.yml` define volume para PostgreSQL:

```yaml
volumes:
  postgres_data:  # Persiste dados do banco
```

**Gerenciar volumes:**

```bash
# Listar volumes
docker volume ls

# Inspecionar volume
docker volume inspect netflix-mercados_postgres_data

# Remover volume (CUIDADO: apaga dados)
docker volume rm netflix-mercados_postgres_data
```

---

## 🌐 Networking

### Rede Bridge

Docker Compose cria rede `netflix-network`:

- **postgres**: Acessível via `postgres:5432` (interno)
- **api**: Acessível via `localhost:8080` (externo)

**DNS interno:**
- API se conecta ao banco via `jdbc:postgresql://postgres:5432/...`
- Sem necessidade de IPs fixos

---

## 🔐 Segurança

### Usuário Não-Root

```dockerfile
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
```

### Secrets (Produção)

Para produção, use Docker Secrets ou Kubernetes Secrets:

```bash
# Docker Swarm
echo "meu-secret-jwt" | docker secret create jwt_secret -

# Kubernetes
kubectl create secret generic jwt-secret \
  --from-literal=jwt-secret='meu-secret-jwt'
```

---

## 🚢 Deploy em Produção

### Docker Swarm

```bash
# Inicializar swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml netflix

# Listar services
docker service ls

# Escalar API
docker service scale netflix_api=3
```

### Kubernetes

Veja [kubernetes/](kubernetes/) para manifests completos.

---

## 🧪 Comandos Úteis

### Logs

```bash
# Logs da API
docker-compose logs -f api

# Logs do PostgreSQL
docker-compose logs -f postgres

# Últimas 100 linhas
docker-compose logs --tail=100 api
```

### Debugging

```bash
# Entrar no container da API
docker exec -it netflix-mercados-api sh

# Entrar no container do PostgreSQL
docker exec -it netflix-mercados-db psql -U postgres -d netflix_mercados

# Ver processos
docker-compose top
```

### Limpeza

```bash
# Parar e remover containers
docker-compose down

# Remover volumes também
docker-compose down -v

# Remover imagens órfãs
docker image prune

# Limpeza completa (CUIDADO)
docker system prune -a --volumes
```

---

## 📈 Monitoramento

### Métricas Spring Boot Actuator

Acesse:
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

### Docker Stats

```bash
# Uso de recursos em tempo real
docker stats

# Apenas API
docker stats netflix-mercados-api
```

---

## 🔄 CI/CD Integration

### GitHub Actions

```yaml
- name: Build Docker image
  run: docker build -t netflix-mercados-api .

- name: Run tests in container
  run: docker run --rm netflix-mercados-api mvn test
```

### GitLab CI

```yaml
docker-build:
  stage: build
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHORT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHORT_SHA
```

---

## ⚠️ Troubleshooting

### API não inicia

```bash
# Ver logs detalhados
docker-compose logs api

# Verificar se PostgreSQL está saudável
docker-compose ps
```

### Conexão com banco falha

```bash
# Verificar network
docker network inspect netflix-mercados_netflix-network

# Testar conexão do container da API
docker exec netflix-mercados-api ping postgres
```

### Health check falha

```bash
# Verificar actuator
curl http://localhost:8080/actuator/health

# Ver configuração do health check
docker inspect netflix-mercados-api | grep -A 10 Healthcheck
```

---

## 📚 Próximos Passos

1. Configure secrets em produção (não use valores padrão)
2. Adicione reverse proxy (Nginx/Traefik)
3. Configure SSL/TLS
4. Implemente backup automático do PostgreSQL
5. Configure monitoramento (Prometheus + Grafana)
6. Adicione rate limiting
7. ✅ **COMPLETO**: Configure autoscaling (Kubernetes HPA) - ver [k8s/README.md](k8s/README.md)

## 🚢 Deploy Kubernetes

Para deployment em clusters Kubernetes, todos os manifestos estão prontos em `k8s/`:

```bash
# Deploy completo
kubectl apply -f k8s/ --namespace=netflix-mercados

# Verificar status
kubectl get pods
kubectl get services
kubectl get ingress

# Ver detalhes
kubectl describe hpa netflix-mercados-hpa
```

📖 **Documentação completa**: [k8s/README.md](k8s/README.md)

---

**Gerado em:** 03 de Fevereiro de 2026  
**Versão Docker:** 24.x  
**Versão Docker Compose:** 3.8
