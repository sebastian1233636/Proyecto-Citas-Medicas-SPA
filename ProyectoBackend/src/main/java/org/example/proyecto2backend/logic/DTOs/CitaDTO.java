package org.example.proyecto2backend.logic.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaDTO(
        Integer id,
        LocalDate fecha,
        LocalTime hora,
        String status,
        String nombrePaciente,
        String nombreMedico,
        String notas
) { }