# 🚀 Guia de Uso dos 11 Services - Netflix Mercados

## 📋 Visão Geral

Este documento fornece um guia de uso prático para os 11 Services implementados no projeto Netflix Mercados com Spring Boot 3.x e Java 21.

---

## 1️⃣ UserService

**Localização**: `com.netflix.mercado.service.UserService`

### Responsabilidades
- Gerenciar criação, atualização e consulta de usuários
- Alterar senhas e verificar emails
- Habilitar/desabilitar autenticação de dois fatores

### Métodos Principais

```java
// Criar novo usuário
User user = userService.createUser(registerRequest);

// Buscar usuário por ID
User user = userService.findUserById(userId);

// Buscar por email
User user = userService.findUserByEmail("email@example.com");

// Atualizar usuário
User updated = userService.updateUser(userId, updateRequest);

// Alterar senha
userService.changePassword(userId, changePasswordRequest);

// Habilitar 2FA
userService.enableTwoFactor(userId);

// Desabilitar 2FA
userService.disableTwoFactor(userId);

// Verificar email
userService.verifyEmail(userId);

// Listar todos com paginação
Page<UserResponse> users = userService.getAllUsers(pageable);
```

### Anotações Importantes
- `@Transactional` em métodos que modificam dados
- `@Transactional(readOnly = true)` em consultas
- Validações automáticas via `ValidationException`

---

## 2️⃣ AuthService

**Localização**: `com.netflix.mercado.service.AuthService`

### Responsabilidades
- Autenticação de usuários (login/registro)
- Geração e validação de tokens JWT
- Gerenciar refresh tokens
- Logout de usuários

### Métodos Principais

```java
// Registrar novo usuário
JwtAuthenticationResponse response = authService.register(registerRequest);

// Fazer login
JwtAuthenticationResponse response = authService.login(loginRequest);

// Renovar access token
JwtAuthenticationResponse response = authService.refreshToken(refreshTokenString);

// Fazer logout
authService.logout(userId);

// Validar token JWT
boolean isValid = authService.validateToken(token);

// Extrair usuário do token
User user = authService.getUserFromToken(token);
```

### Fluxo de Autenticação
1. Usuário faz login com email/senha
2. Sistema valida credenciais
3. Gera JWT access token (curta validade) e refresh token (longa validade)
4. Cliente usa access token nas requisições
5. Quando access token expira, usa refresh token para obter novo access token

---

## 3️⃣ MercadoService

**Localização**: `com.netflix.mercado.service.MercadoService`

### Responsabilidades
- Criar, atualizar e deletar mercados
- Buscar mercados por vários critérios
- Aprovar/rejeitar mercados (admin)
- Atualizar avaliação média

### Métodos Principais

```java
// Criar mercado
Mercado mercado = mercadoService.createMercado(createRequest, owner);

// Atualizar mercado
Mercado updated = mercadoService.updateMercado(mercadoId, updateRequest, user);

// Deletar mercado
mercadoService.deleteMercado(mercadoId, user);

// Obter por ID
Mercado mercado = mercadoService.getMercadoById(mercadoId);

// Listar todos
Page<MercadoResponse> page = mercadoService.getAllMercados(pageable);

// Buscar próximos (geolocalização)
List<Mercado> proximos = mercadoService.buscarPorProximidade(lat, lon, raio);

// Buscar por nome
Page<Mercado> results = mercadoService.buscarPorNome("Carrefour", pageable);

// Buscar por cidade
Page<Mercado> results = mercadoService.buscarPorCidade("São Paulo", pageable);

// Aprovar mercado (ADMIN)
mercadoService.aprovarMercado(mercadoId);

// Rejeitar mercado (ADMIN)
mercadoService.rejeitarMercado(mercadoId, "Documentos inválidos");

// Atualizar nota média (interno)
mercadoService.atualizarAvaliacaoMedia(mercadoId);
```

### Autorização
- Proprietário do mercado pode atualizar/deletar
- Admin pode fazer qualquer operação
- Usuários comuns podem apenas visualizar

---

## 4️⃣ AvaliacaoService

**Localização**: `com.netflix.mercado.service.AvaliacaoService`

### Responsabilidades
- Criar, atualizar e deletar avaliações
- Calcular estatísticas de avaliações
- Marcar avaliações como útil/inútil
- Validar duplicatas

### Métodos Principais

```java
// Criar avaliação
Avaliacao avaliacao = avaliacaoService.criarAvaliacao(createRequest, usuario);

// Atualizar avaliação
Avaliacao updated = avaliacaoService.atualizarAvaliacao(id, updateRequest, usuario);

// Deletar avaliação
avaliacaoService.deletarAvaliacao(id, usuario);

// Obter por ID
Avaliacao avaliacao = avaliacaoService.obterAvaliacaoPorId(id);

// Listar por mercado
Page<Avaliacao> page = avaliacaoService.obterAvaliacoesPorMercado(mercadoId, pageable);

// Listar por usuário
Page<Avaliacao> page = avaliacaoService.obterAvaliacoesPorUsuario(usuarioId, pageable);

// Calcular estatísticas
RatingStatsResponse stats = avaliacaoService.calcularEstatisticas(mercadoId);

// Marcar como útil
avaliacaoService.marcarComoUtil(avaliacaoId);

// Marcar como inútil
avaliacaoService.marcarComoInutil(avaliacaoId);

// Validar se usuário já avaliou
avaliacaoService.validarDuplicata(mercadoId, usuarioId);
```

### Regras de Negócio
- Rating deve estar entre 1-5 estrelas
- Usuário só pode avaliar uma vez por mercado
- Avaliações atualizam automaticamente a nota média do mercado

---

## 5️⃣ ComentarioService

**Localização**: `com.netflix.mercado.service.ComentarioService`

### Responsabilidades
- Gerenciar comentários em avaliações
- Suportar respostas (comentários aninhados)
- Gerenciar curtidas
- Moderar comentários (admin)

### Métodos Principais

```java
// Criar comentário
Comentario comentario = comentarioService.criarComentario(createRequest, usuario);

// Atualizar comentário
Comentario updated = comentarioService.atualizarComentario(id, updateRequest, usuario);

// Deletar comentário
comentarioService.deletarComentario(id, usuario);

// Obter por ID
Comentario comentario = comentarioService.obterComentarioPorId(id);

// Listar comentários da avaliação
Page<Comentario> page = comentarioService.obterComentariosPorAvaliacao(avaliacaoId, pageable);

// Obter respostas de um comentário
Page<Comentario> respostas = comentarioService.obterRespostas(comentarioPaiId, pageable);

// Responder a um comentário
Comentario resposta = comentarioService.responderComentario(comentarioPaiId, createRequest, usuario);

// Adicionar curtida
comentarioService.adicionarCurtida(comentarioId, usuario);

// Remover curtida
comentarioService.removerCurtida(comentarioId, usuario);

// Moderar comentário (ADMIN)
comentarioService.moderarComentario(comentarioId, true);
```

### Estrutura de Comentários
- Comentários raiz (em avaliações)
- Respostas (comentários filhos)
- Suporta múltiplos níveis de resposta

---

## 6️⃣ FavoritoService

**Localização**: `com.netflix.mercado.service.FavoritoService`

### Responsabilidades
- Adicionar/remover favoritos
- Consultar favoritos do usuário
- Contar favoritos
- Priorizar favoritos

### Métodos Principais

```java
// Adicionar aos favoritos
Favorito fav = favoritoService.adicionarFavorito(mercadoId, usuario);

// Remover dos favoritos
favoritoService.removerFavorito(mercadoId, usuario);

// Listar favoritos do usuário
Page<Favorito> page = favoritoService.obterFavoritosDUsuario(usuarioId, pageable);

// Verificar se é favorito
Boolean isFav = favoritoService.verificarFavorito(mercadoId, usuario);

// Contar favoritos do usuário
Long count = favoritoService.contarFavoritosDoUsuario(usuarioId);

// Contar favoritos do mercado
Long count = favoritoService.contarFavoritosDomercado(mercadoId);

// Toggle (adicionar/remover)
Boolean agora = favoritoService.toggleFavorito(mercadoId, usuario);

// Favoritos ordenados por prioridade
List<Favorito> favoritos = favoritoService.obterFavoritosComPrioridade(usuarioId);

// Definir prioridade
favoritoService.definirPrioridade(favoritoId, 8);
```

### Funcionalidades
- Sistema de prioridades (0-10)
- Toggle simplificado para frontend
- Histórico de data de adição

---

## 7️⃣ NotificacaoService

**Localização**: `com.netflix.mercado.service.NotificacaoService`

### Responsabilidades
- Criar e enviar notificações
- Consultar notificações do usuário
- Marcar como lida
- Limpar notificações antigas automaticamente

### Métodos Principais

```java
// Criar notificação
Notificacao notif = notificacaoService.criarNotificacao(createRequest);

// Enviar notificação
Notificacao notif = notificacaoService.enviarNotificacao(usuario, "Título", "Conteúdo", "AVALIACAO");

// Listar notificações do usuário
Page<Notificacao> page = notificacaoService.obterNotificacionesDoUsuario(usuarioId, pageable);

// Listar não lidas
Page<Notificacao> page = notificacaoService.obterNaoLidas(usuarioId, pageable);

// Marcar como lida
notificacaoService.marcarComoLida(notificacaoId);

// Marcar todas como lidas
notificacaoService.marcarTodosComoLido(usuario);

// Deletar notificação
notificacaoService.deletarNotificacao(notificacaoId);

// Contar não lidas
Long count = notificacaoService.contarNaoLidas(usuarioId);

// Limpar antigas (executado automaticamente)
// @Scheduled - 2:00 AM diariamente
```

### Tipos de Notificação
- AVALIACAO: Nova avaliação
- COMENTARIO: Novo comentário
- PROMOCAO: Promoção ativa
- SISTEMA: Notificações do sistema
- MERCADO: Atualizações do mercado

---

## 8️⃣ PromocaoService

**Localização**: `com.netflix.mercado.service.PromocaoService`

### Responsabilidades
- Criar, atualizar e deletar promoções
- Validar códigos promocionais
- Aplicar descontos
- Desativar promoções expiradas

### Métodos Principais

```java
// Criar promoção
Promocao promo = promocaoService.criarPromocao(createRequest, mercadoId, usuario);

// Atualizar promoção
Promocao updated = promocaoService.atualizarPromocao(id, updateRequest, usuario);

// Deletar promoção
promocaoService.deletarPromocao(id, usuario);

// Obter por ID
Promocao promo = promocaoService.obterPromocaoPorId(id);

// Listar do mercado
Page<Promocao> page = promocaoService.obterPromocoesDoMercado(mercadoId, pageable);

// Listar ativas
Page<Promocao> page = promocaoService.obterPromocoesAtivas(pageable);

// Validar código
ValidatePromocaoResponse result = promocaoService.validarCodigo("PROMO2024");

// Aplicar promoção (calcular desconto)
BigDecimal desconto = promocaoService.aplicarPromocao(promocaoId, new BigDecimal("100.00"));

// Verificar disponibilidade
promocaoService.verificarDisponibilidade(promocaoId);

// Desativar expiradas (automático)
// @Scheduled - 2:30 AM diariamente
```

### Tipos de Desconto
- PERCENTUAL: % de desconto
- FIXO: Valor fixo de desconto

### Exemplo de Uso
```java
// 1. Validar código
ValidatePromocaoResponse validation = promocaoService.validarCodigo("PROMO10");

// 2. Calcular desconto
BigDecimal desconto = promocaoService.aplicarPromocao(
    validation.getPromocaoId(), 
    new BigDecimal("200.00")
);

// 3. Aplicar desconto ao carrinho
BigDecimal valorFinal = new BigDecimal("200.00").subtract(desconto);
```

---

## 9️⃣ HorarioFuncionamentoService

**Localização**: `com.netflix.mercado.service.HorarioFuncionamentoService`

### Responsabilidades
- Gerenciar horários de funcionamento
- Verificar se mercado está aberto
- Calcular próxima abertura
- Validar horários

### Métodos Principais

```java
// Criar horário
HorarioFuncionamento hr = horarioFuncionamentoService.criarHorario(mercadoId, createRequest);

// Atualizar horário
HorarioFuncionamento updated = horarioFuncionamentoService.atualizarHorario(id, updateRequest);

// Deletar horário
horarioFuncionamentoService.deletarHorario(id);

// Listar horários do mercado
List<HorarioResponse> horarios = horarioFuncionamentoService.obterHorariosPorMercado(mercadoId);

// Verificar se está aberto
Boolean aberto = horarioFuncionamentoService.verificarSeEstaAberto(mercadoId);

// Próxima abertura
LocalDateTime proxima = horarioFuncionamentoService.obterProximaAbertura(mercadoId);

// Horários de um dia específico
List<HorarioFuncionamento> dias = horarioFuncionamentoService.obterHorariosDia(mercadoId, "MONDAY");

// Validar dados
horarioFuncionamentoService.validarHorarios(createRequest);
```

### Dias da Semana
- MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

### Exemplo de Uso
```java
CreateHorarioRequest request = new CreateHorarioRequest();
request.setDiaSemana("MONDAY");
request.setHoraAbertura(LocalTime.of(8, 0));
request.setHoraFechamento(LocalTime.of(22, 0));

horarioFuncionamentoService.criarHorario(mercadoId, request);
```

---

## 🔟 RefreshTokenService

**Localização**: `com.netflix.mercado.service.RefreshTokenService`

### Responsabilidades
- Criar refresh tokens
- Validar refresh tokens
- Renovar access tokens
- Revogar tokens
- Limpar tokens expirados

### Métodos Principais

```java
// Criar refresh token
RefreshToken token = refreshTokenService.criarRefreshToken(user);

// Obter token
RefreshToken token = refreshTokenService.obterRefreshToken(tokenString);

// Validar token
boolean isValid = refreshTokenService.validarRefreshToken(tokenString);

// Renovar access token
String novoAccessToken = refreshTokenService.renovarAccessToken(refreshTokenString);

// Revogar um token
refreshTokenService.revogarRefreshToken(tokenString);

// Revogar todos os tokens do usuário
refreshTokenService.revogarTodosOsTokensDoUsuario(usuario);

// Limpar expirados (automático)
// @Scheduled - 3:00 AM diariamente

// Tempo até expiração
Long minutos = refreshTokenService.obterTempoExpiracaoRestante(tokenString);
```

### Fluxo de Renovação
1. Cliente recebe refresh token no login
2. Quando access token expira, envia refresh token
3. Service valida refresh token
4. Gera novo access token
5. Cliente continua usando novo access token

---

## 1️⃣1️⃣ AuditLogService

**Localização**: `com.netflix.mercado.service.AuditLogService`

### Responsabilidades
- Registrar todas as ações de usuários
- Consultar histórico de auditoria
- Gerar relatórios
- Detectar atividades suspeitas

### Métodos Principais

```java
// Registrar ação simples
AuditLog log = auditLogService.registrarAcao(usuario, "CREATE", "MERCADO", mercadoId, "Mercado criado");

// Registrar ação com valores anteriores/novos
AuditLog log = auditLogService.registrarAcaoComValores(
    usuario, 
    "UPDATE", 
    "MERCADO", 
    mercadoId,
    "nome=Carrefour", 
    "nome=Carrefour Extra"
);

// Auditoria do usuário
Page<AuditLog> page = auditLogService.obterAuditoriaDoUsuario(usuarioId, pageable);

// Auditoria de uma entidade
List<AuditLog> logs = auditLogService.obterAuditoriaEntidade("MERCADO", mercadoId);

// Auditoria entre datas
Page<AuditLog> page = auditLogService.obterAuditoriaEntreData(dataInicio, dataFim, pageable);

// Auditoria por tipo de ação
Page<AuditLog> page = auditLogService.obterPorTipoAcao("UPDATE", pageable);

// Auditoria por tipo de entidade
Page<AuditLog> page = auditLogService.obterPorTipoEntidade("USER", pageable);

// Contar ações do usuário
Long count = auditLogService.contarAcoesDoUsuario(usuarioId);

// Contar ações de um tipo
Long count = auditLogService.contarAcoes("DELETE");

// Atividades suspeitas
List<AuditLog> suspeitas = auditLogService.obterAtividadeSuspeita(usuarioId, 30, 5);

// Relatório por tipo
List<Object> relatorio = auditLogService.obterRelatorioAtividadesPorTipo(dataInicio, dataFim);
```

### Tipos de Ação Padrão
- CREATE: Criação de entidade
- UPDATE: Atualização de entidade
- DELETE: Exclusão de entidade
- LOGIN: Login de usuário
- LOGOUT: Logout de usuário

---

## 🔐 Padrões de Segurança Implementados

### 1. **Autorização**
```java
// Verificar se é proprietário ou admin
if (!isOwnerOrAdmin(user, entity)) {
    throw new UnauthorizedException("Sem permissão");
}
```

### 2. **Validação de Negócio**
```java
// Lançar ValidationException para erros de regra
if (validation fails) {
    throw new ValidationException("Mensagem de erro");
}
```

### 3. **Transações**
```java
@Transactional
public void metodoQueModificaDados() { ... }

@Transactional(readOnly = true)
public void metodoQueApenasLe() { ... }
```

### 4. **Logging**
```java
@Slf4j
public class Service {
    private static final Logger log = ...;
    
    public void metodo() {
        log.info("Informação importante");
        log.warn("Aviso");
        log.error("Erro");
        log.debug("Debug info");
    }
}
```

---

## 📊 Integração Entre Services

```
UserService ────────────> AuditLogService
    ↓                            ↑
AuthService                      ↑
    ↓                            ↑
RefreshTokenService             ↑
                                ↑
MercadoService ─────────────────┤
    ↓                            ↑
AvaliacaoService ────────────────┤
    ↓                            ↑
ComentarioService ──────────────┤
    ↓                            ↑
FavoritoService ────────────────┤
    ↓                            ↑
PromocaoService ────────────────┤
    ↓                            ↑
HorarioFuncionamentoService ────┤
    ↓                            ↑
NotificacaoService ─────────────┘
```

---

## 🚀 Exemplo de Fluxo Completo

### 1. Registro e Login
```java
// 1. Registrar
RegisterRequest regRequest = new RegisterRequest(...);
JwtAuthenticationResponse response = authService.register(regRequest);
String accessToken = response.getAccessToken();

// 2. Usar access token em requisições
// Header: Authorization: Bearer {accessToken}

// 3. Quando expirar, renovar
String refreshToken = response.getRefreshToken();
JwtAuthenticationResponse newResponse = authService.refreshToken(refreshToken);
```

### 2. Criar e Avaliar Mercado
```java
// 1. Criar mercado
Mercado mercado = mercadoService.createMercado(createRequest, user);

// 2. Admin aprova
mercadoService.aprovarMercado(mercado.getId());

// 3. Usuário avalia
Avaliacao avaliacao = avaliacaoService.criarAvaliacao(createRequest, user);

// 4. Comentar em avaliação
Comentario comentario = comentarioService.criarComentario(createRequest, user2);

// 5. Responder comentário
comentarioService.responderComentario(comentario.getId(), respostaRequest, user);

// 6. Sistema atualiza nota
mercadoService.atualizarAvaliacaoMedia(mercado.getId());

// 7. Notificar usuário
notificacaoService.enviarNotificacao(user, "Nova resposta", "...", "COMENTARIO");

// 8. Registrar em auditoria (automático em cada operação)
```

---

## ✅ Checklist de Implementação

- [x] UserService com CRUD completo
- [x] AuthService com JWT e refresh tokens
- [x] MercadoService com buscas geolocalização
- [x] AvaliacaoService com validações
- [x] ComentarioService com aninhamento
- [x] FavoritoService com prioridades
- [x] NotificacaoService com scheduling
- [x] PromocaoService com códigos
- [x] HorarioFuncionamentoService com verificação
- [x] RefreshTokenService com revogação
- [x] AuditLogService com relatórios
- [x] Exceções customizadas
- [x] Logging completo (@Slf4j)
- [x] Transações apropriadas
- [x] Autorização verificada

---

**Desenvolvido com ❤️ para Netflix Mercados**
**Java 21 | Spring Boot 3.x | Banco de Dados Relacional**
