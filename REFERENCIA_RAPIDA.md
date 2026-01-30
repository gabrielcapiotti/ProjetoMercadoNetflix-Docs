# REFERÊNCIA RÁPIDA - TABELAS DE CORREÇÃO

## 1. TABELA COMPLETA CONSOLIDADA

| # | Arquivo | Linha | Assinatura do Método | ANTES | DEPOIS | Notas |
|---|---------|-------|----------------------|-------|--------|-------|
| 1 | AuditLogService.java | ~5-15 | (imports) | Sem TipoAcao import | `import AuditLog.TipoAcao;` | CRÍTICO |
| 2 | AuditLogService.java | 104 | `obterAuditoriaDoUsuario(Long usuarioId, Pageable)` | `findByUsuarioIdOrderByDataHoraDesc()` | `findByUser(User, Pageable)` | Refatorar assinatura |
| 3 | AuditLogService.java | 125 | `obterAuditoriaEntidade(String tipoEntidade, Long id)` | `findByTipoEntidadeAndIdEntidadeOrderByDataHoraDesc()` | `findHistoricoEntidade()` | - |
| 4 | AuditLogService.java | 151 | `obterAuditoriaEntreData(LocalDateTime, LocalDateTime, Pageable)` | `findByDataHoraBetweenOrderByDataHoraDesc()` | `findByDataRange()` + PageImpl | Retorna List, não Page |
| 5 | AuditLogService.java | 169 | `obterPorTipoAcao(String tipoAcao, Pageable)` | `findByTipoAcaoOrderByDataHoraDesc()` | `findByAcao(TipoAcao, Pageable)` | Converter String→Enum |
| 6 | AuditLogService.java | 187 | `obterPorTipoEntidade(String tipoEntidade, Pageable)` | `findByTipoEntidadeOrderByDataHoraDesc()` | `findByTipoEntidade()` | - |
| 7 | AuditLogService.java | 204 | `contarAcoesDoUsuario(Long usuarioId)` | `countByUsuarioId()` | `countByUser(User)` | Refatorar assinatura |
| 8 | AuditLogService.java | 221 | `contarAcoes(String tipoAcao)` | `countByTipoAcao()` | `findByAcao().getTotalElements()` | Não existe count |
| 9 | AuditLogService.java | 242 | `obterAtividadeSuspeita(Long usuarioId, Integer, Integer)` | `findByUsuarioIdAndDataHoraAfterOrderByDataHoraDesc()` | `findByDataRange()` + filter | Refatorar assinatura |
| 10 | FavoritoService.java | 56 | `adicionarFavorito()` | `existsByMercadoIdAndUsuarioId()` | `existsByUserAndMercado(User, Mercado)` | - |
| 11 | FavoritoService.java | 93 | `removerFavorito()` | `findByMercadoIdAndUsuarioId()` | `findByUserAndMercado(User, Mercado)` | - |
| 12 | FavoritoService.java | 122 | `obterFavoritosDUsuario(Long usuarioId, Pageable)` | `findByUsuarioId()` | `findByUser(User, Pageable)` | Refatorar assinatura |
| 13 | FavoritoService.java | 138 | `verificarFavorito()` | `existsByMercadoIdAndUsuarioId()` | `existsByUserAndMercado(User, Mercado)` | - |
| 14 | FavoritoService.java | 150 | `contarFavoritosDoUsuario(Long usuarioId)` | `countByUsuarioId()` | `countByUser(User)` | Refatorar assinatura |
| 15 | FavoritoService.java | 195 | `obterFavoritosComPrioridade(Long usuarioId)` | `findByUsuarioIdOrderByPrioridadeDescDataAdicaoDesc()` | `findByUser(User, PageRequest com Sort)` | Campo: dataAdicao→createdAt |
| 16 | AvaliacaoService.java | 236 | `obterAvaliacoesPorUsuario(Long usuarioId, Pageable)` | `findByUsuarioId()` | `findByUser(User, Pageable)` | Refatorar assinatura |
| 17 | AvaliacaoService.java | 311 | `validarDuplicata(Long mercadoId, Long usuarioId)` | `existsByMercadoIdAndUsuarioId()` | `findByMercadoAndUser().isPresent()` | - |
| 18 | NotificacaoService.java | 112 | `obterNotificacoesDoUsuario(Long usuarioId, Pageable)` | `findByUsuarioIdOrderByDataEnvioDesc()` | `findByUser(User, Pageable)` | Campo: dataEnvio→createdAt |
| 19 | NotificacaoService.java | 200 | `contarNaoLidas(Long usuarioId)` | `countByUsuarioIdAndLidaFalse()` | `countUnreadByUser(User)` | Refatorar assinatura |

---

## 2. TABELA POR ARQUIVO

### AuditLogService.java (9 correções)

| Linha | Método | Tipo de Erro | Antes | Depois |
|-------|--------|--------------|-------|--------|
| ~5-15 | imports | Falta import | - | Adicionar `import AuditLog.TipoAcao;` |
| 104 | obterAuditoriaDoUsuario | Método + Assinatura | Long usuarioId | User usuario + findByUser() |
| 125 | obterAuditoriaEntidade | Método | findByTipoEntidadeAndIdEntidadeOrderByDataHoraDesc() | findHistoricoEntidade() |
| 151 | obterAuditoriaEntreData | Método + Retorno | findByDataHoraBetweenOrderByDataHoraDesc() | findByDataRange() + PageImpl |
| 169 | obterPorTipoAcao | Método + Tipo | findByTipoAcaoOrderByDataHoraDesc(String) | findByAcao(TipoAcao) |
| 187 | obterPorTipoEntidade | Método | findByTipoEntidadeOrderByDataHoraDesc() | findByTipoEntidade() |
| 204 | contarAcoesDoUsuario | Método + Assinatura | Long usuarioId | User usuario + countByUser() |
| 221 | contarAcoes | Método | countByTipoAcao() | findByAcao().getTotalElements() |
| 242 | obterAtividadeSuspeita | Método + Assinatura | Long usuarioId | User usuario + findByDataRange() |

### FavoritoService.java (6 correções)

| Linha | Método | Tipo de Erro | Antes | Depois |
|-------|--------|--------------|-------|--------|
| 56 | adicionarFavorito | Método | existsByMercadoIdAndUsuarioId() | existsByUserAndMercado() |
| 93 | removerFavorito | Método | findByMercadoIdAndUsuarioId() | findByUserAndMercado() |
| 122 | obterFavoritosDUsuario | Método + Assinatura | Long usuarioId | User usuario + findByUser() |
| 138 | verificarFavorito | Método | existsByMercadoIdAndUsuarioId() | existsByUserAndMercado() |
| 150 | contarFavoritosDoUsuario | Método + Assinatura | Long usuarioId | User usuario + countByUser() |
| 195 | obterFavoritosComPrioridade | Método + Campo | Long usuarioId + dataAdicao | User usuario + createdAt + Sort |

### AvaliacaoService.java (2 correções)

| Linha | Método | Tipo de Erro | Antes | Depois |
|-------|--------|--------------|-------|--------|
| 236 | obterAvaliacoesPorUsuario | Método + Assinatura | Long usuarioId | User usuario + findByUser() |
| 311 | validarDuplicata | Método | existsByMercadoIdAndUsuarioId() | findByMercadoAndUser().isPresent() |

### NotificacaoService.java (2 correções)

| Linha | Método | Tipo de Erro | Antes | Depois |
|-------|--------|--------------|-------|--------|
| 112 | obterNotificacoesDoUsuario | Método + Campo | Long usuarioId + dataEnvio | User usuario + createdAt + findByUser() |
| 200 | contarNaoLidas | Método + Assinatura | Long usuarioId | User usuario + countUnreadByUser() |

---

## 3. TABELA DE MAPEAMENTO REPOSITORY

### Métodos com "usuarioId" → "user"

| Antes | Depois | Campo Afetado |
|-------|--------|---------------|
| findByUsuarioId(Long) | findByUser(User) | user |
| countByUsuarioId(Long) | countByUser(User) | user |
| existsByMercadoIdAndUsuarioId(Long, Long) | existsByUserAndMercado(User, Mercado) | user |
| findByMercadoIdAndUsuarioId(Long, Long) | findByUserAndMercado(Mercado, User) | user |
| findByUsuarioIdOrderBy*() | findByUser(User, Pageable) com Sort | user |
| countByUsuarioIdAndLidaFalse(Long) | countUnreadByUser(User) | user |

### Métodos com "DataHora/DataEnvio/DataAdicao" → "createdAt"

| Antes | Depois | Campo Afetado |
|-------|--------|---------------|
| OrderByDataHoraDesc | OrderByCreatedAtDesc (com Sort) | createdAt |
| findByDataHoraBetweenOrderByDataHoraDesc() | findByDataRange() | createdAt |
| findByUsuarioIdOrderByDataEnvioDesc() | findByUser(User, Pageable) | createdAt |
| findByUsuarioIdOrderByDataAdicaoDesc() | findByUser(User, Sort) | createdAt |
| findByDataHoraBetween() | findByDataRange() | createdAt |

### Métodos com "TipoAcao" (String) → TipoAcao (Enum)

| Antes | Depois | Tipo Afetado |
|-------|--------|--------------|
| findByTipoAcaoOrderByDataHoraDesc(String) | findByAcao(TipoAcao) | acao |
| countByTipoAcao(String) | findByAcao(TipoAcao).getTotalElements() | acao |

---

## 4. RESUMO DE PADRÕES DE CORREÇÃO

### Padrão 1: Refatorar Assinatura (usuarioId → User)
```java
// Linhas afetadas: 104, 122, 150, 195 (FavoritoService)
//                  97, 189, 223 (AuditLogService) 
//                  234 (AvaliacaoService)
//                  112, 200 (NotificacaoService)

Mudar:  public TipoRetorno metodo(Long usuarioId, ...)
Para:   public TipoRetorno metodo(User usuario, ...)
```

### Padrão 2: Corrigir Chamada de Método (usuarioId → User)
```java
// Linhas afetadas: 56, 93, 138 (FavoritoService)
//                  311 (AvaliacaoService)

Mudar:  repository.methodByUsuarioId(usuarioId)
Para:   repository.methodByUser(usuario)
```

### Padrão 3: Corrigir Nomes de Campos (dataHora → createdAt)
```java
// Linhas afetadas: 104, 125, 151, 169, 187, 242 (AuditLogService)
//                  195 (FavoritoService)
//                  112 (NotificacaoService)

Mudar:  OrderByDataHoraDesc / OrderByDataEnvioDesc / OrderByDataAdicaoDesc
Para:   OrderByCreatedAtDesc ou Sort.by("createdAt").descending()
```

### Padrão 4: Converter String para Enum
```java
// Linhas afetadas: 169, 221 (AuditLogService)

Mudar:  repository.methodByTipoAcao(String tipoAcao)
Para:   repository.methodByAcao(TipoAcao.valueOf(tipoAcao))
```

### Padrão 5: Usar Objetos em vez de IDs
```java
// Linhas afetadas: 56, 93, 138 (FavoritoService)
//                  311 (AvaliacaoService)

Mudar:  repository.existsByMercadoIdAndUsuarioId(Long, Long)
Para:   repository.existsByUserAndMercado(User, Mercado)
```

---

## 5. CHECKLIST DE IMPLEMENTAÇÃO

### ✅ Pré-requisitos
- [ ] Fazer backup do código
- [ ] Ler ANALISE_COMPLETA_ERROS_COMPILACAO.md
- [ ] Ler GUIA_PASSO_A_PASSO_CORRECOES.md
- [ ] Entender os padrões acima

### ✅ AuditLogService.java
- [ ] Linha ~5-15: Adicionar import TipoAcao
- [ ] Linha 104: Refatorar obterAuditoriaDoUsuario
- [ ] Linha 125: Corrigir obterAuditoriaEntidade
- [ ] Linha 151: Refatorar obterAuditoriaEntreData
- [ ] Linha 169: Refatorar obterPorTipoAcao
- [ ] Linha 187: Corrigir obterPorTipoEntidade
- [ ] Linha 204: Refatorar contarAcoesDoUsuario
- [ ] Linha 221: Refatorar contarAcoes
- [ ] Linha 242: Refatorar obterAtividadeSuspeita

### ✅ FavoritoService.java
- [ ] Linha 56: Corrigir adicionarFavorito
- [ ] Linha 93: Corrigir removerFavorito
- [ ] Linha 122: Refatorar obterFavoritosDUsuario
- [ ] Linha 138: Corrigir verificarFavorito
- [ ] Linha 150: Refatorar contarFavoritosDoUsuario
- [ ] Linha 195: Refatorar obterFavoritosComPrioridade

### ✅ AvaliacaoService.java
- [ ] Linha 236: Refatorar obterAvaliacoesPorUsuario
- [ ] Linha 311: Corrigir validarDuplicata

### ✅ NotificacaoService.java
- [ ] Linha 112: Refatorar obterNotificacoesDoUsuario
- [ ] Linha 200: Refatorar contarNaoLidas

### ✅ Validação Final
- [ ] Compilação: `mvn clean compile` ✅
- [ ] Testes: `mvn test` ✅
- [ ] Sem warnings relevantes
- [ ] Documentação atualizada

---

## 6. COMANDOS ÚTEIS

### Compilar projeto
```bash
mvn clean compile
```

### Executar testes
```bash
mvn test
```

### Executar testes de um arquivo
```bash
mvn test -Dtest=AuditLogServiceTest
```

### Verificar erros de compilação
```bash
mvn compile 2>&1 | grep "\[ERROR\]"
```

### Limpar e compilar tudo
```bash
mvn clean install
```

---

## 7. ESTIMATIVAS

| Fase | Tempo | Complexidade | Risco |
|------|-------|--------------|-------|
| Leitura dos documentos | 10 min | Baixa | Baixo |
| AuditLogService | 8 min | Baixa | Baixo |
| FavoritoService | 6 min | Baixa | Baixo |
| AvaliacaoService | 2 min | Baixa | Baixo |
| NotificacaoService | 2 min | Baixa | Baixo |
| Testes e validação | 5 min | Média | Médio |
| **TOTAL** | **33 min** | **Baixa** | **Baixo** |

---

## 8. REFERÊNCIAS RÁPIDAS

- **Documento de Análise:** ANALISE_COMPLETA_ERROS_COMPILACAO.md
- **Guia Passo-a-Passo:** GUIA_PASSO_A_PASSO_CORRECOES.md
- **Matriz Visual:** MATRIZ_VISUAL_ERROS.md
- **Exemplos Práticos:** EXEMPLOS_PRATICOS_ANTES_DEPOIS.md
- **Referência Rápida:** Este arquivo (REFERENCIA_RAPIDA.md)

