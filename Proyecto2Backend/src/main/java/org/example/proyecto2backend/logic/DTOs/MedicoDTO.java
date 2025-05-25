package org.example.proyecto2backend.logic.DTOs;

import java.math.BigDecimal;

public record MedicoDTO(
        String especialidad,
        BigDecimal costo,
        String localidad,
        Integer frecuenciaCitas
) {
}
