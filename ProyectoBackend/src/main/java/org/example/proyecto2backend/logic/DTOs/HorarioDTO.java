package org.example.proyecto2backend.logic.DTOs;

public record HorarioDTO(
        Integer horarioId,
        String dia,
        java.time.LocalTime horaInicio,
        java.time.LocalTime horaFin
) { }