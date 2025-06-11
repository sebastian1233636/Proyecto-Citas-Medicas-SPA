package org.example.proyecto2backend.logic;

import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.ColumnDefault;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "cita")
public class Cita {
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @NotNull
    @ColumnDefault("'Pendiente'")
    @Column(name = "status", nullable = false)
    private String status;

    @Lob
    @Column(name = "notas")
    private String notas;

    public Integer getId() {
        return id;
    }
    public Medico getMedico() {
        return medico;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHora() {
        return hora;
    }
    public String getStatus() {
        return status;
    }
    public String getNotas() {
        return notas;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setNotas(String notas) {
        this.notas = notas;
    }
}