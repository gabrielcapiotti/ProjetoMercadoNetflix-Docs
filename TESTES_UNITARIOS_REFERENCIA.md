# Testes Unitários - Referência Rápida

## 📁 Localização dos Testes

```
src/test/java/com/netflix/mercado/service/
├── UserServiceTest.java
├── AuthServiceTest.java
└── MercadoServiceTest.java
```

## 🧪 Resumo dos Testes Criados

### 1. **UserServiceTest.java** (10 testes)

| Teste | Descrição |
|-------|-----------|
| `testCreateUserSuccess` | Criar usuário com dados válidos |
| `testCreateUserEmailDuplicate` | Validar erro: email duplicado |
| `testFindUserById` | Buscar usuário por ID |
| `testFindUserByIdNotFound` | Validar erro: usuário não encontrado |
| `testFindUserByEmail` | Buscar usuário por email |
| `testChangePasswordSuccess` | Alterar senha com sucesso |
| `testChangePasswordWrongOldPassword` | Validar erro: senha antiga incorreta |
| `testEnableTwoFactor` | Habilitar autenticação de dois fatores |
| `testVerifyEmail` | Verificar email do usuário |
| `testSoftDeleteUser` | Deletar usuário (soft delete) |

### 2. **AuthServiceTest.java** (8 testes)

| Teste | Descrição |
|-------|-----------|
| `testRegisterSuccess` | Registrar novo usuário com sucesso |
| `testRegisterEmailExists` | Validar erro: email já existe |
| `testLoginSuccess` | Fazer login com credenciais válidas |
| `testLoginWrongPassword` | Validar erro: senha incorreta |
| `testRefreshTokenSuccess` | Renovar access token com refresh token válido |
| `testRefreshTokenExpired` | Validar erro: refresh token expirado |
| `testValidateTokenSuccess` | Validar JWT token válido |
| `testValidateTokenInvalid` | Validar erro: JWT token inválido |

### 3. **MercadoServiceTest.java** (10 testes)

| Teste | Descrição |
|-------|-----------|
| `testCreateMercadoSuccess` | Criar mercado com dados válidos |
| `testCreateMercadoCNPJDuplicate` | Validar erro: CNPJ duplicado |
| `testUpdateMercadoSuccess` | Atualizar mercado com sucesso |
| `testDeleteMercadoSoftDelete` | Deletar mercado (soft delete) |
| `testFindMercadoById` | Buscar mercado por ID |
| `testBuscarPorProximidade` | Buscar mercados próximos com sucesso |
| `testBuscarPorProximidadeNoResults` | Validar: nenhum mercado encontrado |
| `testBuscarPorNome` | Buscar mercados por nome |
| `testAprovarMercadoAdmin` | Aprovar mercado como admin |
| `testAtualizarAvaliacaoMedia` | Atualizar avaliação média do mercado |

---

## 🛠️ Tecnologias Utilizadas

### Dependências (já configuradas em pom.xml)

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito (incluído no spring-boot-starter-test) -->

<!-- AssertJ (incluído no spring-boot-starter-test) -->
```

### Anotações Utilizadas

| Anotação | Biblioteca | Propósito |
|----------|-----------|----------|
| `@Test` | JUnit 5 | Marca método como teste |
| `@BeforeEach` | JUnit 5 | Executa antes de cada teste |
| `@Mock` | Mockito | Cria mock de dependência |
| `@InjectMocks` | Mockito | Injeta mocks automaticamente |
| `@ExtendWith(MockitoExtension.class)` | Mockito | Habilita Mockito na classe de teste |

### Métodos Mockito Utilizados

```java
// Configurar comportamento
when(mock.metodo()).thenReturn(valor);
when(mock.metodo(anyString())).thenReturn(valor);

// Verificar interações
verify(mock, times(1)).metodo();
verify(mock, never()).metodo();

// Argumentos
any(Class.class)
anyString()
anyLong()
```

### Assertions AssertJ Utilizados

```java
assertThat(valor).isNotNull();
assertThat(valor).isEqualTo(esperado);
assertThat(valor).isTrue();
assertThat(valor).isFalse();
assertThat(lista).hasSize(1);
assertThat(lista).isEmpty();
assertThat(lista).contains(elemento);

assertThatThrownBy(() -> metodo())
    .isInstanceOf(Exception.class)
    .hasMessageContaining("texto");
```

---

## ▶️ Executar os Testes

### Executar todos os testes
```bash
mvn test
```

### Executar apenas um arquivo de teste
```bash
mvn test -Dtest=UserServiceTest
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=MercadoServiceTest
```

### Executar um teste específico
```bash
mvn test -Dtest=UserServiceTest#testCreateUserSuccess
```

### Executar com cobertura de código
```bash
mvn clean test jacoco:report
```

---

## 📊 Estrutura de um Teste

Cada teste segue o padrão **AAA** (Arrange-Act-Assert):

```java
@Test
void testExample() {
    // ARRANGE - Preparar dados e mocks
    when(mock.metodo()).thenReturn(valor);
    
    // ACT - Executar ação testada
    ResultType resultado = service.metodo();
    
    // ASSERT - Validar resultado
    assertThat(resultado).isEqualTo(esperado);
    verify(mock, times(1)).metodo();
}
```

---

## ✅ Checklist para Novos Testes

- [ ] Use `@ExtendWith(MockitoExtension.class)` na classe
- [ ] Declare mocks com `@Mock`
- [ ] Injete com `@InjectMocks`
- [ ] Configure setup em `@BeforeEach`
- [ ] Siga padrão AAA (Arrange-Act-Assert)
- [ ] Use `assertThat()` do AssertJ
- [ ] Verifique chamadas com `verify()`
- [ ] Teste casos de sucesso E erro
- [ ] Use nomes descritivos: `testXxx`
- [ ] Documente com JavaDoc

---

## 🔗 Referências

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Assertions](https://assertj.github.io/assertj-core-features-highlight.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)

---

**Última atualização:** 30 de janeiro de 2026
