package org.example.proyecto2backend.logic;

import org.hibernate.annotations.OnDeleteAction;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horario")
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @NotNull
    @Lob
    @Column(name = "dia", nullable = false)
    private String dia;

    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    public Integer getId() {
        return id;
    }
    public Medico getMedico() {
        return medico;
    }
    public String getDia() {
        return dia;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public void setDia(String dia) {
        this.dia = dia;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}