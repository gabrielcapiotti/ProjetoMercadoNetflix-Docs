# GUIA PASSO-A-PASSO DE CORREÇÃO DOS ERROS

## FASE 1: AuditLogService.java - Correções Críticas

### Passo 1.1: Adicionar Import do Enum TipoAcao
**Arquivo:** `src/main/java/com/netflix/mercado/service/AuditLogService.java`
**Linha:** ~5-15 (após outros imports)

```java
// Adicionar esta linha aos imports:
import com.netflix.mercado.entity.AuditLog.TipoAcao;
```

**Status:** Importação necessária para usar `TipoAcao.valueOf()` sem qualificação

---

### Passo 1.2: Refatorar método `obterAuditoriaDoUsuario` (linha 97-104)

**ANTES:**
```java
public Page<AuditLog> obterAuditoriaDoUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Buscando auditoria do usuário ID: {}", usuarioId);

    if (usuarioId == null) {
        throw new ValidationException("ID do usuário é obrigatório");
    }

    return auditLogRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId, pageable);
}
```

**DEPOIS:**
```java
public Page<AuditLog> obterAuditoriaDoUsuario(User usuario, Pageable pageable) {
    log.debug("Buscando auditoria do usuário: {}", usuario.getEmail());

    if (usuario == null) {
        throw new ValidationException("Usuário é obrigatório");
    }

    return auditLogRepository.findByUser(usuario, pageable);
}
```

**Mudanças:**
- Recebe `User usuario` em vez de `Long usuarioId`
- Chamada corrigida para `findByUser(usuario, pageable)`
- Log atualizado

---

### Passo 1.3: Refatorar método `obterAuditoriaEntidade` (linha 106-125)

**ANTES:**
```java
public List<AuditLog> obterAuditoriaEntidade(String tipoEntidade, Long idEntidade) {
    // ...
    return auditLogRepository.findByTipoEntidadeAndIdEntidadeOrderByDataHoraDesc(tipoEntidade, idEntidade);
}
```

**DEPOIS:**
```java
public List<AuditLog> obterAuditoriaEntidade(String tipoEntidade, Long idEntidade) {
    // ...
    return auditLogRepository.findHistoricoEntidade(tipoEntidade, idEntidade);
}
```

**Mudanças:**
- Chamada corrigida para `findHistoricoEntidade()` (método existente no repositório)

---

### Passo 1.4: Refatorar método `obterAuditoriaEntreData` (linha 127-151)

**ANTES:**
```java
return auditLogRepository.findByDataHoraBetweenOrderByDataHoraDesc(dataInicio, dataFim, pageable);
```

**DEPOIS:**
```java
// Nota: O repositório retorna List, não Page. Método precisa ser refatorado.
List<AuditLog> logs = auditLogRepository.findByDataRange(dataInicio, dataFim);
// Se precisa de paginação, aplicar depois:
return new PageImpl<>(logs, pageable, logs.size());
```

**Mudanças:**
- Chamada corrigida para `findByDataRange()` que retorna List
- Aplicar paginação manualmente se necessário

---

### Passo 1.5: Refatorar método `obterPorTipoAcao` (linha 153-169)

**ANTES:**
```java
public Page<AuditLog> obterPorTipoAcao(String tipoAcao, Pageable pageable) {
    log.debug("Buscando auditoria por tipo de ação: {}", tipoAcao);

    if (tipoAcao == null || tipoAcao.isBlank()) {
        throw new ValidationException("Tipo de ação é obrigatório");
    }

    return auditLogRepository.findByTipoAcaoOrderByDataHoraDesc(tipoAcao, pageable);
}
```

**DEPOIS:**
```java
public Page<AuditLog> obterPorTipoAcao(String tipoAcao, Pageable pageable) {
    log.debug("Buscando auditoria por tipo de ação: {}", tipoAcao);

    if (tipoAcao == null || tipoAcao.isBlank()) {
        throw new ValidationException("Tipo de ação é obrigatório");
    }

    TipoAcao acao = TipoAcao.valueOf(tipoAcao);
    return auditLogRepository.findByAcao(acao, pageable);
}
```

**Mudanças:**
- Converter String para enum TipoAcao antes de chamar repository
- Usar `findByAcao()` que aceita enum

---

### Passo 1.6: Refatorar método `obterPorTipoEntidade` (linha 171-187)

**ANTES:**
```java
return auditLogRepository.findByTipoEntidadeOrderByDataHoraDesc(tipoEntidade, pageable);
```

**DEPOIS:**
```java
return auditLogRepository.findByTipoEntidade(tipoEntidade, pageable);
```

**Mudanças:**
- Remover `OrderByDataHoraDesc` do nome (já feito no repositório)

---

### Passo 1.7: Refatorar método `contarAcoesDoUsuario` (linha 189-204)

**ANTES:**
```java
public Long contarAcoesDoUsuario(Long usuarioId) {
    log.debug("Contando ações do usuário ID: {}", usuarioId);

    if (usuarioId == null) {
        throw new ValidationException("ID do usuário é obrigatório");
    }

    return auditLogRepository.countByUsuarioId(usuarioId);
}
```

**DEPOIS:**
```java
public Long contarAcoesDoUsuario(User usuario) {
    log.debug("Contando ações do usuário: {}", usuario.getEmail());

    if (usuario == null) {
        throw new ValidationException("Usuário é obrigatório");
    }

    return auditLogRepository.countByUser(usuario);
}
```

**Mudanças:**
- Recebe `User usuario` em vez de `Long usuarioId`
- Chamada corrigida para `countByUser(usuario)`

---

### Passo 1.8: Refatorar método `contarAcoes` (linha 206-221)

**ANTES:**
```java
public Long contarAcoes(String tipoAcao) {
    log.debug("Contando ações do tipo: {}", tipoAcao);

    if (tipoAcao == null || tipoAcao.isBlank()) {
        throw new ValidationException("Tipo de ação é obrigatório");
    }

    return auditLogRepository.countByTipoAcao(tipoAcao);
}
```

**DEPOIS:**
```java
public Long contarAcoes(String tipoAcao) {
    log.debug("Contando ações do tipo: {}", tipoAcao);

    if (tipoAcao == null || tipoAcao.isBlank()) {
        throw new ValidationException("Tipo de ação é obrigatório");
    }

    TipoAcao acao = TipoAcao.valueOf(tipoAcao);
    return auditLogRepository.findByAcao(acao, Pageable.unpaged()).getTotalElements();
}
```

**Mudanças:**
- Não existe `countByTipoAcao`, usar `findByAcao` com unpaged
- Usar `getTotalElements()` para contar

---

### Passo 1.9: Refatorar método `obterAtividadeSuspeita` (linha 223-242)

**ANTES:**
```java
public List<AuditLog> obterAtividadeSuspeita(Long usuarioId, Integer minutosAtrás, Integer minimumActions) {
    log.debug("Analisando atividade suspeita do usuário ID: {} nos últimos {} minutos", usuarioId, minutosAtrás);

    if (usuarioId == null) {
        throw new ValidationException("ID do usuário é obrigatório");
    }

    LocalDateTime dataLimite = LocalDateTime.now().minusMinutes(minutosAtrás);

    return auditLogRepository.findByUsuarioIdAndDataHoraAfterOrderByDataHoraDesc(usuarioId, dataLimite);
}
```

**DEPOIS:**
```java
public List<AuditLog> obterAtividadeSuspeita(User usuario, Integer minutosAtrás, Integer minimumActions) {
    log.debug("Analisando atividade suspeita do usuário: {} nos últimos {} minutos", usuario.getEmail(), minutosAtrás);

    if (usuario == null) {
        throw new ValidationException("Usuário é obrigatório");
    }

    LocalDateTime dataLimite = LocalDateTime.now().minusMinutes(minutosAtrás);
    LocalDateTime agora = LocalDateTime.now();

    return auditLogRepository.findByDataRange(dataLimite, agora).stream()
        .filter(log -> log.getUser().getId().equals(usuario.getId()))
        .toList();
}
```

**Mudanças:**
- Recebe `User usuario` em vez de `Long usuarioId`
- Usar `findByDataRange()` e filtrar por usuário
- Remover campo `minimumActions` não utilizado

---

## FASE 2: FavoritoService.java - Correções

### Passo 2.1: Refatorar método `adicionarFavorito` (linha 56)

**ANTES:**
```java
if (favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId())) {
    log.warn("Mercado ID: {} já está nos favoritos do usuário: {}", mercadoId, usuario.getEmail());
    throw new ValidationException("Este mercado já está nos seus favoritos");
}
```

**DEPOIS:**
```java
if (favoritoRepository.existsByUserAndMercado(usuario, mercado)) {
    log.warn("Mercado ID: {} já está nos favoritos do usuário: {}", mercadoId, usuario.getEmail());
    throw new ValidationException("Este mercado já está nos seus favoritos");
}
```

**Mudanças:**
- Usar `existsByUserAndMercado(usuario, mercado)` com objetos
- Já temos `mercado` da linha anterior

---

### Passo 2.2: Refatorar método `removerFavorito` (linha 93)

**ANTES:**
```java
Favorito favorito = favoritoRepository.findByMercadoIdAndUsuarioId(mercadoId, usuario.getId())
        .orElseThrow(() -> {
            log.warn("Favorito não encontrado para mercado ID: {} e usuário: {}", mercadoId, usuario.getEmail());
            return new ResourceNotFoundException("Favorito não encontrado");
        });
```

**DEPOIS:**
```java
Favorito favorito = favoritoRepository.findByUserAndMercado(usuario, mercado)
        .orElseThrow(() -> {
            log.warn("Favorito não encontrado para mercado ID: {} e usuário: {}", mercadoId, usuario.getEmail());
            return new ResourceNotFoundException("Favorito não encontrado");
        });
```

**Mudanças:**
- Usar `findByUserAndMercado(usuario, mercado)` com objetos
- Adicionar busca de `Mercado mercado` antes desta chamada

---

### Passo 2.3: Refatorar método `obterFavoritosDUsuario` (linha 122)

**ANTES:**
```java
public Page<Favorito> obterFavoritosDUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Buscando favoritos do usuário ID: {}", usuarioId);
    return favoritoRepository.findByUsuarioId(usuarioId, pageable);
}
```

**DEPOIS:**
```java
public Page<Favorito> obterFavoritosDUsuario(User usuario, Pageable pageable) {
    log.debug("Buscando favoritos do usuário: {}", usuario.getEmail());
    return favoritoRepository.findByUser(usuario, pageable);
}
```

**Mudanças:**
- Receber `User usuario` em vez de `Long usuarioId`
- Usar `findByUser(usuario, pageable)`

---

### Passo 2.4: Refatorar método `verificarFavorito` (linha 138)

**ANTES:**
```java
return favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId());
```

**DEPOIS:**
```java
return favoritoRepository.existsByUserAndMercado(usuario, mercado);
```

**Mudanças:**
- Usar `existsByUserAndMercado(usuario, mercado)` com objetos
- Adicionar busca de `Mercado mercado` antes

---

### Passo 2.5: Refatorar método `contarFavoritosDoUsuario` (linha 150)

**ANTES:**
```java
public Long contarFavoritosDoUsuario(Long usuarioId) {
    log.debug("Contando favoritos do usuário ID: {}", usuarioId);
    return favoritoRepository.countByUsuarioId(usuarioId);
}
```

**DEPOIS:**
```java
public Long contarFavoritosDoUsuario(User usuario) {
    log.debug("Contando favoritos do usuário: {}", usuario.getEmail());
    return favoritoRepository.countByUser(usuario);
}
```

**Mudanças:**
- Receber `User usuario` em vez de `Long usuarioId`
- Usar `countByUser(usuario)`

---

### Passo 2.6: Refatorar método `obterFavoritosComPrioridade` (linha 195)

**ANTES:**
```java
public List<Favorito> obterFavoritosComPrioridade(Long usuarioId) {
    log.debug("Buscando favoritos do usuário ID: {} ordenados por prioridade", usuarioId);
    return favoritoRepository.findByUsuarioIdOrderByPrioridadeDescDataAdicaoDesc(usuarioId);
}
```

**DEPOIS:**
```java
public List<Favorito> obterFavoritosComPrioridade(User usuario) {
    log.debug("Buscando favoritos do usuário: {} ordenados por prioridade", usuario.getEmail());
    return favoritoRepository.findByUser(
        usuario, 
        PageRequest.of(0, Integer.MAX_VALUE, 
            Sort.by("prioridade").descending()
                .and(Sort.by("createdAt").descending()))
    ).getContent();
}
```

**Mudanças:**
- Receber `User usuario`
- Campo correto é `createdAt`, não `dataAdicao`
- Usar `findByUser` com Sort próprio

---

## FASE 3: AvaliacaoService.java - Correções

### Passo 3.1: Refatorar método `obterAvaliacoesPorUsuario` (linha 236)

**ANTES:**
```java
public Page<Avaliacao> obterAvaliacoesPorUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Buscando avaliações do usuário ID: {}", usuarioId);
    return avaliacaoRepository.findByUsuarioId(usuarioId, pageable);
}
```

**DEPOIS:**
```java
public Page<Avaliacao> obterAvaliacoesPorUsuario(User usuario, Pageable pageable) {
    log.debug("Buscando avaliações do usuário: {}", usuario.getEmail());
    return avaliacaoRepository.findByUser(usuario, pageable);
}
```

**Mudanças:**
- Receber `User usuario` em vez de `Long usuarioId`
- Usar `findByUser(usuario, pageable)`

---

### Passo 3.2: Refatorar método `validarDuplicata` (linha 311)

**ANTES:**
```java
if (avaliacaoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuarioId)) {
    log.warn("Usuário {} já avaliou mercado {}", usuarioId, mercadoId);
    throw new ValidationException("Você já avaliou este mercado");
}
```

**DEPOIS:**
```java
if (avaliacaoRepository.findByMercadoAndUser(mercado, usuario).isPresent()) {
    log.warn("Usuário {} já avaliou mercado {}", usuario.getId(), mercadoId);
    throw new ValidationException("Você já avaliou este mercado");
}
```

**Mudanças:**
- Usar `findByMercadoAndUser(mercado, usuario)` com objetos
- Adicionar busca de `Mercado mercado` antes desta chamada

---

## FASE 4: NotificacaoService.java - Correções

### Passo 4.1: Refatorar método `obterNotificacoesDoUsuario` (linha 112)

**ANTES:**
```java
return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuarioId, pageable);
```

**DEPOIS:**
```java
return notificacaoRepository.findByUser(usuario, pageable);
```

**Mudanças:**
- Campo correto é `createdAt`, não `dataEnvio`
- Usar `findByUser(usuario, pageable)` com User object

---

### Passo 4.2: Refatorar método `contarNaoLidas` (linha 200)

**ANTES:**
```java
return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
```

**DEPOIS:**
```java
return notificacaoRepository.countUnreadByUser(usuario);
```

**Mudanças:**
- Usar `countUnreadByUser(usuario)` que é o nome correto no repositório

---

## CHECKLIST DE VALIDAÇÃO

- [ ] AuditLogService.java - Import de TipoAcao adicionado
- [ ] AuditLogService.java - 8 métodos refatorados
- [ ] FavoritoService.java - 6 métodos/chamadas refatoradas
- [ ] AvaliacaoService.java - 2 métodos refatorados
- [ ] NotificacaoService.java - 2 métodos refatorados
- [ ] UserRepository injetado em services que precisam buscar User por ID
- [ ] Testes unitários atualizados com novos assinatures
- [ ] Projeto compila sem erros
- [ ] Testes passam com sucesso

