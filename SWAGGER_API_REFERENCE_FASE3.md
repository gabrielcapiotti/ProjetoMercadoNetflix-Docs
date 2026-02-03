# 📘 Swagger/OpenAPI - Referência dos Novos Endpoints (Fase 3)

**Data:** 03 de Fevereiro de 2026  
**Escopo:** Promoções, Recomendações, Relatórios, Tendências e Validação  
**Base URL:** `http://localhost:8080`  
**Prefixo da API:** `/api/v1`

---

## 🔐 Autenticação

Todos os endpoints abaixo exigem **JWT Bearer Token**:

```
Authorization: Bearer <accessToken>
```

**Fluxo rápido:**
1. `POST /api/v1/auth/login`
2. Copie o `accessToken`
3. Use no Swagger UI (botão **Authorize**)

---

## 🧩 Tags Swagger

- **Aplicação de Promoções**
- **Recomendações**
- **Relatórios**
- **Tendências**
- **Validação de Dados**

---

# 1) Aplicação de Promoções

## ✅ POST /api/v1/promocoes/aplicacao/aplicar
**Resumo:** Aplica uma promoção e calcula o desconto.  
**Roles:** USER, CUSTOMER  

**Request (body):**
```json
{
  "codigoPromocao": "PROMO10",
  "valorCompra": 199.90
}
```

**Response 200 (sucesso):**
```json
{
  "promocaoId": 12,
  "codigoPromocao": "PROMO10",
  "valorOriginal": 199.90,
  "desconto": 20.00,
  "percentualDesconto": 10.0,
  "valorFinal": 179.90,
  "economia": 10.0,
  "dataExpiracao": "2026-02-28T23:59:59",
  "utilizacaoRestante": 154
}
```

**Erros possíveis:**
- 400 (dados inválidos ou promoção expirada)
- 404 (promoção não encontrada)
- 409 (limite de utilização atingido)
- 401 (não autenticado)

---

## ✅ POST /api/v1/promocoes/aplicacao/validar/{codigoPromocao}
**Resumo:** Verifica se a promoção é válida.  
**Roles:** USER, CUSTOMER  

**Path Param:**
- `codigoPromocao`: código da promoção

**Response 200 (válida):**
```json
{
  "valida": true,
  "mensagem": "Promoção válida"
}
```

**Response 200 (inválida):**
```json
{
  "valida": false,
  "mensagem": "Promoção inválida, expirada ou com limite atingido"
}
```

**Erros possíveis:**
- 401 (não autenticado)
- 500 (erro interno)

---

# 2) Recomendações

## ✅ GET /api/v1/recomendacoes/personalizadas?limite=10
**Resumo:** Recomendações personalizadas para o usuário.  
**Roles:** USER, CUSTOMER  

**Query Param:**
- `limite` (opcional, padrão 10)

**Response 200 (lista):**
```json
[
  {
    "mercado": {
      "id": 1,
      "nome": "Mercado Central",
      "cidade": "São Paulo",
      "estado": "SP",
      "avaliacaoMedia": 4.6,
      "totalAvaliacoes": 128
    },
    "pontuacao": 92.5,
    "motivo": "Similar aos seus favoritos"
  }
]
```

---

## ✅ GET /api/v1/recomendacoes/por-localizacao?limite=10
**Resumo:** Recomendações por localização.  
**Roles:** USER, CUSTOMER  

**Query Param:**
- `limite` (opcional, padrão 10)

**Response 200:** (mesma estrutura do endpoint anterior)

---

## ✅ GET /api/v1/recomendacoes/nao-visitados?limite=10
**Resumo:** Mercados bem avaliados ainda não visitados.  
**Roles:** USER, CUSTOMER  

**Query Param:**
- `limite` (opcional, padrão 10)

**Response 200:** (mesma estrutura do endpoint anterior)

---

# 3) Relatórios

## ✅ GET /api/v1/relatorios/geral
**Resumo:** Estatísticas gerais do sistema.  
**Roles:** ADMIN, SELLER  

**Response 200:**
```json
{
  "dataGeracao": "2026-02-03T14:30:00",
  "totalMercados": 240,
  "totalAvaliacoes": 19234,
  "totalComentarios": 6541,
  "totalPromocoes": 312,
  "mediaAvaliacoes": 4.2,
  "mercadoMelhorAvaliado": "Mercado Central",
  "avaliacaoMelhorMercado": 4.9,
  "mercadoMaisAvaliado": "Mercado Zona Sul",
  "totalAvaliacoesMercadoMaisAvaliado": 1450
}
```

---

## ✅ GET /api/v1/relatorios/mercado/{mercadoId}
**Resumo:** Relatório de performance de um mercado.  
**Roles:** ADMIN, SELLER  

**Response 200:**
```json
{
  "mercadoId": 10,
  "nomeMercado": "Mercado Central",
  "dataGeracao": "2026-02-03T14:30:00",
  "avaliacaoMedia": 4.7,
  "totalAvaliacoes": 520,
  "totalComentarios": 210,
  "totalPromocoesAtivas": 6,
  "distribuicaoEstrelas": {
    "5": 320,
    "4": 140,
    "3": 40,
    "2": 15,
    "1": 5
  },
  "ativo": true
}
```

**Erros possíveis:**
- 404 (mercado não encontrado)

---

## ✅ GET /api/v1/relatorios/ranking?limite=20
**Resumo:** Ranking por avaliação média.  
**Roles:** ADMIN, SELLER, USER  

**Query Param:**
- `limite` (opcional, padrão 20)

**Response 200:**
```json
[
  {
    "posicao": 1,
    "nome": "Mercado Central",
    "cidade": "São Paulo",
    "estado": "SP",
    "avaliacaoMedia": 4.9,
    "totalAvaliacoes": 320
  }
]
```

---

## ✅ GET /api/v1/relatorios/poucas-avaliacoes?avaliacaoMinima=10
**Resumo:** Mercados com poucas avaliações.  
**Roles:** ADMIN, SELLER  

**Query Param:**
- `avaliacaoMinima` (opcional, padrão 10)

**Response 200:**
```json
[
  {
    "mercadoId": 20,
    "nome": "Mercado Bairro",
    "cidade": "Campinas",
    "estado": "SP",
    "totalAvaliacoes": 7,
    "avaliacaoMedia": 3.9
  }
]
```

---

## ✅ GET /api/v1/relatorios/comentarios
**Resumo:** Qualidade e moderação de comentários.  
**Roles:** ADMIN, SELLER  

**Response 200:**
```json
{
  "dataGeracao": "2026-02-03T14:30:00",
  "totalComentarios": 6541,
  "comentariosAtivos": 6120,
  "comentariosInativos": 221,
  "comentariosAguardandoModeração": 200,
  "percentualAtivos": 93.6,
  "mediaCurtidas": 8.2,
  "comentarioMaisCurtido": "Ótimo atendimento e preços!"
}
```

---

# 4) Tendências

## ✅ GET /api/v1/tendencias/geral
**Resumo:** Análise geral de tendências.  
**Roles:** ADMIN, SELLER  

**Response 200:**
```json
{
  "dataAnalise": "2026-02-03T14:30:00",
  "crescimentoMedio": 24.8,
  "mercadosEmAlta": 42,
  "totalMercados": 240,
  "topCrescimento": [
    {
      "mercadoId": 7,
      "nomeMercado": "Mercado Premium",
      "cidade": "São Paulo",
      "estado": "SP",
      "avaliacaoMedia": 4.8,
      "totalAvaliacoes": 110,
      "crescimento": 38.2,
      "tendencia": "ALTA"
    }
  ],
  "topDeclinio": [
    {
      "mercadoId": 15,
      "nomeMercado": "Mercado Bairro",
      "cidade": "Campinas",
      "estado": "SP",
      "avaliacaoMedia": 3.7,
      "totalAvaliacoes": 52,
      "crescimento": 3.1,
      "tendencia": "BAIXA"
    }
  ]
}
```

---

## ✅ GET /api/v1/tendencias/emergentes?limite=10
**Resumo:** Mercados com potencial de crescimento.  
**Roles:** ADMIN, SELLER

## ✅ GET /api/v1/tendencias/consolidados?limite=10
**Resumo:** Mercados consolidados.  
**Roles:** ADMIN, SELLER

## ✅ GET /api/v1/tendencias/melhor-performance?limite=15
**Resumo:** Ranking por performance.  
**Roles:** ADMIN, SELLER

**Response 200 (estrutura):**
```json
[
  {
    "mercadoId": 7,
    "nomeMercado": "Mercado Premium",
    "cidade": "São Paulo",
    "estado": "SP",
    "avaliacaoMedia": 4.8,
    "totalAvaliacoes": 110,
    "crescimento": 38.2,
    "tendencia": "ALTA"
  }
]
```

---

## ✅ GET /api/v1/tendencias/crescimento-medio
**Resumo:** Crescimento médio global.  
**Roles:** ADMIN, SELLER

**Response 200:**
```json
{
  "crescimentoMedio": 24.8
}
```

---

# 5) Validação de Dados

## ✅ POST /api/v1/validacao/email?email=usuario@email.com
**Resumo:** Valida email (RFC 5322).  
**Roles:** ADMIN, SELLER, USER

**Response 200:**
```json
{
  "valido": true,
  "mensagem": "Email válido"
}
```

---

## ✅ POST /api/v1/validacao/url?url=https://exemplo.com
**Resumo:** Valida URL.  
**Roles:** ADMIN, SELLER

**Response 200:**
```json
{
  "valido": true,
  "mensagem": "URL válida"
}
```

---

## ✅ POST /api/v1/validacao/sanitizar?texto=olá<script>
**Resumo:** Sanitiza texto (anti-XSS).  
**Roles:** ADMIN, SELLER, USER

**Response 200:**
```json
{
  "original": "olá<script>",
  "sanitizado": "oláscript",
  "foiAlterado": true
}
```

---

## ✅ Códigos de Resposta (resumo)

- **200** OK (resposta normal)
- **400** Bad Request (dados inválidos)
- **401** Unauthorized (token ausente/inválido)
- **403** Forbidden (role sem permissão)
- **404** Not Found (recurso inexistente)
- **409** Conflict (limite de uso)
- **500** Internal Server Error

---

## ✅ Observações

- Todos os endpoints acima estão anotados com `@Operation`, `@ApiResponses` e `@SecurityRequirement`.
- As respostas seguem os DTOs em `src/main/java/com/netflix/mercado/dto/**`.
- A documentação completa do Swagger UI permanece acessível em `/swagger-ui.html`.
