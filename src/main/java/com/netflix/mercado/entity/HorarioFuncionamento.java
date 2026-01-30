package com.netflix.mercado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "horarios_funcionamento", indexes = {
        @Index(name = "idx_horario_mercado", columnList = "mercado_id"),
        @Index(name = "idx_horario_dia_semana", columnList = "dia_semana")
}, uniqueConstraints = @UniqueConstraint(name = "uk_mercado_dia", columnNames = {"mercado_id", "dia_semana"}))
public class HorarioFuncionamento extends BaseEntity {

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

    public boolean ehDiaFuncionamento(DayOfWeek dayOfWeek) {
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
        return ChronoUnit.MINUTES.between(agora, horaAbertura);
    }
    public HorarioFuncionamento() {
    }

    public HorarioFuncionamento(final int numero, final String descricao, Mercado mercado, DiaSemana diaSemana, LocalTime horaAbertura, LocalTime horaFechamento, Boolean aberto, String observacoes) {
        this.numero = numero;
        this.descricao = descricao;
        this.mercado = mercado;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.aberto = aberto;
        this.observacoes = observacoes;
    }

    public final int getNumero() {
        return this.numero;
    }

    public void setNumero(final int numero) {
        this.numero = numero;
    }

    public final String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public Mercado getMercado() {
        return this.mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public DiaSemana getDiaSemana() {
        return this.diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraAbertura() {
        return this.horaAbertura;
    }

    public void setHoraAbertura(LocalTime horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public LocalTime getHoraFechamento() {
        return this.horaFechamento;
    }

    public void setHoraFechamento(LocalTime horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    public Boolean getAberto() {
        return this.aberto;
    }

    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }

    public String getObservacoes() {
        return this.observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}
