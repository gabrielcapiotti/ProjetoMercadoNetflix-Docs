# MATRIZ VISUAL DE ERROS - REFERÊNCIA RÁPIDA

## 1. TABELA CONSOLIDADA - TODOS OS ERROS

| # | Arquivo | Linha | Método | Chamada Incorreta | Chamada Correta | Tipo de Erro | Prioridade |
|---|---------|-------|--------|------------------|-----------------|--------------|-----------|
| 1 | AuditLogService.java | ~5-15 | N/A | Sem import TipoAcao | `import com.netflix.mercado.entity.AuditLog.TipoAcao;` | Import faltante | CRÍTICO |
| 2 | AuditLogService.java | 104 | obterAuditoriaDoUsuario | `findByUsuarioIdOrderByDataHoraDesc()` | `findByUser()` | Método não existe | CRÍTICO |
| 3 | AuditLogService.java | 125 | obterAuditoriaEntidade | `findByTipoEntidadeAndIdEntidadeOrderByDataHoraDesc()` | `findHistoricoEntidade()` | Método não existe | CRÍTICO |
| 4 | AuditLogService.java | 151 | obterAuditoriaEntreData | `findByDataHoraBetweenOrderByDataHoraDesc()` | `findByDataRange()` | Método não existe | CRÍTICO |
| 5 | AuditLogService.java | 169 | obterPorTipoAcao | `findByTipoAcaoOrderByDataHoraDesc()` | `findByAcao()` | Método não existe | CRÍTICO |
| 6 | AuditLogService.java | 187 | obterPorTipoEntidade | `findByTipoEntidadeOrderByDataHoraDesc()` | `findByTipoEntidade()` | Método não existe | CRÍTICO |
| 7 | AuditLogService.java | 204 | contarAcoesDoUsuario | `countByUsuarioId()` | `countByUser()` | Método não existe | CRÍTICO |
| 8 | AuditLogService.java | 221 | contarAcoes | `countByTipoAcao()` | `findByAcao() + getTotalElements()` | Método não existe | CRÍTICO |
| 9 | AuditLogService.java | 242 | obterAtividadeSuspeita | `findByUsuarioIdAndDataHoraAfterOrderByDataHoraDesc()` | `findByDataRange()` | Método não existe | CRÍTICO |
| 10 | FavoritoService.java | 56 | adicionarFavorito | `existsByMercadoIdAndUsuarioId()` | `existsByUserAndMercado()` | Método não existe | CRÍTICO |
| 11 | FavoritoService.java | 93 | removerFavorito | `findByMercadoIdAndUsuarioId()` | `findByUserAndMercado()` | Método não existe | CRÍTICO |
| 12 | FavoritoService.java | 122 | obterFavoritosDUsuario | `findByUsuarioId()` | `findByUser()` | Método não existe | CRÍTICO |
| 13 | FavoritoService.java | 138 | verificarFavorito | `existsByMercadoIdAndUsuarioId()` | `existsByUserAndMercado()` | Método não existe | CRÍTICO |
| 14 | FavoritoService.java | 150 | contarFavoritosDoUsuario | `countByUsuarioId()` | `countByUser()` | Método não existe | CRÍTICO |
| 15 | FavoritoService.java | 195 | obterFavoritosComPrioridade | `findByUsuarioIdOrderByPrioridadeDescDataAdicaoDesc()` | `findByUser()` + Sort | Nome campo errado | CRÍTICO |
| 16 | AvaliacaoService.java | 236 | obterAvaliacoesPorUsuario | `findByUsuarioId()` | `findByUser()` | Método não existe | CRÍTICO |
| 17 | AvaliacaoService.java | 311 | validarDuplicata | `existsByMercadoIdAndUsuarioId()` | `findByMercadoAndUser()` | Método não existe | CRÍTICO |
| 18 | NotificacaoService.java | 112 | obterNotificacoesDoUsuario | `findByUsuarioIdOrderByDataEnvioDesc()` | `findByUser()` | Método/Campo errado | CRÍTICO |
| 19 | NotificacaoService.java | 200 | contarNaoLidas | `countByUsuarioIdAndLidaFalse()` | `countUnreadByUser()` | Método não existe | CRÍTICO |

**Total de Erros:** 19
**Prioridade:** Todos CRÍTICOS

---

## 2. ERROS POR ARQUIVO

### 🔴 AuditLogService.java (9 erros)

```
ARQUIVO: src/main/java/com/netflix/mercado/service/AuditLogService.java

┌─────────────────────────────────────────────────────────────────┐
│ LINHA  │ METODO                        │ ERRO                    │
├─────────────────────────────────────────────────────────────────┤
│ ~5-15  │ (import)                      │ Falta import TipoAcao   │
│ 104    │ obterAuditoriaDoUsuario       │ Método não existe       │
│ 125    │ obterAuditoriaEntidade        │ Método não existe       │
│ 151    │ obterAuditoriaEntreData       │ Método não existe       │
│ 169    │ obterPorTipoAcao              │ Método não existe       │
│ 187    │ obterPorTipoEntidade          │ Método não existe       │
│ 204    │ contarAcoesDoUsuario          │ Método não existe       │
│ 221    │ contarAcoes                   │ Método não existe       │
│ 242    │ obterAtividadeSuspeita        │ Método não existe       │
└─────────────────────────────────────────────────────────────────┘
```

### 🔴 FavoritoService.java (6 erros)

```
ARQUIVO: src/main/java/com/netflix/mercado/service/FavoritoService.java

┌─────────────────────────────────────────────────────────────────┐
│ LINHA  │ METODO                        │ ERRO                    │
├─────────────────────────────────────────────────────────────────┤
│ 56     │ adicionarFavorito             │ Método não existe       │
│ 93     │ removerFavorito               │ Método não existe       │
│ 122    │ obterFavoritosDUsuario        │ Método não existe       │
│ 138    │ verificarFavorito             │ Método não existe       │
│ 150    │ contarFavoritosDoUsuario      │ Método não existe       │
│ 195    │ obterFavoritosComPrioridade   │ Campo/Método errado     │
└─────────────────────────────────────────────────────────────────┘
```

### 🔴 AvaliacaoService.java (2 erros)

```
ARQUIVO: src/main/java/com/netflix/mercado/service/AvaliacaoService.java

┌─────────────────────────────────────────────────────────────────┐
│ LINHA  │ METODO                        │ ERRO                    │
├─────────────────────────────────────────────────────────────────┤
│ 236    │ obterAvaliacoesPorUsuario     │ Método não existe       │
│ 311    │ validarDuplicata              │ Método não existe       │
└─────────────────────────────────────────────────────────────────┘
```

### 🔴 NotificacaoService.java (2 erros)

```
ARQUIVO: src/main/java/com/netflix/mercado/service/NotificacaoService.java

┌─────────────────────────────────────────────────────────────────┐
│ LINHA  │ METODO                        │ ERRO                    │
├─────────────────────────────────────────────────────────────────┤
│ 112    │ obterNotificacoesDoUsuario    │ Método/Campo errado     │
│ 200    │ contarNaoLidas                │ Método não existe       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. CATEGORIAS DE ERROS

### Categoria 1: Métodos com "usuarioId" que devem usar "User" (7 erros)
```
findByUsuarioId() → findByUser()
countByUsuarioId() → countByUser()
existsByMercadoIdAndUsuarioId() → existsByUserAndMercado()
findByMercadoIdAndUsuarioId() → findByUserAndMercado()
findByUsuarioIdOrderBy*() → findByUser()
```

**Arquivos afetados:**
- AuditLogService.java (linhas 104, 204)
- FavoritoService.java (linhas 56, 93, 122, 138, 150)
- AvaliacaoService.java (linhas 236, 311)
- NotificacaoService.java (linha 112)

---

### Categoria 2: Métodos com "DataHora" que devem usar "createdAt" (7 erros)
```
OrderByDataHoraDesc → OrderByCreatedAtDesc (ou uso de Sort API)
findByDataHoraBetweenOrderByDataHoraDesc() → findByDataRange()
findByDataEnvioDesc → OrderByCreatedAtDesc
findByDataAdicaoDesc → OrderByCreatedAtDesc
```

**Arquivos afetados:**
- AuditLogService.java (linhas 104, 125, 151, 169, 187, 242)
- FavoritoService.java (linha 195)
- NotificacaoService.java (linha 112)

---

### Categoria 3: Enums não importados (1 erro)
```
TipoAcao → import AuditLog.TipoAcao
```

**Arquivos afetados:**
- AuditLogService.java (linha 57)

---

### Categoria 4: Métodos de Count que não existem (3 erros)
```
countByTipoAcao() → findByAcao().getTotalElements()
countByUsuarioIdAndLidaFalse() → countUnreadByUser()
countByUsuarioIdAndRating() → findByMercadoAndEstrelas()
```

**Arquivos afetados:**
- AuditLogService.java (linha 221)
- NotificacaoService.java (linha 200)

---

## 4. PADRÃO DE CORREÇÃO POR TIPO

### Padrão 1: usuarioId → User object

```java
// ANTES
método(Long usuarioId) {
    repository.findByUsuarioId(usuarioId)
}

// DEPOIS
método(User usuario) {
    repository.findByUser(usuario)
}
```

**Aplicar em:**
- AuditLogService: 2 métodos
- FavoritoService: 3 métodos
- AvaliacaoService: 1 método
- NotificacaoService: 1 método

---

### Padrão 2: dataHora → createdAt

```java
// ANTES
OrderByDataHoraDesc
findByDataHoraBetweenOrderByDataHoraDesc()

// DEPOIS
OrderByCreatedAtDesc (com Sort)
findByDataRange()
```

**Aplicar em:**
- AuditLogService: 6 métodos
- FavoritoService: 1 método
- NotificacaoService: 1 método

---

### Padrão 3: Método Count não existe

```java
// ANTES
long count = repository.countByTipoAcao(tipoAcao)

// DEPOIS
long count = repository.findByAcao(acao, Pageable.unpaged()).getTotalElements()
```

**Aplicar em:**
- AuditLogService: 1 método
- NotificacaoService: 1 método

---

## 5. MAPA DE DEPENDÊNCIAS DE CORREÇÃO

```
┌─────────────────────────────────────┐
│  FIX SEQUÊNCIA RECOMENDADA          │
├─────────────────────────────────────┤
│                                     │
│  1. AuditLogService.java            │ (9 erros)
│     ↓ Adicionar import TipoAcao    │
│     ↓ Refatorar 8 métodos          │
│                                     │
│  2. FavoritoService.java            │ (6 erros)
│     ↓ Refatorar 6 métodos          │
│                                     │
│  3. AvaliacaoService.java           │ (2 erros)
│     ↓ Refatorar 2 métodos          │
│                                     │
│  4. NotificacaoService.java         │ (2 erros)
│     ↓ Refatorar 2 métodos          │
│                                     │
│  5. Adicionar UserRepository injeção│ (se necessário)
│                                     │
│  6. Atualizar testes unitários      │
│                                     │
│  7. Executar compilação             │
│     ↓ Deve passar SEM ERROS         │
│                                     │
│  8. Executar testes                 │
│     ↓ Todos devem passar            │
│                                     │
└─────────────────────────────────────┘
```

---

## 6. CHECKLIST VISUAL

### ✅ ANTES DE INICIAR
- [ ] Ler [ANALISE_COMPLETA_ERROS_COMPILACAO.md](ANALISE_COMPLETA_ERROS_COMPILACAO.md)
- [ ] Ler [GUIA_PASSO_A_PASSO_CORRECOES.md](GUIA_PASSO_A_PASSO_CORRECOES.md)
- [ ] Backup do código atual

### ✅ FASE 1: AuditLogService.java
- [ ] Adicionar import TipoAcao (linha ~5-15)
- [ ] Corrigir `obterAuditoriaDoUsuario` (linha 104)
- [ ] Corrigir `obterAuditoriaEntidade` (linha 125)
- [ ] Corrigir `obterAuditoriaEntreData` (linha 151)
- [ ] Corrigir `obterPorTipoAcao` (linha 169)
- [ ] Corrigir `obterPorTipoEntidade` (linha 187)
- [ ] Corrigir `contarAcoesDoUsuario` (linha 204)
- [ ] Corrigir `contarAcoes` (linha 221)
- [ ] Corrigir `obterAtividadeSuspeita` (linha 242)

### ✅ FASE 2: FavoritoService.java
- [ ] Corrigir `adicionarFavorito` (linha 56)
- [ ] Corrigir `removerFavorito` (linha 93)
- [ ] Corrigir `obterFavoritosDUsuario` (linha 122)
- [ ] Corrigir `verificarFavorito` (linha 138)
- [ ] Corrigir `contarFavoritosDoUsuario` (linha 150)
- [ ] Corrigir `obterFavoritosComPrioridade` (linha 195)

### ✅ FASE 3: AvaliacaoService.java
- [ ] Corrigir `obterAvaliacoesPorUsuario` (linha 236)
- [ ] Corrigir `validarDuplicata` (linha 311)

### ✅ FASE 4: NotificacaoService.java
- [ ] Corrigir `obterNotificacoesDoUsuario` (linha 112)
- [ ] Corrigir `contarNaoLidas` (linha 200)

### ✅ FASE 5: Validação
- [ ] Compilação sem erros
- [ ] Testes unitários passando
- [ ] Nenhum warning relevante

---

## 7. RESUMO ESTATÍSTICO

```
┌───────────────────────────────────────────┐
│ ESTATÍSTICAS DE ERROS                     │
├───────────────────────────────────────────┤
│ Total de Arquivos Afetados     │    4    │
│ Total de Métodos Afetados      │   19    │
│ Total de Linhas a Corrigir     │   19    │
│ Tipo Mais Comum                │ usuarioId
│ Prioridade Todos               │ CRÍTICO │
├───────────────────────────────────────────┤
│ Esforço Estimado               │  30 min │
│ Complexidade                   │  Baixa  │
│ Risco                          │  Baixo  │
└───────────────────────────────────────────┘
```

