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

    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    @Size(min = 5, max = 1000, message = "O comentário deve ter entre 5 e 1000 caracteres")
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "avaliacao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comentario_avaliacao"))
    private Avaliacao avaliacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comentario_usuario"))
    private User user;

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
