# ANÁLISE COMPLETA DE ERROS DE COMPILAÇÃO - FIX PLAN

## 1. PROBLEMA PRINCIPAL: REFERÊNCIA A ENUM SEM IMPORT

**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L57)
**Linha:** 57
**Problema:** `TipoAcao.valueOf()` usado sem estar qualificado com `AuditLog.`
**Erro:** Symbol not found - TipoAcao
**Solução:** Adicionar import: `import com.netflix.mercado.entity.AuditLog.TipoAcao;`

---

## 2. REPOSITORY METHOD CALLS COM NOMES INCORRETOS

Os repositórios foram refatorados com nomes corretos baseados nos campos das entities (user, acao, createdAt em vez de usuarioId, tipoAcao, dataHora). As Services ainda usam os nomes antigos.

### Mapeamento de Nomes Corretos:

| Campo da Entity | Nome Português Antigo | Nome Correto Novo |
|---|---|---|
| `user` (User user) | usuarioId | user (object) ou userId (Long) |
| `acao` (TipoAcao acao) | tipoAcao | acao |
| `createdAt` (LocalDateTime createdAt) | dataHora | createdAt |
| Ordenação DESC | OrderByDataHoraDesc | OrderByCreatedAtDesc |

---

## 3. LISTA COMPLETA DE ERROS - AuditLogService.java

### Erro 3.1: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L104)
**Linha:** 104
**Chamada Incorreta:** `auditLogRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId, pageable)`
**Chamada Correta:** `auditLogRepository.findByUser(userRepository.findById(usuarioId).orElseThrow(), pageable)`
**Motivo:** Repository usa `user` (User object), não `usuarioId` (Long), e `createdAt` em vez de `dataHora`
**Alternativa Melhor:** Refatorar para aceitar User object em vez de usuarioId

---

### Erro 3.2: Método de repository não existe  
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L125)
**Linha:** 125
**Chamada Incorreta:** `auditLogRepository.findByTipoEntidadeAndIdEntidadeOrderByDataHoraDesc(tipoEntidade, idEntidade)`
**Chamada Correta:** `auditLogRepository.findHistoricoEntidade(tipoEntidade, idEntidade)`
**Motivo:** Repository method renomeado e usa `createdAt` não `dataHora`

---

### Erro 3.3: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L151)
**Linha:** 151
**Chamada Incorreta:** `auditLogRepository.findByDataHoraBetweenOrderByDataHoraDesc(dataInicio, dataFim, pageable)`
**Chamada Correta:** `auditLogRepository.findByDataRange(dataInicio, dataFim)` (sem Pageable)
**Motivo:** Repository method usa `findByDataRange` e retorna List, não Page

---

### Erro 3.4: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L169)
**Linha:** 169
**Chamada Incorreta:** `auditLogRepository.findByTipoAcaoOrderByDataHoraDesc(tipoAcao, pageable)`
**Chamada Correta:** `auditLogRepository.findByAcao(AuditLog.TipoAcao.valueOf(tipoAcao), pageable)`
**Motivo:** Repository method é `findByAcao`, aceita TipoAcao enum, não String

---

### Erro 3.5: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L187)
**Linha:** 187
**Chamada Incorreta:** `auditLogRepository.findByTipoEntidadeOrderByDataHoraDesc(tipoEntidade, pageable)`
**Chamada Correta:** `auditLogRepository.findByTipoEntidade(tipoEntidade, pageable)`
**Motivo:** Repository method usa `createdAt` não `dataHora`

---

### Erro 3.6: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L204)
**Linha:** 204
**Chamada Incorreta:** `auditLogRepository.countByUsuarioId(usuarioId)`
**Chamada Correta:** `auditLogRepository.countByUser(userRepository.findById(usuarioId).orElseThrow())`
**Motivo:** Repository method é `countByUser`, requer User object, não Long ID

---

### Erro 3.7: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L221)
**Linha:** 221
**Chamada Incorreta:** `auditLogRepository.countByTipoAcao(tipoAcao)`
**Chamada Correta:** `auditLogRepository.findByAcao(AuditLog.TipoAcao.valueOf(tipoAcao), Pageable.unpaged()).getTotalElements()`
**Motivo:** Não existe `countByTipoAcao`. Usar `findByAcao` com `unpaged` para contar

---

### Erro 3.8: Método de repository não existe
**Arquivo:** [AuditLogService.java](src/main/java/com/netflix/mercado/service/AuditLogService.java#L242)
**Linha:** 242
**Chamada Incorreta:** `auditLogRepository.findByUsuarioIdAndDataHoraAfterOrderByDataHoraDesc(usuarioId, dataLimite)`
**Chamada Correta:** `auditLogRepository.findByDataRange(dataLimite, LocalDateTime.now())`
**Motivo:** Não existe método com esse nome. Usar `findByDataRange`

---

## 4. LISTA COMPLETA DE ERROS - FavoritoService.java

### Erro 4.1: Método de repository não existe
**Arquivo:** [FavoritoService.java](src/main/java/com/netflix/mercado/service/FavoritoService.java#L56)
**Linha:** 56
**Chamada Incorreta:** `favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId())`
**Chamada Correta:** `favoritoRepository.existsByUserAndMercado(usuario, mercado)`
**Motivo:** Repository method usa objetos `user` e `mercado`, não IDs primitivos

---

### Erro 4.2: Método de repository não existe
**Arquivo:** [FavoritoService.java](src/main/java/com/netflix/mercado/service/FavoritoService.java#L93)
**Linha:** 93
**Chamada Incorreta:** `favoritoRepository.findByMercadoIdAndUsuarioId(mercadoId, usuario.getId())`
**Chamada Correta:** `favoritoRepository.findByUserAndMercado(usuario, mercado)`
**Motivo:** Repository method usa objetos `user` e `mercado`, não IDs

---

### Erro 4.3: Método de repository não existe
**Arquivo:** [FavoritoService.java](src/main/java/com/netflix/mercado/service/FavoritoService.java#L122)
**Linha:** 122 (estimado em `obterFavoritosDUsuario`)
**Chamada Incorreta:** `favoritoRepository.findByUsuarioId(usuarioId, pageable)`
**Chamada Correta:** `favoritoRepository.findByUser(user, pageable)`
**Motivo:** Repository method usa `user` object, não `usuarioId`

---

### Erro 4.4: Método de repository não existe
**Arquivo:** [FavoritoService.java](src/main/java/com/netflix/mercado/service/FavoritoService.java#L138)
**Linha:** 138
**Chamada Incorreta:** `favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId())`
**Chamada Correta:** `favoritoRepository.existsByUserAndMercado(usuario, mercado)`
**Motivo:** Duplicado do Erro 4.1

---

### Erro 4.5: Método de repository não existe
**Arquivo:** [FavoritoService.java](src/main/java/com/netflix/mercado/service/FavoritoService.java#L150)
**Linha:** 150
**Chamada Incorreta:** `favoritoRepository.countByUsuarioId(usuarioId)`
**Chamada Correta:** `favoritoRepository.countByUser(user)`
**Motivo:** Repository method usa `user` object, não `usuarioId`

---

### Erro 4.6: Método de repository não existe
**Arquivo:** [FavoritoService.java](src/main/java/com/netflix/mercado/service/FavoritoService.java#L195)
**Linha:** 195
**Chamada Incorreta:** `favoritoRepository.findByUsuarioIdOrderByPrioridadeDescDataAdicaoDesc(usuarioId)`
**Chamada Correta:** `favoritoRepository.findByUser(user, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("prioridade").descending().and(Sort.by("createdAt").descending())))`
**Motivo:** Field name é `createdAt`, não `dataAdicao`. Repository method é `findByUser`

---

## 5. LISTA COMPLETA DE ERROS - AvaliacaoService.java

### Erro 5.1: Método de repository não existe
**Arquivo:** [AvaliacaoService.java](src/main/java/com/netflix/mercado/service/AvaliacaoService.java#L236)
**Linha:** 236
**Chamada Incorreta:** `avaliacaoRepository.findByUsuarioId(usuarioId, pageable)`
**Chamada Correta:** `avaliacaoRepository.findByUser(user, pageable)`
**Motivo:** Repository method usa `user` object, não `usuarioId`

---

### Erro 5.2: Método de repository não existe
**Arquivo:** [AvaliacaoService.java](src/main/java/com/netflix/mercado/service/AvaliacaoService.java#L311)
**Linha:** 311
**Chamada Incorreta:** `avaliacaoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuarioId)`
**Chamada Correta:** `avaliacaoRepository.findByMercadoAndUser(mercado, user).isPresent()`
**Motivo:** Repository method usa objetos `mercado` e `user`, não IDs

---

## 6. LISTA COMPLETA DE ERROS - NotificacaoService.java

### Erro 6.1: Método de repository não existe
**Arquivo:** [NotificacaoService.java](src/main/java/com/netflix/mercado/service/NotificacaoService.java#L112)
**Linha:** 112
**Chamada Incorreta:** `notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuarioId, pageable)`
**Chamada Correta:** `notificacaoRepository.findByUser(user, pageable)`
**Motivo:** Repository method usa `user` object e `createdAt` field (não `dataEnvio`)

---

### Erro 6.2: Método de repository não existe
**Arquivo:** [NotificacaoService.java](src/main/java/com/netflix/mercado/service/NotificacaoService.java#L200)
**Linha:** 200
**Chamada Incorreta:** `notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId)`
**Chamada Correta:** `notificacaoRepository.countUnreadByUser(user)`
**Motivo:** Repository method usa `user` object e é chamado `countUnreadByUser`

---

## 7. RESUMO EXECUTIVO DE CORREÇÕES NECESSÁRIAS

### Por Arquivo:

**AuditLogService.java** - 8 erros
- [ ] Adicionar import: `import com.netflix.mercado.entity.AuditLog.TipoAcao;` (linha 57)
- [ ] Corrigir 7 chamadas de métodos de repository com nomes/tipos incorretos

**FavoritoService.java** - 6 erros  
- [ ] Refatorar para usar objetos `User` e `Mercado` em vez de IDs
- [ ] Corrigir nomes de campos: `dataAdicao` → `createdAt`

**AvaliacaoService.java** - 2 erros
- [ ] Refatorar para usar objetos `User` e `Mercado` em vez de IDs

**NotificacaoService.java** - 2 erros
- [ ] Refatorar para usar objeto `User` em vez de ID
- [ ] Corrigir nomes de campos: `dataEnvio` → `createdAt`

### Total: 18 erros de compilação a serem corrigidos

---

## 8. PADRÃO DE NOMES CORRETOS PARA REPOSITORY METHODS

Com base nas entities atuais refatoradas:

```
❌ ANTIGO (INCORRETO)          ✅ NOVO (CORRETO)
findByUsuarioId               findByUser(User user)
findByUsuarioIdOrderBy...     findByUser(User user, Pageable) com Sort
findByMercadoId               findByMercado(Mercado mercado)
findByUsuarioIdAndMercadoId   findByUserAndMercado(User user, Mercado mercado)
existsByUsuarioIdAnd...       existsByUserAnd...
countByUsuarioId              countByUser(User user)
countByUsuarioIdAnd...        countByUserAnd...
OrderByDataHoraDesc           OrderByCreatedAtDesc
OrderByDataEnvioDesc          OrderByCreatedAtDesc
OrderByDataAdicaoDesc         OrderByCreatedAtDesc
TipoAcao.valueOf()            AuditLog.TipoAcao.valueOf()
tipoAcao (String param)       acao (AuditLog.TipoAcao enum)
```

---

## 9. RECOMENDAÇÕES DE REFATORAÇÃO

1. **Adicionar Autowired UserRepository em services** que precisam buscar User por ID
2. **Refatorar métodos públicos** que recebem `usuarioId` para receberem `User user` quando possível
3. **Usar entity objects** em vez de IDs primitivos nas chamadas repository
4. **Atualizar testes** correspondentemente com os novos assinatures

