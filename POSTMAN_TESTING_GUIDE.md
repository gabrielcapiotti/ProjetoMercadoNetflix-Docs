# 🧪 Guia Prático - Testando API com Postman

Guia completo passo a passo para importar, configurar e testar todos os 18 endpoints da Netflix Mercados API usando Postman.

## 📋 Índice

- [Instalação do Postman](#instalação-do-postman)
- [Importação da Collection](#importação-da-collection)
- [Configuração Inicial](#configuração-inicial)
- [Workflow de Testes](#workflow-de-testes)
- [Testando Cada Módulo](#testando-cada-módulo)
- [Cenários de Teste](#cenários-de-teste)
- [Troubleshooting](#troubleshooting)

## 📥 Instalação do Postman

### Desktop App (Recomendado)

**Windows/Mac/Linux:**
1. Acesse https://www.postman.com/downloads/
2. Baixe a versão para seu sistema operacional
3. Instale e abra o Postman

### Web Version

Acesse: https://web.postman.co/

## 📦 Importação da Collection

### Passo 1: Importar Collection

1. **Abra o Postman**

2. **Clique em "Import"** (canto superior esquerdo)

3. **Selecione "Upload Files"**

4. **Navegue até o repositório** e selecione:
   ```
   Netflix-Mercados-API.postman_collection.json
   ```

5. **Clique em "Import"**

6. **Verifique**: A collection "Netflix Mercados API" deve aparecer no painel lateral

### Passo 2: Importar Environment

1. **Clique no ícone de engrenagem** (Manage Environments) no canto superior direito

2. **Clique em "Import"**

3. **Selecione o arquivo**:
   ```
   Netflix-Mercados-Environments.postman_environment.json
   ```

4. **Clique em "Import"**

5. **Ative o environment**:
   - Selecione "Netflix Mercados - Local" no dropdown de environments
   - Deve aparecer no canto superior direito

### Passo 3: Verificar Importação

✅ **Checklist**:
- [ ] Collection "Netflix Mercados API" visível
- [ ] Environment "Netflix Mercados - Local" selecionado
- [ ] 6 pastas na collection (Auth, Promoções, Recomendações, Relatórios, Tendências, Validação)
- [ ] Total de 19 requests (1 auth + 18 endpoints)

## ⚙️ Configuração Inicial

### Variáveis de Environment

Clique no **ícone de olho** 👁️ ao lado do environment para ver as variáveis:

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `baseUrl` | `http://localhost:8080` | URL base da API |
| `accessToken` | *(vazio)* | Token JWT (preenchido após login) |
| `authEmail` | `admin@netflix.com` | Email para login |
| `authPassword` | `admin123` | Senha para login |
| `mercadoId` | `1` | ID de mercado para testes |
| `codigoPromocao` | `PROMO2024` | Código de promoção |
| `limiteRecomendacoes` | `10` | Limite de recomendações |

### Ajustar para Seu Ambiente

Se sua API está rodando em outra porta ou host:

1. Clique em **Environments** → **Netflix Mercados - Local**
2. Edite `baseUrl` para seu valor (ex: `http://localhost:9090`)
3. Salve as alterações

## 🔐 Workflow de Testes

### Sequência Recomendada

```
1. Iniciar API local
   ↓
2. Fazer Login (obter token)
   ↓
3. Testar Validações (sem auth)
   ↓
4. Testar Promoções (com auth)
   ↓
5. Testar Recomendações
   ↓
6. Testar Relatórios
   ↓
7. Testar Tendências
```

### Passo a Passo Completo

#### 1️⃣ Iniciar a API Localmente

**Com Docker Compose:**
```bash
cd /workspaces/ProjetoMercadoNetflix-Docs
docker-compose up -d
docker-compose logs -f api
```

**Aguarde até ver**: `Started NetflixMercadosApplication`

**Verifique health:**
```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

#### 2️⃣ Fazer Login (Obter Token)

1. **Abra a pasta "Auth"** na collection

2. **Clique em "Login"**

3. **Verifique o Body**:
   ```json
   {
     "email": "{{authEmail}}",
     "password": "{{authPassword}}"
   }
   ```

4. **Clique em "Send"** 🚀

5. **Resposta Esperada** (200 OK):
   ```json
   {
     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "tokenType": "Bearer",
     "expiresIn": 86400000
   }
   ```

6. **Token Salvo Automaticamente**: 
   - O script da collection salva automaticamente em `{{accessToken}}`
   - Verifique: 👁️ → Current Value de `accessToken`

✅ **Agora você está autenticado!** Todos os outros requests usarão este token.

## 📋 Testando Cada Módulo

### 🎁 Módulo 1: Aplicação de Promoções

#### Request 1: Aplicar Promoção

**Endpoint**: `POST /api/aplicacao-promocao/aplicar/{mercadoId}`

**Passos**:
1. Abra "Promoções" → "Aplicar Promoção"
2. Verifique URL: `{{baseUrl}}/api/aplicacao-promocao/aplicar/{{mercadoId}}`
3. Body:
   ```json
   {
     "codigoPromocao": "{{codigoPromocao}}",
     "produtos": [1, 2, 3]
   }
   ```
4. **Send** 🚀

**Resposta Esperada** (200 OK):
```json
{
  "sucesso": true,
  "mensagem": "Promoção PROMO2024 aplicada com sucesso",
  "promocaoId": 123,
  "mercadoId": 1,
  "produtosAfetados": 3,
  "descontoTotal": 150.50
}
```

**Teste de Erro (400)**:
- Altere `codigoPromocao` para `"INVALIDO"`
- Send → Deve retornar erro

#### Request 2: Reverter Promoção

**Endpoint**: `POST /api/aplicacao-promocao/reverter/{mercadoId}`

**Body**:
```json
{
  "promocaoId": 123
}
```

**Resposta Esperada**:
```json
{
  "sucesso": true,
  "mensagem": "Promoção revertida com sucesso",
  "promocaoId": 123
}
```

---

### 💡 Módulo 2: Recomendações

#### Request 3: Recomendações por Mercado

**Endpoint**: `GET /api/recomendacoes/mercado/{mercadoId}`

**Query Params**:
- `limite`: `{{limiteRecomendacoes}}`

**Resposta Esperada**:
```json
{
  "mercadoId": 1,
  "recomendacoes": [
    {
      "produtoId": 42,
      "nome": "Produto A",
      "score": 0.95,
      "motivo": "Baseado em compras similares"
    },
    // ... mais 9 recomendações
  ],
  "total": 10
}
```

#### Request 4: Recomendações por Usuário

**Endpoint**: `GET /api/recomendacoes/usuario/{usuarioId}`

**Altere a variável**:
1. Duplicar request (botão direito → Duplicate)
2. Na URL, trocar `{mercadoId}` por `1` (ID do usuário)
3. Send

#### Request 5: Recomendações Populares

**Endpoint**: `GET /api/recomendacoes/populares`

**Query Params**:
- `limite`: `10`

**Resposta**:
```json
{
  "recomendacoes": [
    {
      "produtoId": 15,
      "nome": "Produto Popular",
      "vendas": 1500,
      "rating": 4.8
    }
  ]
}
```

---

### 📊 Módulo 3: Relatórios

Todos os relatórios seguem o mesmo padrão:

**Query Params Comuns**:
- `dataInicio`: `2024-01-01`
- `dataFim`: `2024-12-31`

#### Request 6-10: Todos os Relatórios

**Teste cada um**:
1. ✅ Relatório de Vendas
2. ✅ Relatório de Estoque
3. ✅ Relatório de Desempenho
4. ✅ Relatório de Clientes
5. ✅ Relatório Financeiro

**Exemplo - Vendas**:
```json
{
  "mercadoId": 1,
  "periodo": {
    "inicio": "2024-01-01",
    "fim": "2024-12-31"
  },
  "vendas": {
    "total": 150000.00,
    "quantidade": 1200,
    "ticketMedio": 125.00
  },
  "topProdutos": [
    {
      "produtoId": 5,
      "nome": "Produto X",
      "vendido": 500
    }
  ]
}
```

---

### 📈 Módulo 4: Tendências

#### Request 11-15: Tendências

**Nenhum requer autenticação** (endpoints públicos)

1. **Produtos em Alta**:
   - Limite: `{{limiteRanking}}`
   - Retorna produtos mais vendidos

2. **Mercados em Destaque**:
   - Retorna mercados com melhor desempenho

3. **Categorias em Crescimento**:
   - Retorna categorias com mais vendas

4. **Ranking de Vendedores**:
   - Top vendedores por receita

5. **Previsão de Demanda**:
   - Query params: `categoria`, `periodo`
   - Retorna previsão de vendas

---

### ✅ Módulo 5: Validação

**Endpoints públicos** (sem necessidade de token)

#### Request 16: Validar Email

**Body**:
```json
{
  "email": "{{emailValidacao}}"
}
```

**Resposta**:
```json
{
  "valido": true,
  "email": "teste@example.com"
}
```

**Teste inválido**:
```json
{
  "email": "email-invalido"
}
```

Resposta:
```json
{
  "valido": false,
  "erro": "Formato de email inválido"
}
```

#### Request 17: Validar URL

**Body**:
```json
{
  "url": "{{urlValidacao}}"
}
```

#### Request 18: Validar Texto Ofensivo

**Body**:
```json
{
  "texto": "{{textoValidacao}}"
}
```

**Resposta**:
```json
{
  "ofensivo": false,
  "texto": "Texto limpo"
}
```

## 🎯 Cenários de Teste

### Cenário 1: Fluxo Completo de Promoção

```
1. Login
2. Aplicar promoção no mercado 1
3. Verificar recomendações do mercado
4. Gerar relatório de vendas
5. Reverter promoção
```

### Cenário 2: Análise de Tendências

```
1. Consultar produtos em alta
2. Ver mercados em destaque
3. Verificar categorias em crescimento
4. Consultar ranking de vendedores
5. Obter previsão de demanda
```

### Cenário 3: Validações em Batch

```
1. Validar 5 emails diferentes
2. Validar 5 URLs diferentes
3. Validar textos com palavras ofensivas
```

## 🔧 Configurações Avançadas

### Criar Novo Environment (Staging)

1. Duplicate "Netflix Mercados - Local"
2. Renomeie para "Netflix Mercados - Staging"
3. Altere `baseUrl` para `https://api-staging.netflix-mercados.com`
4. Salve

### Automatizar Login

**Pre-request Script** (na pasta Auth):
```javascript
// Verificar se token expirou
const tokenExpiration = pm.environment.get("tokenExpiration");
const now = new Date().getTime();

if (!tokenExpiration || now > tokenExpiration) {
    // Token expirado, fazer novo login
    pm.sendRequest({
        url: pm.environment.get("baseUrl") + "/auth/login",
        method: 'POST',
        header: {
            'Content-Type': 'application/json',
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                email: pm.environment.get("authEmail"),
                password: pm.environment.get("authPassword")
            })
        }
    }, function (err, res) {
        if (!err) {
            const token = res.json().accessToken;
            const expiresIn = res.json().expiresIn;
            pm.environment.set("accessToken", token);
            pm.environment.set("tokenExpiration", now + expiresIn);
        }
    });
}
```

### Testes Automatizados

**Test Script** (exemplo para Aplicar Promoção):
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has success field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.sucesso).to.eql(true);
});

pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

// Salvar promocaoId para próximos requests
var jsonData = pm.response.json();
pm.environment.set("promocaoId", jsonData.promocaoId);
```

## 🐛 Troubleshooting

### Erro: "Could not get response"

**Causa**: API não está rodando

**Solução**:
```bash
docker-compose ps
docker-compose up -d
curl http://localhost:8080/actuator/health
```

### Erro 401 Unauthorized

**Causa**: Token inválido ou expirado

**Solução**:
1. Execute novamente "Auth → Login"
2. Verifique se `accessToken` foi salvo no environment
3. Verifique header `Authorization: Bearer {{accessToken}}`

### Erro 404 Not Found

**Causa**: Endpoint incorreto ou API não tem rota

**Solução**:
1. Verifique URL: `{{baseUrl}}/api/...`
2. Verifique se `baseUrl` está correto
3. Veja logs da API: `docker-compose logs -f api`

### Token não está sendo salvo

**Causa**: Script de teste não executou

**Solução**:
1. Vá em "Auth → Login"
2. Aba "Tests"
3. Verifique se tem o script:
   ```javascript
   var jsonData = pm.response.json();
   pm.environment.set("accessToken", jsonData.accessToken);
   ```
4. Re-execute o login

### Response muito lento

**Causa**: Banco de dados não está otimizado ou muitos dados

**Solução**:
```bash
# Ver uso de recursos
docker stats

# Reiniciar containers
docker-compose restart
```

## 📊 Collection Runner

### Executar Todos os Testes

1. **Clique no botão "Runner"** (canto superior esquerdo)

2. **Selecione a collection**: "Netflix Mercados API"

3. **Selecione o environment**: "Netflix Mercados - Local"

4. **Configure**:
   - Iterations: `1`
   - Delay: `500ms` (para não sobrecarregar)

5. **Ordem de execução**:
   - ✅ Marque "Auth → Login" primeiro
   - ✅ Marque todos os outros requests

6. **Run Netflix Mercados API** 🚀

7. **Aguarde**: Todos os 18 endpoints serão testados

8. **Resultado**:
   - ✅ Passed: X/18
   - ❌ Failed: Y/18
   - Total time: ~30s

## 📈 Métricas e Reports

### Exportar Resultados

1. Após Runner, clique em **"Export Results"**
2. Formato: JSON
3. Salve para análise

### Visualizar no Newman (CLI)

```bash
# Instalar Newman
npm install -g newman

# Executar collection
newman run Netflix-Mercados-API.postman_collection.json \
  -e Netflix-Mercados-Environments.postman_environment.json

# Com relatório HTML
newman run Netflix-Mercados-API.postman_collection.json \
  -e Netflix-Mercados-Environments.postman_environment.json \
  -r html --reporter-html-export report.html
```

## ✅ Checklist de Testes

### Funcionalidades Básicas
- [ ] Login retorna token válido
- [ ] Token é salvo no environment
- [ ] Endpoints com auth funcionam
- [ ] Endpoints públicos funcionam sem auth

### Promoções
- [ ] Aplicar promoção com sucesso
- [ ] Reverter promoção
- [ ] Erro com código inválido

### Recomendações
- [ ] Recomendações por mercado (10 itens)
- [ ] Recomendações por usuário
- [ ] Recomendações populares

### Relatórios
- [ ] Relatório de vendas
- [ ] Relatório de estoque
- [ ] Relatório de desempenho
- [ ] Relatório de clientes
- [ ] Relatório financeiro

### Tendências
- [ ] Produtos em alta
- [ ] Mercados em destaque
- [ ] Categorias em crescimento
- [ ] Ranking de vendedores
- [ ] Previsão de demanda

### Validação
- [ ] Validar email válido
- [ ] Validar email inválido
- [ ] Validar URL válida
- [ ] Validar texto ofensivo

## 🎯 Próximos Passos

Após testar todos os endpoints:

1. ✅ Configurar testes automatizados no CI/CD
2. ✅ Criar collection de testes de carga (JMeter/Gatling)
3. ✅ Documentar casos de uso específicos
4. ✅ Integrar com monitoring (Datadog/New Relic)

---

**🎉 Parabéns!** Você testou com sucesso todos os 18 endpoints da Netflix Mercados API!

**Documentação**: 
- [POSTMAN_SETUP.md](POSTMAN_SETUP.md) - Setup inicial
- [SWAGGER_API_REFERENCE_FASE3.md](SWAGGER_API_REFERENCE_FASE3.md) - Referência completa da API

**Criado por**: Netflix Mercados DevOps Team  
**Última atualização**: 2024-02-03
