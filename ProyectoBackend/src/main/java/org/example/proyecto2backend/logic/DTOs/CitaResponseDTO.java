package org.example.proyecto2backend.logic.DTOs;

import org.example.proyecto2backend.logic.Cita;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaResponseDTO(
        Integer id,
        String medicoNombre,
        LocalDate fecha,
        LocalTime hora,
        String status,
        String notas
) {
    public CitaResponseDTO(Cita c) {
        this(
                c.getId(),
                c.getMedico().getUsuario().getNombre(),
                c.getFecha(),
                c.getHora(),
                c.getStatus(),
                c.getNotas()
        );
    }
}
