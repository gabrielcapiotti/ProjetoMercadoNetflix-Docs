# 🚢 Kubernetes Manifests - Netflix Mercados API

Manifestos Kubernetes para deployment da API Netflix Mercados em clusters de produção.

## 📋 Índice

- [Arquivos](#arquivos)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Deploy](#deploy)
- [Monitoramento](#monitoramento)
- [Escalonamento](#escalonamento)
- [Troubleshooting](#troubleshooting)

## 📁 Arquivos

| Arquivo | Descrição |
|---------|-----------|
| `deployment.yaml` | Deployment da API com 3 réplicas |
| `service.yaml` | Services para API e PostgreSQL |
| `configmap.yaml` | Configurações da aplicação |
| `secrets.yaml` | Template para secrets (senhas, JWT) |
| `postgres-deployment.yaml` | Deployment do PostgreSQL com PVC |
| `ingress.yaml` | Ingress para exposição externa com TLS |
| `hpa.yaml` | HorizontalPodAutoscaler (auto-scaling) |

## ✅ Pré-requisitos

- **Kubernetes Cluster**: v1.24+
- **kubectl**: Configurado para o cluster
- **Ingress Controller**: NGINX Ingress
- **Cert-Manager**: Para certificados TLS (opcional)
- **Metrics Server**: Para HPA funcionar

### Instalar Dependências

```bash
# NGINX Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml

# Metrics Server (para HPA)
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# Cert-Manager (para TLS automático)
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

## 🔧 Configuração

### 1. Criar Namespace

```bash
kubectl create namespace netflix-mercados
kubectl config set-context --current --namespace=netflix-mercados
```

### 2. Configurar Secrets

**IMPORTANTE**: Nunca commite secrets reais no Git!

```bash
# Criar secrets de forma segura
kubectl create secret generic netflix-secrets \
  --from-literal=database.username=postgres \
  --from-literal=database.password='SuaSenhaSegura123!' \
  --from-literal=jwt.secret='ChaveJWTMuitoSeguraComPeloMenos256Bits' \
  --namespace=netflix-mercados
```

### 3. Revisar ConfigMap

Edite `configmap.yaml` se necessário:
- URL do banco de dados
- Tempo de expiração do JWT
- Níveis de log

### 4. Atualizar Ingress

Edite `ingress.yaml`:
- Substitua `api.netflix-mercados.com` pelo seu domínio real
- Configure o `cluster-issuer` do cert-manager se usar

## 🚀 Deploy

### Deploy Completo

```bash
# 1. Aplicar secrets
kubectl apply -f k8s/secrets.yaml

# 2. Aplicar ConfigMap
kubectl apply -f k8s/configmap.yaml

# 3. Deploar PostgreSQL
kubectl apply -f k8s/postgres-deployment.yaml

# 4. Criar services
kubectl apply -f k8s/service.yaml

# 5. Aguardar PostgreSQL ficar pronto
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s

# 6. Deploar API
kubectl apply -f k8s/deployment.yaml

# 7. Aplicar Ingress
kubectl apply -f k8s/ingress.yaml

# 8. Aplicar HPA (auto-scaling)
kubectl apply -f k8s/hpa.yaml
```

### Deploy com Um Comando

```bash
kubectl apply -f k8s/ --namespace=netflix-mercados
```

## 📊 Verificação

### Status dos Pods

```bash
kubectl get pods
kubectl get pods -w  # Watch mode
```

### Logs

```bash
# Logs da API
kubectl logs -f deployment/netflix-mercados-api

# Logs do PostgreSQL
kubectl logs -f deployment/postgres

# Logs de um pod específico
kubectl logs <pod-name> -f
```

### Services

```bash
kubectl get services
kubectl get endpoints
```

### Ingress

```bash
kubectl get ingress
kubectl describe ingress netflix-mercados-ingress
```

## 📈 Monitoramento

### Health Checks

```bash
# Port-forward para acessar localmente
kubectl port-forward deployment/netflix-mercados-api 8080:8080

# Acessar endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness
```

### Métricas

```bash
# CPU e memória dos pods
kubectl top pods

# CPU e memória dos nodes
kubectl top nodes

# Métricas do HPA
kubectl get hpa
kubectl describe hpa netflix-mercados-hpa
```

### Eventos

```bash
kubectl get events --sort-by='.lastTimestamp'
kubectl describe pod <pod-name>
```

## ⚖️ Escalonamento

### Manual

```bash
# Escalar para 5 réplicas
kubectl scale deployment netflix-mercados-api --replicas=5

# Verificar
kubectl get deployment netflix-mercados-api
```

### Automático (HPA)

O HPA está configurado para:
- **Mínimo**: 3 réplicas
- **Máximo**: 10 réplicas
- **CPU Target**: 70%
- **Memory Target**: 80%

```bash
# Ver status do HPA
kubectl get hpa netflix-mercados-hpa

# Detalhes
kubectl describe hpa netflix-mercados-hpa
```

## 🔄 Atualizações

### Rolling Update

```bash
# Atualizar imagem
kubectl set image deployment/netflix-mercados-api \
  api=netflix-mercados-api:v2.0.0

# Acompanhar rollout
kubectl rollout status deployment/netflix-mercados-api

# Histórico de rollouts
kubectl rollout history deployment/netflix-mercados-api
```

### Rollback

```bash
# Voltar para versão anterior
kubectl rollout undo deployment/netflix-mercados-api

# Voltar para revisão específica
kubectl rollout undo deployment/netflix-mercados-api --to-revision=2
```

## 🛠️ Troubleshooting

### Pod não inicia

```bash
# Verificar descrição do pod
kubectl describe pod <pod-name>

# Verificar logs
kubectl logs <pod-name>

# Logs do container anterior (se crashou)
kubectl logs <pod-name> --previous
```

### Problemas de Conectividade

```bash
# Testar conexão com PostgreSQL
kubectl exec -it deployment/netflix-mercados-api -- \
  wget -O- http://postgres:5432

# Testar DNS
kubectl exec -it deployment/netflix-mercados-api -- \
  nslookup postgres
```

### Health Check Falhando

```bash
# Ver detalhes do pod
kubectl describe pod <pod-name>

# Verificar eventos
kubectl get events --field-selector involvedObject.name=<pod-name>

# Exec no container
kubectl exec -it <pod-name> -- /bin/sh
wget -O- http://localhost:8080/actuator/health
```

### Secrets não Carregando

```bash
# Verificar se secret existe
kubectl get secrets

# Ver conteúdo do secret (base64)
kubectl get secret netflix-secrets -o yaml

# Decodificar valor
kubectl get secret netflix-secrets -o jsonpath='{.data.database\.username}' | base64 -d
```

## 🗑️ Limpeza

### Remover tudo

```bash
# Deletar todos os recursos
kubectl delete -f k8s/ --namespace=netflix-mercados

# Deletar namespace
kubectl delete namespace netflix-mercados
```

### Remover apenas a API

```bash
kubectl delete deployment netflix-mercados-api
kubectl delete service netflix-mercados-api
kubectl delete ingress netflix-mercados-ingress
kubectl delete hpa netflix-mercados-hpa
```

## 🔐 Segurança

### Network Policies

Crie `networkpolicy.yaml` para isolar tráfego:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-network-policy
spec:
  podSelector:
    matchLabels:
      app: netflix-mercados-api
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: nginx-ingress
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: postgres
    ports:
    - protocol: TCP
      port: 5432
```

### Pod Security

Adicione ao `deployment.yaml`:

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
  fsGroup: 1000
  seccompProfile:
    type: RuntimeDefault
```

## 📚 Recursos Adicionais

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [NGINX Ingress](https://kubernetes.github.io/ingress-nginx/)
- [Cert-Manager](https://cert-manager.io/)
- [Horizontal Pod Autoscaler](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)

## 🎯 Próximos Passos

1. ✅ Configurar monitoring com Prometheus + Grafana
2. ✅ Implementar backup automático do PostgreSQL
3. ✅ Adicionar alertas com AlertManager
4. ✅ Configurar service mesh (Istio/Linkerd)
5. ✅ Implementar GitOps com ArgoCD ou Flux

---

**Documentação criada por**: Netflix Mercados DevOps Team  
**Última atualização**: 2024
