package org.example.proyecto2backend.logic.DTOs;

import java.math.BigDecimal;

public record MedicoDTOGestion(
        String id,
        String nombre,
        String especialidad,
        BigDecimal costo,
        String localidad,
        Integer frecuenciaCitas,
        String status
) { }