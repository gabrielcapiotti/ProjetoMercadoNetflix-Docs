# 📮 Postman - Coleção Netflix Mercados (Fase 3)

**Data:** 03 de Fevereiro de 2026  
**Status:** ✅ Coleção pronta para uso

---

## ✅ Arquivos Gerados

1. [Netflix-Mercados-API.postman_collection.json](Netflix-Mercados-API.postman_collection.json)
2. [Netflix-Mercados-Environments.postman_environment.json](Netflix-Mercados-Environments.postman_environment.json)

---

## 🚀 Como Importar

1. Abra o Postman
2. Clique em **Import**
3. Importe os dois arquivos acima
4. Selecione o environment **Netflix Mercados - Local**

---

## 🔐 Como Autenticar

### 1) Fazer login
- Pasta **Auth (opcional)** → **Login (obter accessToken)**
- Use as variáveis `authEmail` e `authPassword`
- Copie o `accessToken` da resposta

### 2) Definir o token
- Abra o environment **Netflix Mercados - Local**
- Cole o token em `accessToken`

### 3) Testar endpoints
Todos os endpoints já estão configurados com Bearer Token usando `{{accessToken}}`.

---

## 🧪 Endpoints Incluídos (18)

### Aplicação de Promoções (2)
- POST /api/v1/promocoes/aplicacao/aplicar
- POST /api/v1/promocoes/aplicacao/validar/{codigoPromocao}

### Recomendações (3)
- GET /api/v1/recomendacoes/personalizadas
- GET /api/v1/recomendacoes/por-localizacao
- GET /api/v1/recomendacoes/nao-visitados

### Relatórios (5)
- GET /api/v1/relatorios/geral
- GET /api/v1/relatorios/mercado/{mercadoId}
- GET /api/v1/relatorios/ranking
- GET /api/v1/relatorios/poucas-avaliacoes
- GET /api/v1/relatorios/comentarios

### Tendências (5)
- GET /api/v1/tendencias/geral
- GET /api/v1/tendencias/emergentes
- GET /api/v1/tendencias/consolidados
- GET /api/v1/tendencias/melhor-performance
- GET /api/v1/tendencias/crescimento-medio

### Validação de Dados (3)
- POST /api/v1/validacao/email
- POST /api/v1/validacao/url
- POST /api/v1/validacao/sanitizar

---

## ⚙️ Variáveis do Environment

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `baseUrl` | URL da API | http://localhost:8080 |
| `accessToken` | JWT do usuário | (cole o token) |
| `authEmail` | Email de login | usuario@example.com |
| `authPassword` | Senha de login | Senha@123 |
| `codigoPromocao` | Código da promoção | PROMO10 |
| `mercadoId` | ID do mercado | 1 |
| `limiteRecomendacoes` | Limite de recomendações | 10 |
| `limiteRanking` | Limite do ranking | 20 |
| `avaliacaoMinima` | Mínimo de avaliações | 10 |
| `limiteEmergentes` | Limite de emergentes | 10 |
| `limiteConsolidados` | Limite de consolidados | 10 |
| `limiteMelhorPerformance` | Limite performance | 15 |
| `emailValidacao` | Email para validar | usuario@email.com |
| `urlValidacao` | URL para validar | https://exemplo.com |
| `textoValidacao` | Texto para sanitizar | ola<script> |

---

## ✅ Dicas

- Ajuste `baseUrl` conforme o ambiente.
- Use o endpoint de login para obter um token válido.
- Todos os endpoints já usam `{{accessToken}}` automaticamente.

---

## 📌 Observação

A pasta **Auth (opcional)** não conta como endpoint da Fase 3, mas facilita o uso da coleção.
