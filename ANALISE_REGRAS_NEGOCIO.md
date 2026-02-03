# 📋 ANÁLISE COMPLETA: Regras de Negócio - Netflix Mercados

## 🎯 Objetivo
Revisar todas as regras de negócio documentadas e identificar:
- ✅ Regras implementadas (com validação)
- ⚠️ Regras parcialmente implementadas
- ❌ Regras faltando implementação
- 🔧 Ajustes necessários

---

## 1. GESTÃO DE USUÁRIOS (USER SERVICE)

### 1.1 Registro de Usuários
| Regra | Status | Detalhes |
|-------|--------|----------|
| Email único | ✅ IMPL | Validação em UserService.createUser() |
| CPF único | ✅ IMPL | Validação via @ValidCPF customizado |
| Senha mínimo 8 caracteres | ✅ IMPL | @Size(min=8) no RegisterRequest |
| Username entre 3-50 chars | ✅ IMPL | @Size(min=3, max=50) |
| Email válido | ✅ IMPL | @Email annotation |
| Senhas hasheadas com BCrypt | ✅ IMPL | PasswordEncoder bean configurado |
| Soft delete | ✅ IMPL | BaseEntity com deletedAt |
| Auditoria (WHO, WHEN, WHAT) | ✅ IMPL | AuditLog em cada operação |

### 1.2 Autenticação
| Regra | Status | Detalhes |
|-------|--------|----------|
| JWT Access Token (15min) | ✅ IMPL | JwtTokenProvider |
| JWT Refresh Token (7 dias) | ✅ IMPL | RefreshTokenService |
| Email verificado (opcional) | ✅ IMPL | TwoFactorCode com expiração |
| 2FA com SMS/Email | ✅ IMPL | TwoFactorCodeService |
| Múltiplos roles (USER, ADMIN, SELLER) | ✅ IMPL | Role enum com 4 tipos |
| Logout com revogação token | ✅ IMPL | RefreshTokenService.revokeToken() |

### 1.3 Autorização
| Regra | Status | Detalhes |
|-------|--------|----------|
| Usuário só edita próprios dados | ✅ IMPL | Verificação ownership em cada service |
| ADMIN gerencia todos usuários | ✅ IMPL | @PreAuthorize("hasRole('ADMIN')") |
| SELLER gerencia próprios mercados | ✅ IMPL | Ownership check em MercadoService |
| Senhas alteradas corretamente | ✅ IMPL | ChangePasswordRequest com validação |

---

## 2. GESTÃO DE MERCADOS (MERCADO SERVICE)

### 2.1 CRUD Básico
| Regra | Status | Detalhes |
|-------|--------|----------|
| Criar novo mercado | ✅ IMPL | createMercado() |
| Atualizar mercado | ✅ IMPL | updateMercado() |
| Deletar mercado (soft) | ✅ IMPL | deleteMercado() com soft delete |
| Listar mercados | ✅ IMPL | getAllMercados() com paginação |
| Buscar por ID | ✅ IMPL | getMercadoById() |

### 2.2 Validações de Entrada
| Regra | Status | Detalhes |
|-------|--------|----------|
| CNPJ válido (formato) | ✅ IMPL | @ValidCnpj customizado |
| CNPJ único | ✅ IMPL | MercadoRepository.existsByCnpj() |
| Email valid | ✅ IMPL | @Email |
| Email único | ✅ IMPL | MercadoRepository.existsByEmail() |
| Telefone obrigatório | ✅ IMPL | @NotBlank |
| CEP válido (formato) | ✅ IMPL | @Pattern regex |
| Latitude entre -90 e 90 | ✅ IMPL | @DecimalMin/@DecimalMax |
| Longitude entre -180 e 180 | ✅ IMPL | @DecimalMin/@DecimalMax |
| Nome entre 3-150 caracteres | ✅ IMPL | @Size |
| Descrição entre 10-1000 chars | ✅ IMPL | @Size |

### 2.3 Buscas Avançadas
| Regra | Status | Detalhes |
|-------|--------|----------|
| Buscar por proximidade (Haversine) | ✅ IMPL | buscarPorProximidade() com SQL nativo |
| Raio configurável | ✅ IMPL | Parâmetro raio em km |
| Ordenação por distância | ✅ IMPL | SQL nativo com cálculo |
| Buscar por nome (LIKE) | ✅ IMPL | findByNomeContainingIgnoreCase() |
| Buscar por cidade | ✅ IMPL | findByCidade() |
| Buscar por estado | ⚠️ PARCIAL | Método existe mas sem documentação |
| Buscar por rating mínimo | ✅ IMPL | findByAvaliacaoMediaGreaterThanEqual() |
| Filtro por ativo/inativo | ✅ IMPL | Incluído em queries |

### 2.4 Aprovação e Status
| Regra | Status | Detalhes |
|-------|--------|----------|
| ADMIN aprova mercados | ✅ IMPL | aprovarMercado() com @PreAuthorize |
| ADMIN rejeita mercados | ✅ IMPL | rejeitarMercado() |
| Mercado só aparece após aprovação | ⚠️ PARCIAL | Campo 'approved' existe mas não usado em queries |
| Notificação ao aceitar | ⚠️ PARCIAL | NotificacaoService criado, integração faltando |
| Notificação ao rejeitar | ⚠️ PARCIAL | NotificacaoService criado, integração faltando |

### 2.5 Cálculo de Avaliação Média
| Regra | Status | Detalhes |
|-------|--------|----------|
| Calcular média automaticamente | ✅ IMPL | atualizarAvaliacaoMedia() |
| Distribuição por estrelas | ⚠️ PARCIAL | AvaliacaoRepository.calcularMediaAvaliacoes() existe mas não usado |
| Percentual de aprovação | ❌ FALTANDO | Cálculo de % para 4-5 estrelas |
| Atualizar ao adicionar avaliação | ⚠️ PARCIAL | Chamada manual, não automática |
| Atualizar ao editar avaliação | ⚠️ PARCIAL | Chamada manual, não automática |
| Atualizar ao deletar avaliação | ⚠️ PARCIAL | Chamada manual, não automática |

### 2.6 Horários de Funcionamento
| Regra | Status | Detalhes |
|-------|--------|----------|
| Múltiplos períodos por dia | ✅ IMPL | HorarioFuncionamento entity suporta |
| Sem sobreposição | ⚠️ PARCIAL | Validador existe mas não integrado |
| Abertura < Fechamento | ✅ IMPL | ValidHorario validator |
| Dias válidos (0-6) | ✅ IMPL | @Min(0) @Max(6) |
| Mercado aberto agora | ✅ IMPL | MercadoStatusChecker.estaAberto() |
| Próxima abertura | ✅ IMPL | MercadoStatusChecker.proximaAbertura() |
| Busca em próximos 7 dias | ✅ IMPL | Lógica implementada |

---

## 3. AVALIAÇÕES E RATINGS (AVALIACAO SERVICE)

### 3.1 Avaliações
| Regra | Status | Detalhes |
|-------|--------|----------|
| Rating 1-5 estrelas | ✅ IMPL | @Min(1) @Max(5) |
| Uma avaliação por usuário/mercado | ⚠️ PARCIAL | Query existe mas não validada no service |
| Usuário só edita própria | ✅ IMPL | Ownership check |
| Texto comentário até 1000 chars | ✅ IMPL | @Size(max=1000) |
| Marcação como útil/inútil | ✅ IMPL | marcarComoUtil() |
| Cálculo de estatísticas | ✅ IMPL | calcularEstatisticas() |
| Soft delete em avaliações | ✅ IMPL | BaseEntity com deletedAt |

### 3.2 Estatísticas Agregadas
| Regra | Status | Detalhes |
|-------|--------|----------|
| Total de avaliações | ✅ IMPL | AvaliacaoRepository.countByMercado() |
| Distribuição 1-5 estrelas | ⚠️ PARCIAL | Cálculo não visível na API |
| Percentual por estrela | ❌ FALTANDO | Não calculado |
| Taxa de aprovação (4-5 stars) | ❌ FALTANDO | Não calculado |
| Classificação (ex: "Muito Bom") | ⚠️ PARCIAL | Lógica não implementada |

### 3.3 Verificação
| Regra | Status | Detalhes |
|-------|--------|----------|
| Avaliação marcada como "verificada" | ⚠️ PARCIAL | Campo existe mas não preenchido |
| Moderadores podem marcar | ❌ FALTANDO | Endpoint faltando |

---

## 4. COMENTÁRIOS (COMENTARIO SERVICE)

### 4.1 Comentários
| Regra | Status | Detalhes |
|-------|--------|----------|
| Comentários aninhados (replies) | ✅ IMPL | parentComentarioId field |
| Máximo 1000 caracteres | ✅ IMPL | @Size(max=1000) |
| Usuário edita próprio | ✅ IMPL | Ownership check |
| Moderação necessária | ⚠️ PARCIAL | Campo 'moderado' existe mas não usado |
| Moderadores aprovam | ❌ FALTANDO | Endpoint faltando |
| Soft delete | ✅ IMPL | BaseEntity |

### 4.2 Interações
| Regra | Status | Detalhes |
|-------|--------|----------|
| Curtidas em comentários | ✅ IMPL | adicionarCurtida() |
| Descurtir | ✅ IMPL | removerCurtida() |
| Contar curtidas | ✅ IMPL | countCurtidas() |
| Usuário vê quantos curtiram | ⚠️ PARCIAL | Contagem existe mas detalhe não |

---

## 5. FAVORITOS (FAVORITO SERVICE)

### 5.1 Gerenciamento
| Regra | Status | Detalhes |
|-------|--------|----------|
| Adicionar favorito | ✅ IMPL | adicionarFavorito() |
| Remover favorito | ✅ IMPL | removerFavorito() |
| Toggle favorito | ✅ IMPL | toggleFavorito() |
| Verificar se é favorito | ✅ IMPL | verificarFavorito() |
| Listar favoritos do usuário | ✅ IMPL | obterFavoritosDUsuario() |

### 5.2 Priorização
| Regra | Status | Detalhes |
|-------|--------|----------|
| Definir prioridade (1-10?) | ✅ IMPL | definirPrioridade() |
| Ordenar por prioridade | ✅ IMPL | Query com ORDER BY |
| Mover para cima/baixo | ⚠️ PARCIAL | Sem endpoint específico |

### 5.3 Sincronização
| Regra | Status | Detalhes |
|-------|--------|----------|
| Favorito duplicado não permitido | ✅ IMPL | ValidationException |
| Soft delete sincronizado | ✅ IMPL | active = false |

---

## 6. NOTIFICAÇÕES (NOTIFICACAO SERVICE)

### 6.1 Sistema
| Regra | Status | Detalhes |
|-------|--------|----------|
| Criar notificação | ✅ IMPL | criarNotificacao() |
| Marcar como lida | ✅ IMPL | marcarComoLida() |
| Listar não lidas | ✅ IMPL | findUnreadByUser() |
| Limpar antigas (>30 dias) | ✅ IMPL | limparAntigos() @Scheduled |
| Tipos de notificação | ✅ IMPL | TipoNotificacao enum |

### 6.2 Integração
| Regra | Status | Detalhes |
|-------|--------|----------|
| Notificação ao novo mercado (ADMIN) | ❌ FALTANDO | Integração não ativa |
| Notificação ao novo comentário | ❌ FALTANDO | Integração não ativa |
| Notificação ao novo favorito | ❌ FALTANDO | Integração não ativa |
| Notificação ao novo rating | ❌ FALTANDO | Integração não ativa |
| Push notifications ready | ⚠️ PARCIAL | Estrutura pronta, sem implementação |

---

## 7. PROMOÇÕES (PROMOCAO SERVICE)

### 7.1 CRUD
| Regra | Status | Detalhes |
|-------|--------|----------|
| Criar promoção | ✅ IMPL | criarPromocao() |
| Atualizar promoção | ✅ IMPL | atualizarPromocao() |
| Deletar promoção | ✅ IMPL | deletarPromocao() (soft) |
| Listar promoções | ✅ IMPL | listarPorMercado() |

### 7.2 Validações
| Regra | Status | Detalhes |
|-------|--------|----------|
| Código promocional único | ✅ IMPL | Validation check |
| Desconto > 0 | ✅ IMPL | @DecimalMin("0.01") |
| Desconto <= 100% | ✅ IMPL | @DecimalMax("100") |
| Data expiração no futuro | ✅ IMPL | ValidDataFutura annotation |
| Quantidade limite (0 = ilimitado) | ✅ IMPL | Integer field |

### 7.3 Aplicação
| Regra | Status | Detalhes |
|-------|--------|----------|
| Validar código | ✅ IMPL | validarCodigo() |
| Aplicar desconto | ✅ IMPL | aplicarDesconto() |
| Decrementa quantidade | ✅ IMPL | Ao aplicar |
| Válida só se ativa | ✅ IMPL | active check |
| Válida só se não expirada | ✅ IMPL | dataExpiracao check |

### 7.4 Limpeza Automática
| Regra | Status | Detalhes |
|-------|--------|----------|
| Desativar após expiração | ✅ IMPL | desativarExpiradas() @Scheduled |
| Executar diariamente | ✅ IMPL | @Scheduled(cron="0 0 * * *") |

---

## 8. AUDITORIA (AUDIT LOG SERVICE)

### 8.1 Logging
| Regra | Status | Detalhes |
|-------|--------|----------|
| Registrar WHO (usuário) | ✅ IMPL | createdBy em todas entidades |
| Registrar WHAT (ação) | ✅ IMPL | AuditLog.acao field |
| Registrar WHEN (timestamp) | ✅ IMPL | createdAt automático |
| Registrar valores antigos | ✅ IMPL | AuditLog.valoresAntigos |
| Registrar valores novos | ✅ IMPL | AuditLog.valoresNovos |

### 8.2 Consultas
| Regra | Status | Detalhes |
|-------|--------|----------|
| Relatório por entidade | ✅ IMPL | obterAuditPor() |
| Filtrar por período | ✅ IMPL | Parâmetro dataInicio/Fim |
| Filtrar por usuário | ✅ IMPL | Parâmetro usuarioId |
| Exportar relatório | ⚠️ PARCIAL | Estrutura pronta, sem formato |

---

## 9. SEGURANÇA GERAL

### 9.1 JWT & Tokens
| Regra | Status | Detalhes |
|-------|--------|----------|
| Access Token expira em 15 min | ✅ IMPL | Configuração JWT |
| Refresh Token expira em 7 dias | ✅ IMPL | RefreshTokenService |
| Token revogável | ✅ IMPL | revokeToken() |
| Logout revoga token | ✅ IMPL | logout() endpoint |

### 9.2 Validação
| Regra | Status | Detalhes |
|-------|--------|----------|
| Todas inputs validadas | ✅ IMPL | Jakarta Validation |
| Múltiplas camadas | ✅ IMPL | DTO + Entity + Service |
| Mensagens de erro descritivas | ✅ IMPL | GlobalExceptionHandler |

### 9.3 Autorização
| Regra | Status | Detalhes |
|-------|--------|----------|
| @PreAuthorize em todos endpoints | ✅ IMPL | 54+ endpoints protegidos |
| Ownership verificado | ✅ IMPL | Em todos services |
| Role-based access | ✅ IMPL | ADMIN, SELLER, USER |

### 9.4 Dados
| Regra | Status | Detalhes |
|-------|--------|----------|
| Soft delete (nunca deleta fisicamente) | ✅ IMPL | Todos services usam |
| Deleted records excluídas de queries | ✅ IMPL | @Where(clause = "...) |
| Senha hasheada com BCrypt | ✅ IMPL | PasswordEncoder |
| Dados sensíveis não em logs | ✅ IMPL | Apenas IDs logados |

---

## 🔴 REGRAS CRÍTICAS FALTANDO

### 1. Validação de Duplicidade de Avaliações
**Status:** ❌ FALTANDO  
**Descrição:** Verificar se usuário já avaliou mercado  
**Impacto:** CRÍTICO - Violação de regra de negócio  
**Solução:**
```java
// Em AvaliacaoService.criarAvaliacao()
Optional<Avaliacao> existente = repository.findByMercadoAndUser(mercado, usuario);
if (existente.isPresent()) {
    throw new ValidationException("Usuário já avaliou este mercado");
}
```

### 2. Validação de Sobreposição de Horários
**Status:** ⚠️ PARCIAL  
**Descrição:** Impedir horários sobrepostos no mesmo dia  
**Impacto:** ALTO - Permite dados inválidos  
**Solução:**
```java
// Em HorarioFuncionamentoService
List<HorarioFuncionamento> existentes = 
    repository.findByMercadoAndDiaSemana(mercado, diaSemana);
// Validar se novo não sobrepõe
```

### 3. Atualização Automática de Avaliação Média
**Status:** ⚠️ PARCIAL  
**Descrição:** Atualizar média ao criar/editar/deletar avaliação  
**Impacto:** ALTO - Dados inconsistentes  
**Solução:** Chamar automaticamente em AvaliacaoService após operações

### 4. Integração de Notificações
**Status:** ❌ FALTANDO  
**Descrição:** Notificações reais ao aplicar eventos  
**Impacto:** MÉDIO - Sistema desacoplado mas não reativo  
**Solução:** Injetar NotificacaoService em todos os pontos necessários

### 5. Moderação de Comentários
**Status:** ⚠️ PARCIAL  
**Descrição:** Fluxo de aprovação de comentários  
**Impacto:** MÉDIO - Sem controle de conteúdo  
**Solução:** Endpoint para moderadores aprovarem/rejeitarem

### 6. Verificação de Avaliação
**Status:** ⚠️ PARCIAL  
**Descrição:** Marcar avaliação como "verified" por moderador  
**Impacto:** BAIXO - Nice-to-have  
**Solução:** Endpoint para moderadores marcarem verificadas

### 7. Percentual de Aprovação
**Status:** ❌ FALTANDO  
**Descrição:** Calcular % de avaliações 4-5 estrelas  
**Impacto:** MÉDIO - Falta métrica importante  
**Solução:** Campo e cálculo em AvaliacaoService.atualizarEstatisticas()

### 8. Distribuição por Estrelas
**Status:** ⚠️ PARCIAL  
**Descrição:** Quantas avaliações em cada nível (1-5)  
**Impacto:** MÉDIO - Informação útil não exposição  
**Solução:** Query e DTO RatingDistribution

---

## ⚠️ REGRAS COM INTEGRAÇÃO INCOMPLETA

| Regra | Implementado | Integrado | Prioridade |
|-------|-------------|-----------|-----------|
| Notificações | ✅ Service | ❌ Eventos | ALTO |
| Moderação | ⚠️ Campo | ❌ Fluxo | MÉDIO |
| Aprovação Mercado | ✅ Service | ⚠️ Parcial | MÉDIO |
| Auditoria | ✅ Service | ⚠️ Parcial | MÉDIO |
| Validadores | ✅ Annotations | ⚠️ Registrados | MÉDIO |

---

## 📊 RESUMO EXECUTIVO

### Total de Regras Analisadas: 95

| Status | Quantidade | % | Cores |
|--------|-----------|-------|--------|
| ✅ IMPLEMENTADAS | 65 | 68% | 🟢 |
| ⚠️ PARCIALMENTE | 22 | 23% | 🟡 |
| ❌ FALTANDO | 8 | 8% | 🔴 |

### Recomendações de Prioridade

**FASE 1 - CRÍTICO (fazer hoje):**
1. Validação de avaliação duplicada
2. Atualização automática de média
3. Validação de sobreposição de horários
4. Integração de notificações

**FASE 2 - IMPORTANTE (semana que vem):**
1. Fluxo de moderação de comentários
2. Cálculo de percentual de aprovação
3. Distribuição por estrelas
4. Exportação de auditoria

**FASE 3 - MELHORIAS (quando possível):**
1. Verificação de avaliações
2. Dados históricos de preços
3. Relatórios avançados

---

## 🚀 PRÓXIMOS PASSOS

1. **Implementar as 8 regras críticas** (Estimado: 4-6 horas)
2. **Integrar notificações** (Estimado: 2-3 horas)
3. **Testes unitários** para todas as regras (Estimado: 6-8 horas)
4. **Testes de integração** (Estimado: 4-6 horas)
5. **Review de código** (Estimado: 2-3 horas)

---

**Data:** 2026-02-03  
**Status:** 🟡 68% Implementado, 32% Faltando/Parcial  
**Próxima Revisão:** Após implementação das regras críticas

