# ⚡ QUICK REFERENCE - Regras de Negócio

## 🎯 STATUS GERAL EM 10 SEGUNDOS

```
╔════════════════════════════════════════════╗
║  NETFLIX MERCADOS - REGRAS DE NEGÓCIO    ║
╠════════════════════════════════════════════╣
║                                            ║
║  TOTAL: 95 regras                         ║
║  ✅ 65 (68%)  - Implementadas            ║
║  ⚠️  22 (23%) - Parciais                  ║
║  ❌ 8  (8%)   - Faltando                  ║
║                                            ║
║  STATUS: 🟡 76% Completo                  ║
║  PRONTO PRODUÇÃO: ❌ (24% falta)         ║
║                                            ║
╚════════════════════════════════════════════╝
```

---

## 🔴 4 REGRAS CRÍTICAS (FAZER HOJE - 2h 45min)

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃ 1️⃣  VALIDAÇÃO DUPLICIDADE AVALIAÇÕES    ┃
┃    STATUS: ❌ Faltando                    ┃
┃    ARQUIVO: AvaliacaoService.java         ┃
┃    TEMPO: 30 min                          ┃
┃    IMPACTO: 🔴 CRÍTICO                    ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃ 2️⃣  ATUALIZAÇÃO AUTOMÁTICA MÉDIA         ┃
┃    STATUS: ❌ Faltando                    ┃
┃    ARQUIVO: AvaliacaoService.java         ┃
┃    TEMPO: 30 min                          ┃
┃    IMPACTO: 🔴 CRÍTICO                    ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃ 3️⃣  VALIDAÇÃO SOBREPOSIÇÃO HORÁRIOS     ┃
┃    STATUS: ❌ Faltando                    ┃
┃    ARQUIVO: HorarioFuncionamentoService   ┃
┃    TEMPO: 45 min                          ┃
┃    IMPACTO: 🔴 CRÍTICO                    ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃ 4️⃣  INTEGRAÇÃO NOTIFICAÇÕES              ┃
┃    STATUS: ❌ Faltando (serviço existe)  ┃
┃    ARQUIVO: MercadoService.java           ┃
┃    TEMPO: 60 min                          ┃
┃    IMPACTO: 🔴 CRÍTICO                    ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

---

## 📊 PROGRESSO POR MÓDULO

### 1. Usuários
```
████████████████████ 15/15 (100%) ✅
Ação: Nenhuma
```

### 2. Mercados
```
███████████████████░ 26/31 (84%) 🟡
Ação: Validar sobreposição + notificações
```

### 3. Avaliações
```
███████████░░░░░░░░░ 12/17 (71%) 🔴
Ação: Duplicidade + média + distribuição
```

### 4. Comentários
```
████████████░░░░░░░░ 8/10 (80%) 🟡
Ação: Moderação
```

### 5. Favoritos
```
█████████░░░░░░░░░░░ 9/10 (90%) 🟡
Ação: Nenhuma crítica
```

### 6. Notificações
```
█████░░░░░░░░░░░░░░░ 5/10 (50%) 🔴
Ação: Integrar com eventos
```

### 7. Promoções
```
████████████████████ 16/16 (100%) ✅
Ação: Nenhuma
```

### 8. Auditoria
```
█████████░░░░░░░░░░░ 8/9 (89%) 🟡
Ação: Exportação CSV/Excel
```

### 9. Segurança
```
████████████████████ 14/14 (100%) ✅
Ação: Nenhuma
```

---

## 📅 CRONOGRAMA

```
┌─────────────────────────────────────────────┐
│ HOJE - FASE 1 (CRÍTICO)                    │
├─────────────────────────────────────────────┤
│ 09:00 ✓ Duplicidade avaliações    [30min] │
│ 09:30 ✓ Atualização média auto    [30min] │
│ 10:00 ✓ Sobreposição horários     [45min] │
│ 10:45 ✓ Integração notificações   [60min] │
│ 11:45 ✓ Testes + deploy           [15min] │
│ Total: 2h 45min                            │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ SEMANA - FASE 2 (IMPORTANTE)               │
├─────────────────────────────────────────────┤
│ ✓ Percentual aprovação         [30min]    │
│ ✓ Distribuição por estrelas    [45min]    │
│ ✓ Fluxo moderação comentários  [90min]    │
│ ✓ Exportação auditoria         [60min]    │
│ Total: 3h 45min                            │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ DEPOIS - FASE 3 (MELHORIAS)                │
├─────────────────────────────────────────────┤
│ ◯ Mover favorito cima/baixo    [30min]    │
│ ◯ Verificação avaliações       [45min]    │
│ ◯ Push notifications Firebase  [120min]   │
│ Total: 3h 15min                            │
└─────────────────────────────────────────────┘
```

---

## 🔍 CHECKLIST RÁPIDO

### Implementar Hoje
- [ ] Validação duplicidade (AvaliacaoService.criarAvaliacao)
- [ ] Atualizar média (criarAvaliacao + atualizarAvaliacao + deletarAvaliacao)
- [ ] Validar sobreposição (HorarioFuncionamentoService.criarHorario)
- [ ] Notificar novo mercado (MercadoService.criarMercado)
- [ ] Notificar aprovação (MercadoService.aprovarMercado)
- [ ] Notificar rejeição (MercadoService.rejeitarMercado)
- [ ] Notificar avaliação (AvaliacaoService.criarAvaliacao)

### Testes Mínimos
- [ ] Teste: Avaliação duplicada recusada
- [ ] Teste: Média recalcula ao editar
- [ ] Teste: Horários sobrepostos recusados
- [ ] Teste: Notificações criadas

### Deploy Hoje
- [ ] Compilação sem erros (mvn clean compile)
- [ ] Testes passando (mvn test)
- [ ] Deploy para QA

---

## 📁 ARQUIVOS RELEVANTES

```
Implementação:
├─ src/main/java/.../service/AvaliacaoService.java
├─ src/main/java/.../service/HorarioFuncionamentoService.java
├─ src/main/java/.../service/MercadoService.java
├─ src/main/java/.../service/NotificacaoService.java
├─ src/main/java/.../entity/HorarioFuncionamento.java
└─ src/main/java/.../repository/AvaliacaoRepository.java

Testes:
├─ src/test/java/.../service/AvaliacaoServiceTest.java
├─ src/test/java/.../service/HorarioFuncionamentoServiceTest.java
├─ src/test/java/.../service/MercadoServiceTest.java
└─ src/test/java/.../service/NotificacaoServiceTest.java

Documentação:
├─ ANALISE_REGRAS_NEGOCIO.md (COMPLETO)
├─ IMPLEMENTACAO_REGRAS_CRITICAS.md (CÓDIGO PRONTO)
├─ MATRIZ_VISUAL_REGRAS.md (DASHBOARD)
├─ RESUMO_EXECUTIVO_REGRAS.md (PLANO)
└─ INDICE_ANALISE_REGRAS.md (NAVEGAÇÃO)
```

---

## ⚠️ CUIDADOS

```
NÃO FAÇA:
❌ Implementar sem ler IMPLEMENTACAO_REGRAS_CRITICAS.md
❌ Colocar em produção sem testes
❌ Atualizar média manualmente (deve ser automático)
❌ Permitir avaliações duplicadas
❌ Permitir horários sobrepostos

SEMPRE FAÇA:
✅ Rodar mvn clean compile antes de commitar
✅ Rodar mvn test para validar
✅ Verificar a documentação antes de implementar
✅ Pedir review para mudanças em AvaliacaoService
✅ Notificar o PO após cada implementação
```

---

## 💡 DICAS DE IMPLEMENTAÇÃO

```
1. VALIDAÇÃO DUPLICIDADE
   Query: findByMercadoAndCriadoPor(mercado, usuario)
   Se existe → throw ValidationException
   
2. ATUALIZAÇÃO MÉDIA
   Chamar method private atualizarAvaliacaoMedia(Mercado)
   Em: criarAvaliacao, atualizarAvaliacao, deletarAvaliacao
   
3. SOBREPOSIÇÃO HORÁRIOS
   Método: temSobreposicao(ab1, fe1, ab2, fe2)
   Lógica: ab1 < fe2 && ab2 < fe1 = sobreposto
   
4. NOTIFICAÇÕES
   Injetar: @Autowired private NotificacaoService notificService;
   Chamar: notificService.criarNotificacao(usuario, tipo, mensagem);
```

---

## 📞 REFERÊNCIAS RÁPIDAS

| Preciso de... | Consulte |
|---------------|----------|
| Entender TUDO | ANALISE_REGRAS_NEGOCIO.md |
| Código pronto | IMPLEMENTACAO_REGRAS_CRITICAS.md |
| Ver dashboard | MATRIZ_VISUAL_REGRAS.md |
| Plano de ação | RESUMO_EXECUTIVO_REGRAS.md |
| Navegar docs | INDICE_ANALISE_REGRAS.md |
| Rápida consulta | QUICK_REFERENCE.md (este arquivo) |

---

## 🎯 META HOJE

```
06:00 → Fim: 92% de regras implementadas
        Com: Todas as 4 críticas funcionando
        Teste: Suite passando
        Deploy: Pronto para QA
```

---

**Último Atualizado:** 2024-02-03  
**Status:** 🟡 PRONTO PARA COMEÇAR

