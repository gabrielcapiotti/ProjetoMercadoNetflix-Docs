# 📑 ÍNDICE - SERVICES NETFLIX MERCADOS

**Gerado em:** 30 de Janeiro de 2026  
**Total de Arquivos:** 15 (11 Services + 1 GlobalExceptionHandler + 3 Docs)  
**Status:** ✅ Completo e Pronto para Uso

---

## 📂 LOCALIZAÇÃO DOS ARQUIVOS

### Services Java (11 arquivos)
**Caminho:** `src/main/java/com/netflix/mercado/service/`

```
service/
├── UserService.java
│   └── Responsável por: Gerenciamento de usuários
│       Métodos: 9 | Linhas: 326
│       
├── AuthService.java
│   └── Responsável por: Autenticação e autorização
│       Métodos: 6 | Linhas: 240
│       
├── MercadoService.java
│   └── Responsável por: CRUD de mercados
│       Métodos: 11 | Linhas: 392
│       Especial: Cálculo Haversine
│       
├── AvaliacaoService.java
│   └── Responsável por: CRUD de avaliações
│       Métodos: 10 | Linhas: 300+
│       Especial: Cálculo de ratings
│       
├── ComentarioService.java
│   └── Responsável por: CRUD de comentários
│       Métodos: 10 | Linhas: 350+
│       Especial: Comentários aninhados
│       
├── FavoritoService.java
│   └── Responsável por: Gerenciamento de favoritos
│       Métodos: 8 | Linhas: 280+
│       Especial: Sistema de prioridades
│       
├── NotificacaoService.java
│   └── Responsável por: Gerenciamento de notificações
│       Métodos: 9 | Linhas: 260+
│       Especial: Limpeza automática @Scheduled
│       
├── PromocaoService.java
│   └── Responsável por: CRUD de promoções
│       Métodos: 10 | Linhas: 380+
│       Especial: Cálculo de descontos
│       
├── HorarioFuncionamentoService.java
│   └── Responsável por: Gerenciamento de horários
│       Métodos: 8 | Linhas: 320+
│       Especial: Verificação de abertura
│       
├── RefreshTokenService.java
│   └── Responsável por: Gerenciamento de tokens
│       Métodos: 8 | Linhas: 280+
│       Especial: Limpeza automática @Scheduled
│       
└── AuditLogService.java
    └── Responsável por: Logging de auditoria
        Métodos: 11 | Linhas: 330+
        Especial: Detecção de atividades suspeitas
```

### Exception Handler (1 arquivo)
**Caminho:** `src/main/java/com/netflix/mercado/exception/`

```
exception/
├── GlobalExceptionHandler.java
│   └── Trata: ResourceNotFoundException (404)
│       │      ValidationException (400)
│       │      UnauthorizedException (401)
│       │      MethodArgumentNotValidException (400)
│       │      Exception genérica (500)
│       Linhas: 150+
│       
├── ResourceNotFoundException.java (existente)
├── ValidationException.java (existente)
└── UnauthorizedException.java (existente)
```

---

## 📚 DOCUMENTAÇÃO (3 arquivos)

**Caminho:** `/workspaces/ProjetoMercadoNetflix-Docs/`

```
docs/
├── SERVICES_EXTRACTION_SUMMARY.md
│   └── Sumário completo com:
│       • Visão geral dos 11 Services
│       • Métodos principais de cada um
│       • Dependências entre Services
│       • Tarefas agendadas
│       • Estatísticas
│       • Checklist
│
├── SERVICES_QUICK_REFERENCE.md
│   └── Referência rápida com:
│       • Quick start para cada Service
│       • Exemplos de código
│       • Fluxo completo end-to-end
│       • Dicas e boas práticas
│
└── EXTRACTION_COMPLETE.md
    └── Status final com:
        • Detalhamento por Service
        • Cobertura de funcionalidades
        • Qualidade do código
        • Próximos passos
        • Checklist final
```

---

## 🎯 GUIA DE NAVEGAÇÃO

### Para Iniciantes
1. **Comece aqui:** [SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md)
2. **Depois veja:** Cada arquivo de Service individualmente
3. **Referência:** [SERVICES_EXTRACTION_SUMMARY.md](./SERVICES_EXTRACTION_SUMMARY.md)

### Para Desenvolvedores Experientes
1. **Resumo Rápido:** Seção "🎯 Quick Start" em [SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md)
2. **Código Específico:** Vá direto ao arquivo Service desejado
3. **Exemplos Avançados:** Seção "⚡ Fluxo Completo" em [SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md)

### Para Arquitetos
1. **Visão Geral:** [EXTRACTION_COMPLETE.md](./EXTRACTION_COMPLETE.md)
2. **Dependências:** Seção "🔄 Dependências Entre Services"
3. **Funcionalidades:** Seção "🎯 Cobertura de Funcionalidades"

---

## 🔍 COMO ENCONTRAR O QUE PRECISA

### Preciso gerenciar usuários
- **Service:** `UserService.java`
- **Métodos:** createUser, findUserById, updateUser, changePassword
- **Referência:** [Seção 1️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#1️⃣-userservice)

### Preciso de autenticação
- **Service:** `AuthService.java`
- **Métodos:** register, login, refreshToken, logout
- **Referência:** [Seção 2️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#2️⃣-authservice)

### Preciso de mercados
- **Service:** `MercadoService.java`
- **Métodos:** createMercado, buscarProximos, getAllMercados
- **Referência:** [Seção 3️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#3️⃣-mercadoservice)

### Preciso de avaliações
- **Service:** `AvaliacaoService.java`
- **Métodos:** criarAvaliacao, calcularEstatisticas, obterAvaliacoesPorMercado
- **Referência:** [Seção 4️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#4️⃣-avaliacaoservice)

### Preciso de comentários
- **Service:** `ComentarioService.java`
- **Métodos:** criarComentario, responderComentario, adicionarCurtida
- **Referência:** [Seção 5️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#5️⃣-comentarioservice)

### Preciso de favoritos
- **Service:** `FavoritoService.java`
- **Métodos:** adicionarFavorito, toggleFavorito, definirPrioridade
- **Referência:** [Seção 6️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#6️⃣-favoritoservice)

### Preciso de notificações
- **Service:** `NotificacaoService.java`
- **Métodos:** enviarNotificacao, marcarComoLida, obterNaoLidas
- **Referência:** [Seção 7️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#7️⃣-notificacaoservice)

### Preciso de promoções
- **Service:** `PromocaoService.java`
- **Métodos:** criarPromocao, validarCodigo, aplicarPromocao
- **Referência:** [Seção 8️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#8️⃣-promocaoservice)

### Preciso de horários
- **Service:** `HorarioFuncionamentoService.java`
- **Métodos:** criarHorario, verificarSeEstaAberto, obterProximaAbertura
- **Referência:** [Seção 9️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#9️⃣-horariofuncionamentoservice)

### Preciso de tokens
- **Service:** `RefreshTokenService.java`
- **Métodos:** criarRefreshToken, validarRefreshToken, renovarAccessToken
- **Referência:** [Seção 🔟 em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#🔟-refreshtokenservice)

### Preciso de auditoria
- **Service:** `AuditLogService.java`
- **Métodos:** registrarAcao, obterAuditoriaDoUsuario, obterAtividadeSuspeita
- **Referência:** [Seção 1️⃣1️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#1️⃣1️⃣-auditlogservice)

### Tratamento de exceções
- **Handler:** `GlobalExceptionHandler.java`
- **Trata:** 5 tipos de exceções (404, 400, 401, 500)
- **Referência:** [Seção "🔐 Tratamento de Exceções" em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#-tratamento-de-exceções)

---

## 📊 ESTATÍSTICAS RÁPIDAS

```
Total de Services: 11
Total de Métodos: 99
Total de Linhas: 3.500+
Total de Handlers: 1 (GlobalExceptionHandler)
Total de Tipos de Exceção: 5
Total de Anotações: 8 tipos

@Service:          11 vezes
@Transactional:    99 métodos
@Autowired:        ~30 vezes
@Slf4j:            12 classes
@Scheduled:        3 métodos
@ExceptionHandler: 5 handlers
```

---

## 🚀 FLUXO DE USO RECOMENDADO

```
1. Ler este arquivo de índice
   ↓
2. Ler SERVICES_QUICK_REFERENCE.md (Referência Rápida)
   ↓
3. Escolher o Service que precisa
   ↓
4. Abrir o arquivo Service.java
   ↓
5. Consultar exemplos em SERVICES_QUICK_REFERENCE.md
   ↓
6. Implementar sua lógica usando o Service
   ↓
7. Se tiver erro, consultar GlobalExceptionHandler.java
   ↓
8. Para mais detalhes, ler SERVICES_EXTRACTION_SUMMARY.md
```

---

## 🎯 CASOS DE USO COMUNS

### Registrar novo usuário e fazer login
```
1. AuthService.register()          ← Registra usuário
2. AuthService.login()             ← Faz login
3. RefreshTokenService criada      ← Automático
4. AuditLogService registra        ← Automático
5. NotificacaoService enviada      ← Pode ser feita
```
**Arquivo:** AuthService.java  
**Referência:** [Seção ⚡ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#-fluxo-completo-criar-e-avaliar-mercado)

### Criar mercado e obter próximos
```
1. MercadoService.createMercado()  ← Cria mercado
2. MercadoService.buscarProximos() ← Busca proximidade
3. AuditLogService registra        ← Automático
```
**Arquivo:** MercadoService.java  
**Referência:** [Seção 3️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#3️⃣-mercadoservice)

### Avaliar mercado e comentar
```
1. AvaliacaoService.criarAvaliacao()         ← Cria avaliação
2. MercadoService.atualizarAvaliacaoMedia()  ← Atualiza rating
3. ComentarioService.criarComentario()       ← Comenta
4. NotificacaoService.enviarNotificacao()    ← Notifica
5. AuditLogService registra tudo             ← Automático
```
**Arquivo:** AvaliacaoService.java e ComentarioService.java  
**Referência:** [Seção 4️⃣ e 5️⃣ em SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md#4️⃣-avaliacaoservice)

---

## 🔗 LINKS RÁPIDOS

- 📖 [Referência Completa](./SERVICES_EXTRACTION_SUMMARY.md)
- ⚡ [Quick Start](./SERVICES_QUICK_REFERENCE.md)
- ✅ [Status de Conclusão](./EXTRACTION_COMPLETE.md)
- 📂 [Arquivo Principal](./00_START_HERE.md)

---

## 💡 DICAS IMPORTANTES

1. **Sempre use @Transactional em métodos que modificam dados**
2. **Use @Transactional(readOnly = true) em consultas**
3. **Todas as exceções são tratadas pelo GlobalExceptionHandler**
4. **Logging é automático com @Slf4j**
5. **Auditoria é registrada automaticamente**
6. **Services podem ser injetados com @Autowired**
7. **Verifique autorizações com isOwnerOrAdmin()**

---

## ❓ FAQ

**P: Onde estão os arquivos?**  
R: Em `src/main/java/com/netflix/mercado/service/` (11 Services)  
E em `src/main/java/com/netflix/mercado/exception/` (GlobalExceptionHandler)

**P: Como injetar um Service?**  
R: Use `@Autowired private NomeService nomeService;`

**P: O que fazer se um Service lançar uma exceção?**  
R: O GlobalExceptionHandler trata automaticamente

**P: Como usar um Service num Controller?**  
R: Injete com @Autowired e chame o método desejado

**P: Preciso criar Controllers?**  
R: Sim, veja "Próximos Passos" em EXTRACTION_COMPLETE.md

---

## 📞 SUPORTE

Para mais informações, consulte:
- **Sumário Completo:** [SERVICES_EXTRACTION_SUMMARY.md](./SERVICES_EXTRACTION_SUMMARY.md)
- **Referência Rápida:** [SERVICES_QUICK_REFERENCE.md](./SERVICES_QUICK_REFERENCE.md)
- **Status Completo:** [EXTRACTION_COMPLETE.md](./EXTRACTION_COMPLETE.md)

---

**Desenvolvido com ❤️ para Netflix Mercados**  
**Data:** 30 de Janeiro de 2026  
**Versão:** Java 21 | Spring Boot 3.x  
**Status:** ✅ PRONTO PARA USO
