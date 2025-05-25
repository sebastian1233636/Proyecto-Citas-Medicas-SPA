package org.example.proyecto2backend.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "medico")
public class Medico {
    @Id
    @Size(max = 100)
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private Usuario usuario;

    @Size(max = 100)
    @NotNull
    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;

    @NotNull
    @Column(name = "costo", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Size(max = 100)
    @NotNull
    @Column(name = "localidad", nullable = false, length = 100)
    private String localidad;

    @NotNull
    @Column(name = "frecuencia_citas", nullable = false)
    private Integer frecuenciaCitas;

    @NotNull
    @ColumnDefault("'Pendiente'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Integer getFrecuenciaCitas() {
        return frecuenciaCitas;
    }

    public void setFrecuenciaCitas(Integer frecuenciaCitas) {
        this.frecuenciaCitas = frecuenciaCitas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}