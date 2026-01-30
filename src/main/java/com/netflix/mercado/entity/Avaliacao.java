package com.netflix.mercado.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "avaliacoes", indexes = {
        @Index(name = "idx_avaliacao_mercado", columnList = "mercado_id"),
        @Index(name = "idx_avaliacao_usuario", columnList = "usuario_id"),
        @Index(name = "idx_avaliacao_estrelas", columnList = "estrelas"),
        @Index(name = "idx_avaliacao_criada_em", columnList = "created_at")
})
public class Avaliacao extends BaseEntity {

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

    @OneToMany(mappedBy = "avaliacao", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();

    public Double calcularUtilidade() {
        long total = uteis + inutils;
        if (total == 0) {
            return 0.0;
        }
        return (double) uteis / total * 100;
    }
}
