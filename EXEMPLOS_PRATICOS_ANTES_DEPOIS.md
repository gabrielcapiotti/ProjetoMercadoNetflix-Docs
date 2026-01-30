# EXEMPLOS PRÁTICOS - ANTES E DEPOIS

## EXEMPLO 1: AuditLogService - Import Faltante

### ❌ ANTES (ERRO)
```java
package com.netflix.mercado.service;

import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class AuditLogService {
    
    public AuditLog registrarAcao(...) {
        // ...
        auditLog.setAcao(tipoAcao == null ? null : TipoAcao.valueOf(tipoAcao));
        //                                          ^^^^^^^^^ ERRO: Symbol not found
    }
}
```

### ✅ DEPOIS (CORRIGIDO)
```java
package com.netflix.mercado.service;

import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.entity.AuditLog.TipoAcao;  // ← ADICIONADO
import com.netflix.mercado.entity.User;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class AuditLogService {
    
    public AuditLog registrarAcao(...) {
        // ...
        auditLog.setAcao(tipoAcao == null ? null : TipoAcao.valueOf(tipoAcao));
        //                                          ^^^^^^^^^ ✅ ENCONTRADO
    }
}
```

**Mudança:** Adicionar `import com.netflix.mercado.entity.AuditLog.TipoAcao;`

---

## EXEMPLO 2: AuditLogService - Refatorar obterAuditoriaDoUsuario

### ❌ ANTES (ERRO)
```java
@Transactional(readOnly = true)
public Page<AuditLog> obterAuditoriaDoUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Buscando auditoria do usuário ID: {}", usuarioId);

    if (usuarioId == null) {
        throw new ValidationException("ID do usuário é obrigatório");
    }

    return auditLogRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId, pageable);
    //                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Método não existe
}
```

**Erros:**
- Método `findByUsuarioIdOrderByDataHoraDesc` não existe no repositório
- Repositório não tem métodos que aceitam `Long usuarioId`, mas sim `User user`
- Nome incorreto: `dataHora` vs. `createdAt`

### ✅ DEPOIS (CORRIGIDO)
```java
@Transactional(readOnly = true)
public Page<AuditLog> obterAuditoriaDoUsuario(User usuario, Pageable pageable) {
    log.debug("Buscando auditoria do usuário: {}", usuario.getEmail());

    if (usuario == null) {
        throw new ValidationException("Usuário é obrigatório");
    }

    return auditLogRepository.findByUser(usuario, pageable);
    //                        ^^^^^^^^^ ✅ Método existe no repositório
}
```

**Mudanças:**
1. Parâmetro: `Long usuarioId` → `User usuario`
2. Chamada: `findByUsuarioIdOrderByDataHoraDesc()` → `findByUser()`
3. Log: Usar `usuario.getEmail()` em vez de ID
4. Validação: Verificar se User é null

---

## EXEMPLO 3: AuditLogService - Refatorar obterAuditoriaEntreData

### ❌ ANTES (ERRO)
```java
@Transactional(readOnly = true)
public Page<AuditLog> obterAuditoriaEntreData(LocalDateTime dataInicio, 
                                               LocalDateTime dataFim, 
                                               Pageable pageable) {
    log.debug("Buscando auditoria entre {} e {}", dataInicio, dataFim);

    if (dataInicio == null) {
        throw new ValidationException("Data inicial é obrigatória");
    }
    if (dataFim == null) {
        throw new ValidationException("Data final é obrigatória");
    }
    if (dataInicio.isAfter(dataFim)) {
        throw new ValidationException("Data inicial não pode ser após a data final");
    }

    return auditLogRepository.findByDataHoraBetweenOrderByDataHoraDesc(dataInicio, dataFim, pageable);
    //                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Método não existe
}
```

**Erros:**
- Método não existe
- Usa `dataHora` em vez de `createdAt`

### ✅ DEPOIS (CORRIGIDO)
```java
@Transactional(readOnly = true)
public Page<AuditLog> obterAuditoriaEntreData(LocalDateTime dataInicio, 
                                               LocalDateTime dataFim, 
                                               Pageable pageable) {
    log.debug("Buscando auditoria entre {} e {}", dataInicio, dataFim);

    if (dataInicio == null) {
        throw new ValidationException("Data inicial é obrigatória");
    }
    if (dataFim == null) {
        throw new ValidationException("Data final é obrigatória");
    }
    if (dataInicio.isAfter(dataFim)) {
        throw new ValidationException("Data inicial não pode ser após a data final");
    }

    List<AuditLog> logs = auditLogRepository.findByDataRange(dataInicio, dataFim);
    //                                      ^^^^^^^^^^^^^^^^ ✅ Método existe
    
    // Aplicar paginação manualmente
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), logs.size());
    List<AuditLog> pageContent = logs.subList(start, end);
    
    return new PageImpl<>(pageContent, pageable, logs.size());
}
```

**Mudanças:**
1. Usar `findByDataRange()` que retorna `List`
2. Aplicar paginação manualmente
3. Retornar `PageImpl` para manter o tipo `Page`

---

## EXEMPLO 4: FavoritoService - Refatorar adicionarFavorito

### ❌ ANTES (ERRO)
```java
public Favorito adicionarFavorito(Long mercadoId, User usuario) {
    log.info("Adicionando mercado ID: {} aos favoritos do usuário: {}", 
             mercadoId, usuario.getEmail());

    // Buscar mercado
    Mercado mercado = mercadoService.getMercadoById(mercadoId);

    // Verificar se já está nos favoritos
    if (favoritoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuario.getId())) {
    //                       ^^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Método não existe
        log.warn("Mercado ID: {} já está nos favoritos do usuário: {}", 
                 mercadoId, usuario.getEmail());
        throw new ValidationException("Este mercado já está nos seus favoritos");
    }
    // ...
}
```

**Erro:**
- Método `existsByMercadoIdAndUsuarioId` não existe
- Repositório usa `existsByUserAndMercado` com objetos, não IDs

### ✅ DEPOIS (CORRIGIDO)
```java
public Favorito adicionarFavorito(Long mercadoId, User usuario) {
    log.info("Adicionando mercado ID: {} aos favoritos do usuário: {}", 
             mercadoId, usuario.getEmail());

    // Buscar mercado
    Mercado mercado = mercadoService.getMercadoById(mercadoId);

    // Verificar se já está nos favoritos
    if (favoritoRepository.existsByUserAndMercado(usuario, mercado)) {
    //                       ^^^^^^^^^^^^^^^^^^ ✅ Método existe
        log.warn("Mercado ID: {} já está nos favoritos do usuário: {}", 
                 mercadoId, usuario.getEmail());
        throw new ValidationException("Este mercado já está nos seus favoritos");
    }
    // ...
}
```

**Mudanças:**
1. Usar `existsByUserAndMercado()` com objetos `usuario` e `mercado`
2. Já temos o objeto `mercado` da linha anterior

---

## EXEMPLO 5: FavoritoService - Refatorar obterFavoritosComPrioridade

### ❌ ANTES (ERRO)
```java
@Transactional(readOnly = true)
public List<Favorito> obterFavoritosComPrioridade(Long usuarioId) {
    log.debug("Buscando favoritos do usuário ID: {} ordenados por prioridade", usuarioId);
    
    return favoritoRepository.findByUsuarioIdOrderByPrioridadeDescDataAdicaoDesc(usuarioId);
    //                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Múltiplos problemas
}
```

**Erros:**
1. Método não existe
2. Usa `usuarioId` em vez de `User user`
3. Campo `dataAdicao` não existe (deve ser `createdAt`)

### ✅ DEPOIS (CORRIGIDO)
```java
@Transactional(readOnly = true)
public List<Favorito> obterFavoritosComPrioridade(User usuario) {
    log.debug("Buscando favoritos do usuário: {} ordenados por prioridade", usuario.getEmail());
    
    return favoritoRepository.findByUser(
        usuario,
        PageRequest.of(0, Integer.MAX_VALUE,
            Sort.by("prioridade").descending()  // Prioridade DESC
                .and(Sort.by("createdAt").descending())  // createdAt DESC (era dataAdicao)
        )
    ).getContent();
}
```

**Mudanças:**
1. Parâmetro: `Long usuarioId` → `User usuario`
2. Usar `findByUser()` com `PageRequest` e `Sort`
3. Campo correto: `createdAt` em vez de `dataAdicao`
4. Retornar `.getContent()` para converter de Page para List

---

## EXEMPLO 6: AvaliacaoService - Refatorar validarDuplicata

### ❌ ANTES (ERRO)
```java
public void validarDuplicata(Long mercadoId, Long usuarioId) {
    log.debug("Validando duplicata de avaliação. Mercado: {}, Usuário: {}", 
              mercadoId, usuarioId);

    if (avaliacaoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuarioId)) {
    //                       ^^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Método não existe
        log.warn("Usuário {} já avaliou mercado {}", usuarioId, mercadoId);
        throw new ValidationException("Você já avaliou este mercado");
    }
}
```

**Erro:**
- Método não existe
- Repositório usa `findByMercadoAndUser` com objetos

### ✅ DEPOIS (CORRIGIDO)
```java
public void validarDuplicata(Long mercadoId, User usuario) {
    log.debug("Validando duplicata de avaliação. Mercado: {}, Usuário: {}", 
              mercadoId, usuario.getId());

    // Buscar mercado
    Mercado mercado = mercadoService.getMercadoById(mercadoId);
    
    if (avaliacaoRepository.findByMercadoAndUser(mercado, usuario).isPresent()) {
    //                       ^^^^^^^^^^^^^^^^^^ ✅ Método existe
        log.warn("Usuário {} já avaliou mercado {}", usuario.getId(), mercadoId);
        throw new ValidationException("Você já avaliou este mercado");
    }
}
```

**Mudanças:**
1. Parâmetro: `Long usuarioId` → `User usuario`
2. Buscar `Mercado` antes de usar
3. Usar `findByMercadoAndUser()` com objetos
4. Usar `.isPresent()` em vez de `existsByMercadoIdAndUsuarioId()`

---

## EXEMPLO 7: NotificacaoService - Refatorar obterNotificacoesDoUsuario

### ❌ ANTES (ERRO)
```java
@Transactional(readOnly = true)
public Page<Notificacao> obterNotificacoesDoUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Buscando notificações do usuário ID: {}", usuarioId);
    
    return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuarioId, pageable);
    //                           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Múltiplos problemas
}
```

**Erros:**
1. Método não existe
2. Campo `dataEnvio` não existe (deve ser `createdAt`)
3. Usa `usuarioId` em vez de `User user`

### ✅ DEPOIS (CORRIGIDO)
```java
@Transactional(readOnly = true)
public Page<Notificacao> obterNotificacoesDoUsuario(User usuario, Pageable pageable) {
    log.debug("Buscando notificações do usuário: {}", usuario.getEmail());
    
    return notificacaoRepository.findByUser(usuario, pageable);
    //                           ^^^^^^^^^ ✅ Método existe
}
```

**Mudanças:**
1. Parâmetro: `Long usuarioId` → `User usuario`
2. Usar `findByUser()` (já retorna ordenado por `createdAt` DESC)
3. Campo correto é `createdAt`, não `dataEnvio`

---

## EXEMPLO 8: NotificacaoService - Refatorar countByUsuarioIdAndLidaFalse

### ❌ ANTES (ERRO)
```java
@Transactional(readOnly = true)
public Long contarNaoLidas(Long usuarioId) {
    log.debug("Contando notificações não lidas do usuário ID: {}", usuarioId);
    
    return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    //                           ^^^^^^^^^^^^^^^^^^^^^^^^^^ ERRO: Método não existe
}
```

**Erro:**
- Método não existe
- Repositório tem `countUnreadByUser()`

### ✅ DEPOIS (CORRIGIDO)
```java
@Transactional(readOnly = true)
public Long contarNaoLidas(User usuario) {
    log.debug("Contando notificações não lidas do usuário: {}", usuario.getEmail());
    
    return notificacaoRepository.countUnreadByUser(usuario);
    //                           ^^^^^^^^^^^^^^^^^ ✅ Método existe
}
```

**Mudanças:**
1. Parâmetro: `Long usuarioId` → `User usuario`
2. Usar `countUnreadByUser()` que é o nome correto

---

## RESUMO DE PADRÕES

### Padrão 1: Long usuarioId → User usuario
```java
// Antes
método(Long usuarioId) { repository.methodByUsuarioId(usuarioId) }

// Depois
método(User usuario) { repository.methodByUser(usuario) }
```

### Padrão 2: dataHora/dataEnvio/dataAdicao → createdAt
```java
// Antes
OrderByDataHoraDesc / OrderByDataEnvioDesc / OrderByDataAdicaoDesc

// Depois
OrderByCreatedAtDesc (ou Sort.by("createdAt").descending())
```

### Padrão 3: Múltiplos IDs → Objetos
```java
// Antes
existsByMercadoIdAndUsuarioId(Long mercadoId, Long usuarioId)

// Depois
existsByUserAndMercado(User user, Mercado mercado)
```

### Padrão 4: Methods Count que não existem
```java
// Antes
countByUsuarioId(usuarioId)

// Depois
findByUser(usuario, Pageable.unpaged()).getTotalElements()
```

---

## VERIFICAÇÃO FINAL

Após aplicar todas as correções, seu código deve compilar sem erros:

```bash
$ mvn clean compile
[INFO] BUILD SUCCESS
```

E testes devem passar:

```bash
$ mvn test
[INFO] Tests run: XXX, Failures: 0, Errors: 0
[INFO] BUILD SUCCESS
```

