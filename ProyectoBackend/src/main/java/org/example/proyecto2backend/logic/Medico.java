package org.example.proyecto2backend.logic;

import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.ColumnDefault;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "medico")
    private Set<Horario> horarios = new LinkedHashSet<>();

    public Set<Horario> getHorarios() {
        return horarios;
    }
    public String getId() {
        return id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public String getEspecialidad() {
        return especialidad;
    }
    public BigDecimal getCosto() {
        return costo;
    }
    public String getLocalidad() {
        return localidad;
    }
    public Integer getFrecuenciaCitas() { return frecuenciaCitas; }
    public String getStatus() {
        return status;
    }

    public void setHorarios(Set<Horario> horarios) {
        this.horarios = horarios;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public void setFrecuenciaCitas(Integer frecuenciaCitas) {
        this.frecuenciaCitas = frecuenciaCitas;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @PrePersist
    public void setDefaultStatus() {
        if (this.status == null) { this.status = "Pendiente"; }
    }

    public Map<LocalDate, List<String>> getFechas(int semanaOffset) {
        Map<LocalDate, List<String>> disponibilidad = new TreeMap<>();
        LocalDate fechaBase = LocalDate.now().plusWeeks(semanaOffset);

        for (Horario horario : horarios) {
            DayOfWeek diaSemana = convertirDiaSemana(horario.getDia());
            LocalDate proximaFecha = obtenerProximaFecha(fechaBase, diaSemana);
            List<String> horariosGenerados = generarHorarios(horario);
            disponibilidad.put(proximaFecha, horariosGenerados);
        }
        return disponibilidad;
    }

    private DayOfWeek convertirDiaSemana(String dia) {
        return switch (dia.toLowerCase()) {
            case "lunes" -> DayOfWeek.MONDAY;
            case "martes" -> DayOfWeek.TUESDAY;
            case "miércoles" -> DayOfWeek.WEDNESDAY;
            case "jueves" -> DayOfWeek.THURSDAY;
            case "viernes" -> DayOfWeek.FRIDAY;
            case "sábado" -> DayOfWeek.SATURDAY;
            case "domingo" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Día inválido: " + dia);
        };
    }

    private LocalDate obtenerProximaFecha(LocalDate desde, DayOfWeek diaSemana) {
        int diasParaSumar = (diaSemana.getValue() - desde.getDayOfWeek().getValue() + 7) % 7;
        return desde.plusDays(diasParaSumar == 0 ? 7 : diasParaSumar);
    }

    private List<String> generarHorarios(Horario horario) {
        List<String> horariosGenerados = new ArrayList<>();
        LocalTime inicio = horario.getHoraInicio();
        LocalTime fin = horario.getHoraFin();
        int frecuencia = this.frecuenciaCitas;

        while (inicio.isBefore(fin)) {
            horariosGenerados.add(inicio.toString());
            inicio = inicio.plusMinutes(frecuencia);
        }
        return horariosGenerados;
    }
}