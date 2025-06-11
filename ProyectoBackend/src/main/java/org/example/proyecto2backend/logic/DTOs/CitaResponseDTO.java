package org.example.proyecto2backend.logic.DTOs;

import org.example.proyecto2backend.logic.Cita;
import java.time.LocalDate;
import java.time.LocalTime;

public record CitaResponseDTO(
        Integer id,
        String medicoNombre,
        String pacienteNombre,
        LocalDate fecha,
        LocalTime hora,
        String status,
        String notas
) {
    public CitaResponseDTO(Cita cita) {
        this(
                cita.getId(),
                cita.getMedico().getUsuario().getNombre(),
                cita.getUsuario().getNombre(),
                cita.getFecha(),
                cita.getHora(),
                cita.getStatus(),
                cita.getNotas()
        );
    }
}