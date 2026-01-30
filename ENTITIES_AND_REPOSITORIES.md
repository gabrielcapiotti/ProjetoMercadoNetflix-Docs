# Entidades e Repositórios - Netflix Mercados

## Índice
1. [Entidades Base](#entidades-base)
2. [Entidades de Usuário](#entidades-de-usuário)
3. [Entidades de Mercado](#entidades-de-mercado)
4. [Entidades de Avaliações](#entidades-de-avaliações)
5. [Entidades de Segurança](#entidades-de-segurança)
6. [Repositórios](#repositórios)

---

## Entidades Base

### 1. BaseEntity.java
Classe abstrata para auditoria em todas as entidades.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @SoftDelete
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        this.deleted = false;
        this.version = 0L;
    }

    public void markAsDeleted(String deletedBy) {
        this.deleted = true;
        this.updatedBy = deletedBy;
    }

    public void unmarkAsDeleted(String updatedBy) {
        this.deleted = false;
        this.updatedBy = updatedBy;
    }
}
```

---

## Entidades de Usuário

### 2. Role.java
Papéis de usuário no sistema.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name", unique = true)
})
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NaturalId
    @NotBlank(message = "O nome da role não pode estar em branco")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public enum RoleName {
        USER, ADMIN, SELLER, MODERATOR
    }
}
```

### 3. User.java
Usuários do sistema.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_cpf", columnList = "cpf", unique = true),
    @Index(name = "idx_user_active", columnList = "active")
})
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NaturalId
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", 
             message = "O CPF deve estar no formato XXX.XXX.XXX-XX")
    @Column(name = "cpf", unique = true, length = 14)
    private String cpf;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(name = "last_login")
    private java.time.LocalDateTime lastLogin;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        indexes = {
            @Index(name = "idx_user_roles_user", columnList = "user_id"),
            @Index(name = "idx_user_roles_role", columnList = "role_id")
        }
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorito> favoritos = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notificacao> notificacoes = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}
```

---

## Entidades de Mercado

### 4. Mercado.java
Informações dos mercados.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mercados", indexes = {
    @Index(name = "idx_mercado_email", columnList = "email", unique = true),
    @Index(name = "idx_mercado_cnpj", columnList = "cnpj", unique = true),
    @Index(name = "idx_mercado_ativo", columnList = "ativo"),
    @Index(name = "idx_mercado_avaliacao", columnList = "avaliacao_media"),
    @Index(name = "idx_mercado_coordenadas", columnList = "latitude,longitude")
})
public class Mercado extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O nome do mercado é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres")
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", 
             message = "O CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX")
    @Column(name = "cnpj", unique = true, length = 18)
    private String cnpj;

    @Email(message = "O email deve ser válido")
    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório")
    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "complemento", length = 255)
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato XXXXX-XXX")
    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @NotNull(message = "A latitude é obrigatória")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @NotNull(message = "A longitude é obrigatória")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "foto_principal_url", length = 500)
    private String fotoPrincipalUrl;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(name = "avaliacao_media", nullable = false, precision = 3, scale = 2)
    private BigDecimal avaliacaoMedia = BigDecimal.ZERO;

    @Column(name = "total_avaliacoes", nullable = false)
    private Long totalAvaliacoes = 0L;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorito> favoritos = new HashSet<>();

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Promocao> promocoes = new HashSet<>();

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HorarioFuncionamento> horariosFuncionamento = new HashSet<>();

    public void atualizarAvaliacaoMedia(BigDecimal novaAvaliacao) {
        if (this.totalAvaliacoes == 0) {
            this.avaliacaoMedia = novaAvaliacao;
        } else {
            BigDecimal soma = this.avaliacaoMedia.multiply(BigDecimal.valueOf(this.totalAvaliacoes));
            soma = soma.add(novaAvaliacao);
            this.avaliacaoMedia = soma.divide(BigDecimal.valueOf(this.totalAvaliacoes + 1), 2, java.math.RoundingMode.HALF_UP);
        }
        this.totalAvaliacoes++;
    }

    public double calcularDistancia(BigDecimal latitude2, BigDecimal longitude2) {
        // Haversine formula
        double lat1 = this.latitude.doubleValue();
        double lon1 = this.longitude.doubleValue();
        double lat2 = latitude2.doubleValue();
        double lon2 = longitude2.doubleValue();

        double R = 6371; // Raio da Terra em km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
```

---

## Entidades de Avaliações

### 5. Avaliacao.java
Avaliações dos mercados.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avaliacoes", indexes = {
    @Index(name = "idx_avaliacao_mercado", columnList = "mercado_id"),
    @Index(name = "idx_avaliacao_usuario", columnList = "usuario_id"),
    @Index(name = "idx_avaliacao_estrelas", columnList = "estrelas"),
    @Index(name = "idx_avaliacao_criada_em", columnList = "created_at")
})
public class Avaliacao extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "O usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_avaliacao_usuario"))
    private User user;

    @NotNull(message = "O mercado é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_avaliacao_mercado"))
    private Mercado mercado;

    @NotNull(message = "A classificação é obrigatória")
    @Min(value = 1, message = "A classificação deve ser entre 1 e 5 estrelas")
    @Max(value = 5, message = "A classificação deve ser entre 1 e 5 estrelas")
    @Column(name = "estrelas", nullable = false)
    private Integer estrelas;

    @Size(min = 10, max = 1000, message = "O comentário deve ter entre 10 e 1000 caracteres")
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado = false;

    @Column(name = "uteis", nullable = false)
    private Long uteis = 0L;

    @Column(name = "inutils", nullable = false)
    private Long inutils = 0L;

    @OneToMany(mappedBy = "avaliacaoPai", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();

    public Double calcularUtilidade() {
        Long total = uteis + inutils;
        if (total == 0) return 0.0;
        return (double) uteis / total * 100;
    }
}
```

### 6. Comentario.java
Comentários nas avaliações (suporta respostas aninhadas).

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comentarios", indexes = {
    @Index(name = "idx_comentario_avaliacao", columnList = "avaliacao_id"),
    @Index(name = "idx_comentario_usuario", columnList = "usuario_id"),
    @Index(name = "idx_comentario_pai", columnList = "comentario_pai_id"),
    @Index(name = "idx_comentario_criada_em", columnList = "created_at")
})
public class Comentario extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    @Size(min = 5, max = 1000, message = "O comentário deve ter entre 5 e 1000 caracteres")
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "avaliacao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comentario_avaliacao"))
    private Avaliacao avaliacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comentario_usuario"))
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_pai_id", foreignKey = @ForeignKey(name = "fk_comentario_pai"))
    private Comentario comentarioPai;

    @OneToMany(mappedBy = "comentarioPai", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> respostas = new HashSet<>();

    @Column(name = "curtidas", nullable = false)
    private Long curtidas = 0L;

    @Column(name = "moderado", nullable = false)
    private Boolean moderado = false;

    public void adicionarResposta(Comentario resposta) {
        respostas.add(resposta);
        resposta.comentarioPai = this;
    }

    public void removerResposta(Comentario resposta) {
        respostas.remove(resposta);
        resposta.comentarioPai = null;
    }

    public boolean ehResposta() {
        return comentarioPai != null;
    }
}
```

### 7. Favorito.java
Favoritos do usuário.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favoritos", indexes = {
    @Index(name = "idx_favorito_usuario", columnList = "usuario_id"),
    @Index(name = "idx_favorito_mercado", columnList = "mercado_id"),
    @Index(name = "idx_favorito_unico", columnList = "usuario_id,mercado_id", unique = true),
    @Index(name = "idx_favorito_criada_em", columnList = "created_at")
},
uniqueConstraints = @UniqueConstraint(name = "uk_usuario_mercado", columnNames = {"usuario_id", "mercado_id"}))
public class Favorito extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorito_usuario"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_favorito_mercado"))
    private Mercado mercado;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "prioridade", nullable = false)
    private Integer prioridade = 0;
}
```

### 8. Notificacao.java
Notificações do usuário.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificacoes", indexes = {
    @Index(name = "idx_notificacao_usuario", columnList = "usuario_id"),
    @Index(name = "idx_notificacao_lida", columnList = "lida"),
    @Index(name = "idx_notificacao_tipo", columnList = "tipo"),
    @Index(name = "idx_notificacao_criada_em", columnList = "created_at")
})
public class Notificacao extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public enum TipoNotificacao {
        PROMOCAO("Promoção"),
        AVALIACAO("Avaliação"),
        COMENTARIO("Comentário"),
        FAVORITO("Favorito"),
        SEGURANCA("Segurança"),
        SISTEMA("Sistema");

        private final String descricao;

        TipoNotificacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notificacao_usuario"))
    private User usuario;

    @NotBlank(message = "O título é obrigatório")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "O conteúdo é obrigatório")
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoNotificacao tipo;

    @Column(name = "lida", nullable = false)
    private Boolean lida = false;

    @Column(name = "url_acao", length = 500)
    private String urlAcao;

    @Column(name = "data_leitura")
    private java.time.LocalDateTime dataLeitura;

    public void marcarComoLida() {
        this.lida = true;
        this.dataLeitura = java.time.LocalDateTime.now();
    }

    public void marcarComoNaoLida() {
        this.lida = false;
        this.dataLeitura = null;
    }
}
```

### 9. Promocao.java
Promoções dos mercados.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promocoes", indexes = {
    @Index(name = "idx_promocao_mercado", columnList = "mercado_id"),
    @Index(name = "idx_promocao_codigo", columnList = "codigo", unique = true),
    @Index(name = "idx_promocao_ativa", columnList = "ativa"),
    @Index(name = "idx_promocao_validade", columnList = "data_validade")
})
public class Promocao extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O código é obrigatório")
    @Size(min = 3, max = 50, message = "O código deve ter entre 3 e 50 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "O desconto é obrigatório")
    @DecimalMin("0.01")
    @DecimalMax("100.00")
    @Column(name = "percentual_desconto", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    @DecimalMin("0.0")
    @Column(name = "valor_desconto_maximo", precision = 10, scale = 2)
    private BigDecimal valorDescontoMaximo;

    @DecimalMin("0.0")
    @Column(name = "valor_minimo_compra", precision = 10, scale = 2)
    private BigDecimal valorMinimoCompra = BigDecimal.ZERO;

    @NotNull(message = "A data de válidade é obrigatória")
    @Column(name = "data_validade", nullable = false)
    private LocalDateTime dataValidade;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "max_utilizacoes", nullable = false)
    private Long maxUtilizacoes = Long.MAX_VALUE;

    @Column(name = "utilizacoes_atuais", nullable = false)
    private Long utilizacoesAtuais = 0L;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_promocao_mercado"))
    private Mercado mercado;

    public boolean ehValida() {
        LocalDateTime agora = LocalDateTime.now();
        boolean validaPorData = (dataInicio == null || !agora.isBefore(dataInicio)) && 
                                agora.isBefore(dataValidade);
        boolean validaPorUtilizacao = utilizacoesAtuais < maxUtilizacoes;
        return ativa && validaPorData && validaPorUtilizacao;
    }

    public BigDecimal calcularDesconto(BigDecimal valorCompra) {
        if (!ehValida()) {
            return BigDecimal.ZERO;
        }
        
        if (valorCompra.compareTo(valorMinimoCompra) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal desconto = valorCompra.multiply(percentualDesconto).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        
        if (valorDescontoMaximo != null && desconto.compareTo(valorDescontoMaximo) > 0) {
            desconto = valorDescontoMaximo;
        }

        return desconto;
    }

    public void utilizarPromocao() {
        if (ehValida()) {
            this.utilizacoesAtuais++;
        }
    }
}
```

### 10. HorarioFuncionamento.java
Horários de funcionamento dos mercados.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "horarios_funcionamento", indexes = {
    @Index(name = "idx_horario_mercado", columnList = "mercado_id"),
    @Index(name = "idx_horario_dia_semana", columnList = "dia_semana")
},
uniqueConstraints = @UniqueConstraint(name = "uk_mercado_dia", columnNames = {"mercado_id", "dia_semana"}))
public class HorarioFuncionamento extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public enum DiaSemana {
        SEGUNDA(1, "Segunda-feira"),
        TERCA(2, "Terça-feira"),
        QUARTA(3, "Quarta-feira"),
        QUINTA(4, "Quinta-feira"),
        SEXTA(5, "Sexta-feira"),
        SABADO(6, "Sábado"),
        DOMINGO(7, "Domingo");

        private final int numero;
        private final String descricao;

        DiaSemana(int numero, String descricao) {
            this.numero = numero;
            this.descricao = descricao;
        }

        public int getNumero() {
            return numero;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @NotNull(message = "O mercado é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_horario_mercado"))
    private Mercado mercado;

    @NotNull(message = "O dia da semana é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 20)
    private DiaSemana diaSemana;

    @NotNull(message = "A hora de abertura é obrigatória")
    @Column(name = "hora_abertura", nullable = false)
    private LocalTime horaAbertura;

    @NotNull(message = "A hora de fechamento é obrigatória")
    @Column(name = "hora_fechamento", nullable = false)
    private LocalTime horaFechamento;

    @Column(name = "aberto", nullable = false)
    private Boolean aberto = true;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    public boolean ehDiaFuncionamento(java.time.DayOfWeek dayOfWeek) {
        return this.diaSemana.getNumero() == dayOfWeek.getValue();
    }

    public boolean estahAberto() {
        if (!this.aberto) {
            return false;
        }
        
        LocalTime agora = LocalTime.now();
        return !agora.isBefore(horaAbertura) && agora.isBefore(horaFechamento);
    }

    public long minutosAteAbertura() {
        LocalTime agora = LocalTime.now();
        if (estahAberto()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(agora, horaAbertura);
    }
}
```

---

## Entidades de Segurança

### 11. RefreshToken.java
Tokens para renovação de autenticação.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token_usuario", columnList = "usuario_id"),
    @Index(name = "idx_refresh_token_token", columnList = "token", unique = true),
    @Index(name = "idx_refresh_token_expiracao", columnList = "data_expiracao")
})
public class RefreshToken extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_usuario"))
    private User usuario;

    @Column(name = "data_expiracao", nullable = false)
    private Instant dataExpiracao;

    @Column(name = "revogado", nullable = false)
    private Boolean revogado = false;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    public boolean ehValido() {
        return !revogado && Instant.now().isBefore(dataExpiracao);
    }

    public void revogar() {
        this.revogado = true;
    }
}
```

### 12. AuditLog.java
Logs de auditoria das operações.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_usuario", columnList = "usuario_id"),
    @Index(name = "idx_audit_acao", columnList = "acao"),
    @Index(name = "idx_audit_entidade", columnList = "tipo_entidade"),
    @Index(name = "idx_audit_criada_em", columnList = "created_at")
})
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public enum TipoAcao {
        CRIACAO, ATUALIZACAO, DELECAO, VISUALIZACAO, LOGIN, LOGOUT, ERRO
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_audit_usuario"))
    private User usuario;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "acao", nullable = false, length = 50)
    private TipoAcao acao;

    @NotBlank
    @Column(name = "tipo_entidade", nullable = false, length = 100)
    private String tipoEntidade;

    @Column(name = "id_entidade")
    private Long idEntidade;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "valores_anteriores", columnDefinition = "TEXT")
    private String valoresAnteriores;

    @Column(name = "valores_novos", columnDefinition = "TEXT")
    private String valoresNovos;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "status_http")
    private Integer statusHttp;
}
```

### 13. TwoFactorCode.java
Códigos para autenticação de dois fatores.

```java
package com.netflix.mercados.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "two_factor_codes", indexes = {
    @Index(name = "idx_2fa_usuario", columnList = "usuario_id"),
    @Index(name = "idx_2fa_codigo", columnList = "codigo", unique = true),
    @Index(name = "idx_2fa_expiracao", columnList = "data_expiracao")
})
public class TwoFactorCode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O código é obrigatório")
    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_2fa_usuario"))
    private User usuario;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(name = "utilizado", nullable = false)
    private Boolean utilizado = false;

    @Column(name = "data_utilizacao")
    private LocalDateTime dataUtilizacao;

    @Column(name = "metodo_envio", nullable = false, length = 20)
    private String metodoEnvio; // SMS, EMAIL

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "max_tentativas", nullable = false)
    private Integer maxTentativas = 3;

    public boolean ehValido() {
        return !utilizado && 
               LocalDateTime.now().isBefore(dataExpiracao) && 
               tentativas < maxTentativas;
    }

    public void marcarUtilizado() {
        this.utilizado = true;
        this.dataUtilizacao = LocalDateTime.now();
    }

    public void incrementarTentativas() {
        this.tentativas++;
    }

    public boolean podeReusar() {
        return this.tentativas < this.maxTentativas;
    }
}
```

---

## Repositórios

### Repository Configuration
Todas os repositórios estendem `JpaRepository` com suporte a projeções, especificações e queries customizadas.

### 1. RoleRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r WHERE r.active = true ORDER BY r.name")
    List<Role> findAllActive();

    boolean existsByName(String name);
}
```

### 2. UserRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmailActive(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.deleted = false")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true AND u.deleted = false")
    long countActiveUsers();

    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.deleted = false ORDER BY u.createdAt DESC")
    List<User> findUnverifiedUsers();

    @Query("SELECT u FROM User u WHERE u.twoFactorEnabled = true AND u.active = true AND u.deleted = false")
    List<User> findUsersWithTwoFactor();

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);

    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true WHERE u.id = :userId")
    void verifyEmail(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
           "WHERE u.email = :email AND u.id != :userId AND u.deleted = false")
    boolean existsOtherUserWithEmail(@Param("email") String email, @Param("userId") Long userId);
}
```

### 3. MercadoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Mercado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long> {

    Optional<Mercado> findByCnpj(String cnpj);

    Optional<Mercado> findByEmail(String email);

    @Query("SELECT m FROM Mercado m WHERE m.ativo = true AND m.deleted = false")
    Page<Mercado> findAllAtivos(Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.ativo = true AND m.deleted = false " +
           "ORDER BY m.avaliacaoMedia DESC")
    Page<Mercado> findTopRated(Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.cidade = :cidade AND m.ativo = true AND m.deleted = false " +
           "ORDER BY m.avaliacaoMedia DESC")
    Page<Mercado> findByCity(@Param("cidade") String cidade, Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.estado = :estado AND m.ativo = true AND m.deleted = false")
    List<Mercado> findByState(@Param("estado") String estado);

    @Query(value = "SELECT m.*, " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(m.latitude)) * " +
           "cos(radians(m.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(m.latitude)))) AS distancia " +
           "FROM mercados m " +
           "WHERE m.ativo = true AND m.deleted = false " +
           "HAVING distancia <= :raioKm " +
           "ORDER BY distancia ASC",
           nativeQuery = true)
    List<Mercado> findNearby(@Param("latitude") BigDecimal latitude,
                            @Param("longitude") BigDecimal longitude,
                            @Param("raioKm") Double raioKm);

    @Query(value = "SELECT m.*, " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(m.latitude)) * " +
           "cos(radians(m.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(m.latitude)))) AS distancia " +
           "FROM mercados m " +
           "WHERE m.ativo = true AND m.deleted = false " +
           "HAVING distancia <= :raioKm " +
           "ORDER BY m.avaliacao_media DESC, distancia ASC",
           nativeQuery = true)
    Page<Mercado> findNearbyWithPagination(@Param("latitude") BigDecimal latitude,
                                          @Param("longitude") BigDecimal longitude,
                                          @Param("raioKm") Double raioKm,
                                          Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.nome LIKE CONCAT('%', :termo, '%') " +
           "AND m.ativo = true AND m.deleted = false")
    Page<Mercado> searchByName(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT m FROM Mercado m WHERE m.avaliacaoMedia >= :minAvaliacao " +
           "AND m.ativo = true AND m.deleted = false " +
           "ORDER BY m.avaliacaoMedia DESC")
    Page<Mercado> findByMinRating(@Param("minAvaliacao") BigDecimal minAvaliacao, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Mercado m WHERE m.ativo = true AND m.deleted = false")
    long countAtivos();

    @Query("SELECT m FROM Mercado m WHERE m.totalAvaliacoes > 50 AND m.ativo = true AND m.deleted = false " +
           "ORDER BY m.avaliacaoMedia DESC")
    Page<Mercado> findVerifiedStores(Pageable pageable);
}
```

### 4. AvaliacaoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado.id = :mercadoId " +
           "AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<Avaliacao> findByMercado(@Param("mercadoId") Long mercadoId, Pageable pageable);

    @Query("SELECT a FROM Avaliacao a WHERE a.user.id = :userId " +
           "AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<Avaliacao> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado.id = :mercadoId " +
           "AND a.estrelas = :estrelas AND a.deleted = false")
    List<Avaliacao> findByMercadoAndEstrelas(@Param("mercadoId") Long mercadoId,
                                            @Param("estrelas") Integer estrelas);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.mercado.id = :mercadoId " +
           "AND a.deleted = false")
    long countByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT AVG(a.estrelas) FROM Avaliacao a WHERE a.mercado.id = :mercadoId " +
           "AND a.deleted = false")
    Double findAverageRatingByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado.id = :mercadoId " +
           "AND a.verificado = true AND a.deleted = false " +
           "ORDER BY a.uteis DESC, a.createdAt DESC")
    Page<Avaliacao> findVerifiedByMercado(@Param("mercadoId") Long mercadoId, Pageable pageable);

    @Query("SELECT a FROM Avaliacao a WHERE a.createdAt >= :dataInicio " +
           "AND a.deleted = false ORDER BY a.createdAt DESC")
    List<Avaliacao> findRecentReviews(@Param("dataInicio") LocalDateTime dataInicio);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Avaliacao a " +
           "WHERE a.user.id = :userId AND a.mercado.id = :mercadoId AND a.deleted = false")
    boolean existsByUserAndMercado(@Param("userId") Long userId, @Param("mercadoId") Long mercadoId);
}
```

### 5. ComentarioRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query("SELECT c FROM Comentario c WHERE c.avaliacao.id = :avaliacaoId " +
           "AND c.comentarioPai IS NULL AND c.deleted = false " +
           "ORDER BY c.curtidas DESC, c.createdAt DESC")
    Page<Comentario> findByAvaliacao(@Param("avaliacaoId") Long avaliacaoId, Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.comentarioPai.id = :comentarioPaiId " +
           "AND c.deleted = false ORDER BY c.createdAt ASC")
    List<Comentario> findRespostas(@Param("comentarioPaiId") Long comentarioPaiId);

    @Query("SELECT c FROM Comentario c WHERE c.usuario.id = :usuarioId " +
           "AND c.deleted = false ORDER BY c.createdAt DESC")
    Page<Comentario> findByUsuario(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT c FROM Comentario c WHERE c.moderado = false " +
           "AND c.deleted = false ORDER BY c.createdAt ASC")
    Page<Comentario> findCommentsPendingModeration(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.avaliacao.id = :avaliacaoId " +
           "AND c.comentarioPai IS NULL AND c.deleted = false")
    long countByAvaliacao(@Param("avaliacaoId") Long avaliacaoId);
}
```

### 6. FavoritoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Favorito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    @Query("SELECT f FROM Favorito f WHERE f.user.id = :userId " +
           "AND f.deleted = false ORDER BY f.prioridade DESC, f.createdAt DESC")
    Page<Favorito> findByUsuario(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f FROM Favorito f WHERE f.mercado.id = :mercadoId " +
           "AND f.deleted = false")
    Page<Favorito> findByMercado(@Param("mercadoId") Long mercadoId, Pageable pageable);

    @Query("SELECT f FROM Favorito f WHERE f.user.id = :userId " +
           "AND f.mercado.id = :mercadoId AND f.deleted = false")
    Optional<Favorito> findByUsuarioAndMercado(@Param("userId") Long userId,
                                              @Param("mercadoId") Long mercadoId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorito f " +
           "WHERE f.user.id = :userId AND f.mercado.id = :mercadoId AND f.deleted = false")
    boolean isFavorito(@Param("userId") Long userId, @Param("mercadoId") Long mercadoId);

    @Query("SELECT COUNT(f) FROM Favorito f WHERE f.mercado.id = :mercadoId " +
           "AND f.deleted = false")
    long countByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT COUNT(f) FROM Favorito f WHERE f.user.id = :userId " +
           "AND f.deleted = false")
    long countByUsuario(@Param("userId") Long userId);
}
```

### 7. NotificacaoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    @Query("SELECT n FROM Notificacao n WHERE n.usuario.id = :usuarioId " +
           "AND n.deleted = false ORDER BY n.createdAt DESC")
    Page<Notificacao> findByUsuario(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT n FROM Notificacao n WHERE n.usuario.id = :usuarioId " +
           "AND n.lida = false AND n.deleted = false ORDER BY n.createdAt DESC")
    Page<Notificacao> findNotReadByUsuario(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT n FROM Notificacao n WHERE n.usuario.id = :usuarioId " +
           "AND n.tipo = :tipo AND n.deleted = false ORDER BY n.createdAt DESC")
    Page<Notificacao> findByUsuarioAndTipo(@Param("usuarioId") Long usuarioId,
                                          @Param("tipo") Notificacao.TipoNotificacao tipo,
                                          Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.usuario.id = :usuarioId " +
           "AND n.lida = false AND n.deleted = false")
    long countUnreadByUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.usuario.id = :usuarioId " +
           "AND n.lida = false AND n.deleted = false")
    void markAllAsReadByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT n FROM Notificacao n WHERE n.createdAt < :data " +
           "AND n.lida = true AND n.deleted = false")
    List<Notificacao> findOldReadNotifications(@Param("data") LocalDateTime data);
}
```

### 8. PromocaoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.Promocao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    Optional<Promocao> findByCodigo(String codigo);

    @Query("SELECT p FROM Promocao p WHERE p.mercado.id = :mercadoId " +
           "AND p.deleted = false ORDER BY p.dataValidade DESC")
    Page<Promocao> findByMercado(@Param("mercadoId") Long mercadoId, Pageable pageable);

    @Query("SELECT p FROM Promocao p WHERE p.mercado.id = :mercadoId " +
           "AND p.ativa = true AND p.dataValidade > CURRENT_TIMESTAMP " +
           "AND (p.dataInicio IS NULL OR p.dataInicio <= CURRENT_TIMESTAMP) " +
           "AND p.deleted = false ORDER BY p.dataValidade ASC")
    List<Promocao> findActiveByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT p FROM Promocao p WHERE p.ativa = true " +
           "AND p.dataValidade > CURRENT_TIMESTAMP " +
           "AND (p.dataInicio IS NULL OR p.dataInicio <= CURRENT_TIMESTAMP) " +
           "AND p.deleted = false ORDER BY p.dataValidade ASC")
    Page<Promocao> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Promocao p WHERE p.dataValidade < CURRENT_TIMESTAMP " +
           "AND p.ativa = true AND p.deleted = false")
    List<Promocao> findExpiredPromocoes();

    @Query("SELECT COUNT(p) FROM Promocao p WHERE p.mercado.id = :mercadoId " +
           "AND p.ativa = true AND p.deleted = false")
    long countActiveByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT p FROM Promocao p WHERE p.percentualDesconto >= :minDesconto " +
           "AND p.ativa = true AND p.dataValidade > CURRENT_TIMESTAMP " +
           "AND p.deleted = false ORDER BY p.percentualDesconto DESC")
    Page<Promocao> findByMinDiscount(@Param("minDesconto") java.math.BigDecimal minDesconto,
                                     Pageable pageable);
}
```

### 9. HorarioFuncionamentoRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.HorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {

    @Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado.id = :mercadoId " +
           "AND h.deleted = false ORDER BY h.diaSemana")
    List<HorarioFuncionamento> findByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado.id = :mercadoId " +
           "AND h.diaSemana = :diaSemana AND h.deleted = false")
    Optional<HorarioFuncionamento> findByMercadoAndDia(@Param("mercadoId") Long mercadoId,
                                                       @Param("diaSemana") HorarioFuncionamento.DiaSemana diaSemana);

    @Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado.id = :mercadoId " +
           "AND h.aberto = true AND h.deleted = false")
    List<HorarioFuncionamento> findOperatingHoursByMercado(@Param("mercadoId") Long mercadoId);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HorarioFuncionamento h " +
           "WHERE h.mercado.id = :mercadoId AND h.aberto = false AND h.deleted = false")
    boolean hasClosedDays(@Param("mercadoId") Long mercadoId);
}
```

### 10. RefreshTokenRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.usuario.id = :usuarioId " +
           "AND rt.revogado = false AND rt.dataExpiracao > CURRENT_TIMESTAMP " +
           "AND rt.deleted = false")
    List<RefreshToken> findValidTokensByUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revogado = true WHERE rt.usuario.id = :usuarioId " +
           "AND rt.deleted = false")
    void revokeAllTokensByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.dataExpiracao < CURRENT_TIMESTAMP " +
           "AND rt.deleted = false")
    List<RefreshToken> findExpiredTokens();

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.dataExpiracao < :data AND rt.deleted = false")
    void deleteExpiredTokens(@Param("data") Instant data);

    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.usuario.id = :usuarioId " +
           "AND rt.revogado = false AND rt.deleted = false")
    long countValidTokensByUsuario(@Param("usuarioId") Long usuarioId);
}
```

### 11. AuditLogRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT al FROM AuditLog al WHERE al.usuario.id = :usuarioId " +
           "AND al.deleted = false ORDER BY al.createdAt DESC")
    Page<AuditLog> findByUsuario(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.tipoEntidade = :tipoEntidade " +
           "AND al.idEntidade = :idEntidade AND al.deleted = false " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> findByEntidade(@Param("tipoEntidade") String tipoEntidade,
                                 @Param("idEntidade") Long idEntidade,
                                 Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.acao = :acao AND al.deleted = false " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> findByAcao(@Param("acao") AuditLog.TipoAcao acao, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.createdAt >= :dataInicio " +
           "AND al.createdAt <= :dataFim AND al.deleted = false " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT al FROM AuditLog al WHERE al.statusHttp >= 400 " +
           "AND al.deleted = false ORDER BY al.createdAt DESC")
    Page<AuditLog> findErrors(Pageable pageable);

    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.usuario.id = :usuarioId " +
           "AND al.acao = :acao AND al.deleted = false")
    long countActionsByUsuario(@Param("usuarioId") Long usuarioId, @Param("acao") AuditLog.TipoAcao acao);
}
```

### 12. TwoFactorCodeRepository.java

```java
package com.netflix.mercados.repository;

import com.netflix.mercados.entity.TwoFactorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {

    Optional<TwoFactorCode> findByCodigo(String codigo);

    @Query("SELECT tfc FROM TwoFactorCode tfc WHERE tfc.usuario.id = :usuarioId " +
           "AND tfc.utilizado = false AND tfc.dataExpiracao > CURRENT_TIMESTAMP " +
           "AND tfc.deleted = false ORDER BY tfc.createdAt DESC LIMIT 1")
    Optional<TwoFactorCode> findValidCodeByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT tfc FROM TwoFactorCode tfc WHERE tfc.usuario.id = :usuarioId " +
           "AND tfc.dataExpiracao < CURRENT_TIMESTAMP AND tfc.deleted = false")
    List<TwoFactorCode> findExpiredCodesByUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("DELETE FROM TwoFactorCode tfc WHERE tfc.dataExpiracao < :data " +
           "AND tfc.deleted = false")
    void deleteExpiredCodes(@Param("data") LocalDateTime data);

    @Query("SELECT COUNT(tfc) FROM TwoFactorCode tfc WHERE tfc.usuario.id = :usuarioId " +
           "AND tfc.utilizado = false AND tfc.dataExpiracao > CURRENT_TIMESTAMP " +
           "AND tfc.deleted = false")
    long countValidCodesByUsuario(@Param("usuarioId") Long usuarioId);
}
```

---

## Configuração Adicional JPA

### Propriedades application.yml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
        show_sql: false
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        hibernate.enable_lazy_load_no_trans: true
    open-in-view: false
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

### Índices de Performance
Os índices foram adicionados às anotações `@Table` das entidades:
- **Chaves primárias**: Automáticas
- **Chaves estrangeiras**: Criadas automaticamente
- **Soft delete**: `idx_*_deleted` implícitos nas queries
- **Busca por proximidade**: `idx_mercado_coordenadas` para queries Haversine
- **Filtros comuns**: Email, CPF, CNPJ, Estados, Cidades
- **Ordenação**: Timestamps para sort eficiente

### Versioning e Optimistic Locking
Todas as entidades possuem `@Version` para controle de concorrência.

### Soft Delete
Implementado através de `@SoftDelete` do Hibernate e filtros nas queries JPQL.

---

## Boas Práticas Aplicadas

✅ Java 21+
✅ Lombok para reduzir boilerplate
✅ Serializable em todas as entidades
✅ Validações JSR-380 (@NotNull, @NotBlank, etc)
✅ Relacionamentos bem definidos (OneToMany, ManyToOne, ManyToMany)
✅ Orphan removal para cascata correta
✅ Named queries para performance
✅ Índices compostos para buscas eficientes
✅ Soft delete com versionamento
✅ Auditoria via BaseEntity
✅ Haversine formula para geolocalização
✅ Derived Queries e @Query customizadas
✅ Paginação em repositórios
✅ Fetch type otimizado (LAZY por padrão)
