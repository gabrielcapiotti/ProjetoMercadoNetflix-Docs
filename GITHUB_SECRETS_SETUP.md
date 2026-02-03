# 🔐 Configuração de GitHub Secrets - Netflix Mercados API

Guia passo a passo para configurar todos os secrets necessários para o pipeline CI/CD funcionar.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Secrets Obrigatórios](#secrets-obrigatórios)
- [Secrets Opcionais](#secrets-opcionais)
- [Como Configurar](#como-configurar)
- [Gerando Kubeconfig](#gerando-kubeconfig)
- [Testando Configuração](#testando-configuração)
- [Troubleshooting](#troubleshooting)

## 🎯 Visão Geral

O pipeline CI/CD requer secrets para:
- 🔑 Autenticação em clusters Kubernetes
- 📢 Notificações Slack
- 🔒 Scans de segurança (Snyk)
- 📊 Coverage tracking (Codecov)

## ✅ Secrets Obrigatórios

### 1. KUBE_CONFIG_DEV (Kubernetes Development)

**Descrição**: Kubeconfig em base64 para cluster de desenvolvimento

**Como gerar**:
```bash
# 1. Conecte-se ao cluster de desenvolvimento
kubectl config use-context dev-cluster

# 2. Extraia o kubeconfig
kubectl config view --flatten --minify > kubeconfig-dev.yaml

# 3. Converta para base64 (sem quebras de linha)
cat kubeconfig-dev.yaml | base64 -w 0

# 4. Copie o output e adicione como secret
```

**Valor**: `<base64-encoded-kubeconfig>`

---

### 2. KUBE_CONFIG_STAGING (Kubernetes Staging)

**Descrição**: Kubeconfig em base64 para cluster de staging

**Como gerar**:
```bash
# 1. Conecte-se ao cluster de staging
kubectl config use-context staging-cluster

# 2. Extraia o kubeconfig
kubectl config view --flatten --minify > kubeconfig-staging.yaml

# 3. Converta para base64
cat kubeconfig-staging.yaml | base64 -w 0
```

**Valor**: `<base64-encoded-kubeconfig>`

---

### 3. KUBE_CONFIG_PROD (Kubernetes Production)

**Descrição**: Kubeconfig em base64 para cluster de produção

**Como gerar**:
```bash
# 1. Conecte-se ao cluster de produção
kubectl config use-context prod-cluster

# 2. Extraia o kubeconfig
kubectl config view --flatten --minify > kubeconfig-prod.yaml

# 3. Converta para base64
cat kubeconfig-prod.yaml | base64 -w 0
```

**Valor**: `<base64-encoded-kubeconfig>`

⚠️ **IMPORTANTE**: Use um service account com permissões limitadas em produção!

---

## 🔔 Secrets Opcionais

### 4. SLACK_WEBHOOK (Notificações)

**Descrição**: URL do webhook do Slack para notificações de build

**Como obter**:
1. Acesse https://api.slack.com/apps
2. Crie um novo app ou selecione existente
3. Vá em "Incoming Webhooks"
4. Ative "Activate Incoming Webhooks"
5. Clique em "Add New Webhook to Workspace"
6. Escolha o canal e autorize
7. Copie a Webhook URL

**Valor**: `https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX`

**Exemplo de notificação**:
```
✅ Build #123 - SUCCESS
Branch: main
Commit: abc1234
Author: @usuario
Time: 5m 32s
```

---

### 5. SNYK_TOKEN (Security Scanning)

**Descrição**: Token da API do Snyk para vulnerability scanning

**Como obter**:
1. Crie conta em https://snyk.io/
2. Acesse https://app.snyk.io/account
3. Em "General" → "Auth Token"
4. Clique em "Click to show" e copie o token

**Valor**: `snyk-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`

**Free tier**: Ilimitado para projetos open source!

---

### 6. CODECOV_TOKEN (Coverage Tracking)

**Descrição**: Token do Codecov para upload de coverage reports

**Como obter**:
1. Acesse https://codecov.io/
2. Faça login com GitHub
3. Adicione seu repositório
4. Copie o "Repository Upload Token"

**Valor**: `codecov-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`

**Alternativa**: Codecov funciona sem token para repositórios públicos!

---

## 🛠️ Como Configurar no GitHub

### Via Interface Web

1. **Acesse o repositório** no GitHub:
   ```
   https://github.com/gabrielcapiotti/ProjetoMercadoNetflix-Docs
   ```

2. **Vá para Settings**:
   - Clique em "Settings" (ícone de engrenagem)

3. **Acesse Secrets**:
   - No menu lateral: "Secrets and variables" → "Actions"

4. **Adicione cada secret**:
   - Clique em "New repository secret"
   - **Name**: Nome do secret (ex: `KUBE_CONFIG_DEV`)
   - **Value**: Valor do secret (cole o base64 ou token)
   - Clique em "Add secret"

5. **Repita para todos os secrets**

### Via GitHub CLI

Se você tem `gh` CLI instalado:

```bash
# Instalar gh CLI (se necessário)
# Ubuntu/Debian: sudo apt install gh
# macOS: brew install gh

# Login
gh auth login

# Adicionar secrets
gh secret set KUBE_CONFIG_DEV < kubeconfig-dev-base64.txt
gh secret set KUBE_CONFIG_STAGING < kubeconfig-staging-base64.txt
gh secret set KUBE_CONFIG_PROD < kubeconfig-prod-base64.txt
gh secret set SLACK_WEBHOOK -b "https://hooks.slack.com/services/..."
gh secret set SNYK_TOKEN -b "snyk-xxxxx..."
gh secret set CODECOV_TOKEN -b "codecov-xxxxx..."

# Listar secrets configurados
gh secret list
```

---

## 🔑 Gerando Kubeconfig Seguro

### Opção 1: Service Account (Recomendado para Produção)

```bash
# 1. Criar service account
kubectl create serviceaccount github-actions -n netflix-mercados

# 2. Criar role com permissões mínimas
cat <<EOF | kubectl apply -f -
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: github-actions-role
  namespace: netflix-mercados
rules:
- apiGroups: ["apps"]
  resources: ["deployments"]
  verbs: ["get", "list", "update", "patch"]
- apiGroups: [""]
  resources: ["pods", "services"]
  verbs: ["get", "list"]
EOF

# 3. Criar role binding
kubectl create rolebinding github-actions-binding \
  --role=github-actions-role \
  --serviceaccount=netflix-mercados:github-actions \
  -n netflix-mercados

# 4. Obter token do service account
SECRET_NAME=$(kubectl get serviceaccount github-actions -n netflix-mercados -o jsonpath='{.secrets[0].name}')
TOKEN=$(kubectl get secret $SECRET_NAME -n netflix-mercados -o jsonpath='{.data.token}' | base64 -d)

# 5. Obter CA certificate
CA_CERT=$(kubectl get secret $SECRET_NAME -n netflix-mercados -o jsonpath='{.data.ca\.crt}')

# 6. Obter server URL
SERVER=$(kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}')

# 7. Criar kubeconfig
cat > kubeconfig-sa.yaml <<EOF
apiVersion: v1
kind: Config
clusters:
- cluster:
    certificate-authority-data: $CA_CERT
    server: $SERVER
  name: github-actions-cluster
contexts:
- context:
    cluster: github-actions-cluster
    namespace: netflix-mercados
    user: github-actions
  name: github-actions
current-context: github-actions
users:
- name: github-actions
  user:
    token: $TOKEN
EOF

# 8. Converter para base64
cat kubeconfig-sa.yaml | base64 -w 0 > kubeconfig-sa-base64.txt
```

### Opção 2: Kubeconfig Existente (Desenvolvimento)

```bash
# Para desenvolvimento/staging, pode usar seu kubeconfig atual
kubectl config view --flatten --minify | base64 -w 0
```

---

## ✅ Testando Configuração

### 1. Testar Kubeconfig Local

```bash
# Decodificar e testar
echo "<seu-base64>" | base64 -d > test-kubeconfig.yaml
kubectl --kubeconfig=test-kubeconfig.yaml get pods

# Se funcionar, está correto!
```

### 2. Testar Pipeline

```bash
# Trigger manual do workflow
gh workflow run ci-cd.yml

# Verificar status
gh run list

# Ver logs
gh run view --log
```

### 3. Verificar Secrets no GitHub

```bash
# Listar secrets configurados
gh secret list

# Output esperado:
# KUBE_CONFIG_DEV        Updated 2024-02-03
# KUBE_CONFIG_STAGING    Updated 2024-02-03
# KUBE_CONFIG_PROD       Updated 2024-02-03
# SLACK_WEBHOOK          Updated 2024-02-03
# SNYK_TOKEN            Updated 2024-02-03
```

---

## 🔄 Rotação de Secrets

### Quando Rotacionar

- ✅ **Service account tokens**: A cada 90 dias
- ✅ **Webhook URLs**: Se comprometidas
- ✅ **API tokens**: Conforme policy da empresa

### Como Rotacionar

```bash
# 1. Gerar novo secret
kubectl create token github-actions -n netflix-mercados --duration=2160h

# 2. Atualizar no GitHub
gh secret set KUBE_CONFIG_PROD -b "<novo-valor>"

# 3. Testar
gh workflow run ci-cd.yml

# 4. Revogar secret antigo (após confirmar que funciona)
```

---

## 🚨 Troubleshooting

### Secret não está funcionando

```bash
# Verificar se secret existe
gh secret list

# Re-criar secret
gh secret delete KUBE_CONFIG_DEV
gh secret set KUBE_CONFIG_DEV < kubeconfig-dev-base64.txt

# Verificar logs do workflow
gh run view --log
```

### Kubeconfig inválido

**Erro**: `Unable to connect to the server`

**Solução**:
```bash
# Verificar formato
echo $KUBE_CONFIG_DEV | base64 -d | kubectl --kubeconfig=/dev/stdin get pods

# Re-gerar kubeconfig
kubectl config view --flatten --minify > kubeconfig-new.yaml
cat kubeconfig-new.yaml | base64 -w 0
```

### Permissões insuficientes

**Erro**: `forbidden: User "system:serviceaccount:..." cannot get resource`

**Solução**:
```bash
# Verificar permissões
kubectl auth can-i get deployments --as=system:serviceaccount:netflix-mercados:github-actions

# Adicionar permissões necessárias
kubectl edit role github-actions-role -n netflix-mercados
```

---

## 📊 Checklist de Configuração

### Obrigatório para CI/CD Básico
- [ ] `KUBE_CONFIG_DEV` configurado
- [ ] `KUBE_CONFIG_STAGING` configurado
- [ ] `KUBE_CONFIG_PROD` configurado
- [ ] Testado deploy em dev
- [ ] Testado deploy em staging

### Recomendado
- [ ] `SLACK_WEBHOOK` para notificações
- [ ] `SNYK_TOKEN` para security scanning
- [ ] Service account com permissões mínimas
- [ ] Rotação de secrets agendada

### Opcional
- [ ] `CODECOV_TOKEN` para coverage tracking
- [ ] Secrets de staging/prod separados
- [ ] Backup dos secrets em vault seguro

---

## 📚 Recursos Adicionais

- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Kubernetes RBAC](https://kubernetes.io/docs/reference/access-authn-authz/rbac/)
- [Service Accounts](https://kubernetes.io/docs/tasks/configure-pod-container/configure-service-account/)
- [Slack Incoming Webhooks](https://api.slack.com/messaging/webhooks)

---

## 🎯 Próximos Passos

Após configurar os secrets:

1. ✅ Fazer primeiro push para testar CI/CD
2. ✅ Verificar notificações no Slack
3. ✅ Conferir security scans no GitHub Security tab
4. ✅ Validar deploy em dev/staging/prod

---

**🔒 IMPORTANTE**: Nunca commite secrets no Git! Use sempre GitHub Secrets ou vault seguro.

**Criado por**: Netflix Mercados DevOps Team  
**Última atualização**: 2024-02-03
